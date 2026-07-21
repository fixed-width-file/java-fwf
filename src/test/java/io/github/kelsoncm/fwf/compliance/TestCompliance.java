package io.github.kelsoncm.fwf.compliance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kelsoncm.fwf.columns.*;
import io.github.kelsoncm.fwf.descriptors.*;
import io.github.kelsoncm.fwf.readers.Reader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestCompliance {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static Path complianceDir;

    @BeforeAll
    static void setUpClass() throws IOException {
        Path rootPath = Path.of("").toAbsolutePath();
        Path siblingCompliance = rootPath.getParent().resolve("fwf-compliance-tests");

        if (Files.exists(siblingCompliance.resolve("manifest.json"))) {
            complianceDir = siblingCompliance;
        } else {
            complianceDir = rootPath.resolve("src/test/resources/fwf-compliance");
        }

        assertTrue(Files.exists(complianceDir.resolve("manifest.json")), "Compliance manifest.json not found at: " + complianceDir);
    }

    @Test
    void testAllComplianceCases() throws IOException {
        Path manifestPath = complianceDir.resolve("manifest.json");
        Map<String, Object> manifest = MAPPER.readValue(manifestPath.toFile(), new TypeReference<>() {});

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cases = (List<Map<String, Object>>) manifest.get("cases");
        assertNotNull(cases, "Cases array in manifest.json cannot be null");
        assertFalse(cases.isEmpty(), "No cases found in manifest.json");

        for (Map<String, Object> caseItem : cases) {
            String caseId = (String) caseItem.get("id");
            String caseRelPath = (String) caseItem.get("path");
            Path caseDir = complianceDir.resolve(caseRelPath);

            Path descriptorPath = caseDir.resolve("descriptor.json");
            Path inputPath = caseDir.resolve("input.fwf");
            Path expectedPath = caseDir.resolve("expected.json");

            assertTrue(Files.exists(descriptorPath), "Missing descriptor.json for case: " + caseId);
            assertTrue(Files.exists(inputPath), "Missing input.fwf for case: " + caseId);
            assertTrue(Files.exists(expectedPath), "Missing expected.json for case: " + caseId);

            Map<String, Object> descData = MAPPER.readValue(descriptorPath.toFile(), new TypeReference<>() {});
            List<Map<String, Object>> expectedData = MAPPER.readValue(expectedPath.toFile(), new TypeReference<>() {});
            String inputContent = Files.readString(inputPath);

            FileDescriptor fileDescriptor = buildFileDescriptor(descData);

            List<String> rawLines = Arrays.asList(inputContent.replace("\r\n", "\n").split("\n"));
            List<String> formattedLines = new ArrayList<>();
            int lineSize = fileDescriptor.getLineSize();
            for (String line : rawLines) {
                if (line.length() < lineSize) {
                    line = String.format("%-" + lineSize + "s", line);
                }
                formattedLines.add(line + "\n");
            }

            Reader reader = new Reader(formattedLines, fileDescriptor, "\n");
            List<Map<String, Object>> parsedRows = new ArrayList<>();
            for (Map<String, Object> row : reader) {
                parsedRows.add(serializeRow(row));
            }

            assertEquals(expectedData, parsedRows, "Compliance test failed for case: '" + caseId + "'");
        }
    }

    private static AbstractColumn buildColumn(Map<String, Object> colDef) {
        String cType = (String) colDef.get("type");
        String name = (String) colDef.get("name");
        String desc = colDef.containsKey("description") ? (String) colDef.get("description") : name;
        int size = colDef.containsKey("size") ? ((Number) colDef.get("size")).intValue() : 0;

        return switch (cType) {
            case "char" -> new CharColumn(name, size, desc);
            case "right_char" -> new RightCharColumn(name, size, desc);
            case "positive_integer" -> new PositiveIntegerColumn(name, size, desc);
            case "positive_decimal" -> {
                int decimals = colDef.containsKey("decimals") ? ((Number) colDef.get("decimals")).intValue() : 2;
                yield new PositiveDecimalColumn(name, size, decimals, desc);
            }
            case "date" -> {
                String fmt = colDef.containsKey("format") ? (String) colDef.get("format") : "%d%m%Y";
                yield new DateColumn(name, fmt, desc);
            }
            case "time" -> {
                String fmt = colDef.containsKey("format") ? (String) colDef.get("format") : "%H%M";
                yield new TimeColumn(name, fmt, desc);
            }
            case "datetime" -> {
                String fmt = colDef.containsKey("format") ? (String) colDef.get("format") : "%d%m%Y%H%M";
                yield new DateTimeColumn(name, fmt, desc);
            }
            default -> throw new IllegalArgumentException("Unknown column type: " + cType);
        };
    }

    @SuppressWarnings("unchecked")
    private static RowDescriptor buildRowDescriptor(Class<? extends RowDescriptor> clazz, Map<String, Object> rowDef) {
        List<Map<String, Object>> colDefs = (List<Map<String, Object>>) rowDef.get("columns");
        List<AbstractColumn> cols = new ArrayList<>();
        for (Map<String, Object> cd : colDefs) {
            cols.add(buildColumn(cd));
        }

        if (clazz == HeaderRowDescriptor.class) return new HeaderRowDescriptor(cols);
        if (clazz == FooterRowDescriptor.class) return new FooterRowDescriptor(cols);
        return new DetailRowDescriptor(cols);
    }

    @SuppressWarnings("unchecked")
    private static FileDescriptor buildFileDescriptor(Map<String, Object> descData) {
        HeaderRowDescriptor header = null;
        if (descData.containsKey("header") && descData.get("header") != null) {
            header = (HeaderRowDescriptor) buildRowDescriptor(HeaderRowDescriptor.class, (Map<String, Object>) descData.get("header"));
        }

        FooterRowDescriptor footer = null;
        if (descData.containsKey("footer") && descData.get("footer") != null) {
            footer = (FooterRowDescriptor) buildRowDescriptor(FooterRowDescriptor.class, (Map<String, Object>) descData.get("footer"));
        }

        List<Map<String, Object>> detailsData = (List<Map<String, Object>>) descData.get("details");
        List<DetailRowDescriptor> details = new ArrayList<>();
        for (Map<String, Object> d : detailsData) {
            details.add((DetailRowDescriptor) buildRowDescriptor(DetailRowDescriptor.class, d));
        }

        return new FileDescriptor(details, header, footer);
    }

    private static Map<String, Object> serializeRow(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            result.put(entry.getKey(), serializeValue(entry.getValue()));
        }
        return result;
    }

    private static Object serializeValue(Object val) {
        if (val instanceof LocalDate ld) {
            return ld.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (val instanceof LocalTime lt) {
            return lt.format(DateTimeFormatter.ISO_LOCAL_TIME);
        }
        if (val instanceof LocalDateTime ldt) {
            return ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return val;
    }
}
