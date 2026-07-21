package io.github.kelsoncm.fwf.readers;

import io.github.kelsoncm.fwf.columns.CharColumn;
import io.github.kelsoncm.fwf.columns.PositiveIntegerColumn;
import io.github.kelsoncm.fwf.descriptors.DetailRowDescriptor;
import io.github.kelsoncm.fwf.descriptors.FileDescriptor;
import io.github.kelsoncm.fwf.descriptors.FooterRowDescriptor;
import io.github.kelsoncm.fwf.descriptors.HeaderRowDescriptor;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TestReaders {

    @Test
    void testReaderValidations() {
        CharColumn c1 = new CharColumn("name", 10);
        PositiveIntegerColumn c2 = new PositiveIntegerColumn("age", 3);
        FileDescriptor fd = new FileDescriptor(List.of(new DetailRowDescriptor(List.of(c1, c2))));

        assertThrows(IllegalArgumentException.class, () -> new Reader(null, fd, "\n"));
        assertThrows(IllegalArgumentException.class, () -> new Reader("data", null, "\n"));
        assertThrows(IllegalArgumentException.class, () -> new Reader("data", fd, "invalid"));
        assertThrows(IllegalArgumentException.class, () -> new Reader(12345, fd, "\n"));
    }

    @Test
    void testReaderMismatchedLineLength() {
        CharColumn c1 = new CharColumn("name", 5);
        FileDescriptor fd = new FileDescriptor(List.of(new DetailRowDescriptor(List.of(c1))));

        assertThrows(IllegalArgumentException.class, () -> new Reader("123456\n", fd, "\n"));
    }

    @Test
    void testReaderParseStringContent() {
        CharColumn c1 = new CharColumn("name", 10);
        PositiveIntegerColumn c2 = new PositiveIntegerColumn("age", 3);

        HeaderRowDescriptor header = new HeaderRowDescriptor(List.of(new CharColumn("h", 13)));
        DetailRowDescriptor detail = new DetailRowDescriptor(List.of(c1, c2));
        FooterRowDescriptor footer = new FooterRowDescriptor(List.of(new CharColumn("f", 13)));

        FileDescriptor fd = new FileDescriptor(List.of(detail), header, footer);

        String content = "HEADER_TITLE_\nKELSON    045\nMARIA     030\nFOOTER_END___\n";

        Reader reader = new Reader(content, fd, "\n");
        assertEquals(4, reader.getLinesCount());

        List<Map<String, Object>> rows = new java.util.ArrayList<>();
        for (Map<String, Object> row : reader) {
            rows.add(row);
        }

        assertEquals(4, rows.size());
        assertEquals("HEADER_TITLE_", rows.get(0).get("h"));
        assertEquals("KELSON", rows.get(1).get("name"));
        assertEquals(45, rows.get(1).get("age"));
        assertEquals("MARIA", rows.get(2).get("name"));
        assertEquals(30, rows.get(2).get("age"));
        assertEquals("FOOTER_END___", rows.get(3).get("f"));

        assertThrows(NoSuchElementException.class, reader::next);
    }

    @Test
    void testReaderInputTypes() {
        CharColumn c1 = new CharColumn("name", 5);
        FileDescriptor fd = new FileDescriptor(List.of(new DetailRowDescriptor(List.of(c1))));

        String content = "HELLO\nWORLD\n";

        Reader r1 = new Reader(List.of("HELLO\n", "WORLD\n"), fd, "\n");
        assertEquals(2, r1.getLinesCount());

        Reader r2 = new Reader(new StringReader(content), fd, "\n");
        assertEquals(2, r2.getLinesCount());

        Reader r3 = new Reader(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), fd, "\n");
        assertEquals(2, r3.getLinesCount());

        Reader r3Reader = new Reader(new StringReader(content), fd, "\n");
        assertEquals(2, r3Reader.getLinesCount());

        assertThrows(IllegalArgumentException.class, () -> new Reader(null, fd, "\n"));

        String defaultContent = "HELLO\n\rWORLD\n\r";
        Reader r4 = new Reader(defaultContent, fd);
        assertEquals(0, r4.getLineNum());
        r4.next();
        assertEquals(1, r4.getLineNum());

        assertThrows(IllegalArgumentException.class, () -> new Reader(new Object(), fd, "\n"));

        InputStream failingIs = new InputStream() {
            @Override
            public int read() throws java.io.IOException {
                throw new java.io.IOException("Stream error");
            }
        };
        assertThrows(RuntimeException.class, () -> new Reader(failingIs, fd, "\n"));

        java.io.Reader failingReader = new java.io.Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) throws java.io.IOException {
                throw new java.io.IOException("Reader error");
            }
            @Override
            public void close() {}
        };
        assertThrows(RuntimeException.class, () -> new Reader(failingReader, fd, "\n"));
    }
}
