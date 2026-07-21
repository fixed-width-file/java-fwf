package io.github.kelsoncm.fwf.readers;

import io.github.kelsoncm.fwf.descriptors.FileDescriptor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Reader for fixed-width file content into structured maps.
 */
public class Reader implements Iterable<Map<String, Object>>, Iterator<Map<String, Object>> {

    private final List<String> lines;
    private final FileDescriptor fileDescriptor;
    private final String newline;
    private final int totalLinesCount;
    private int currentLineNum = 0;

    public Reader(Object iterable, FileDescriptor fileDescriptor, String newline) {
        if (iterable == null) {
            throw new IllegalArgumentException("O argumento _iterable tem que ser um Iterator");
        }
        if (fileDescriptor == null) {
            throw new IllegalArgumentException("O argumento file_descriptor tem que ser um FileDescriptor");
        }
        if (newline == null || (!newline.equals("\n") && !newline.equals("\r") && !newline.equals("\n\r"))) {
            throw new IllegalArgumentException("O argumento newline tem que ser uma str e conter \"\\n\", \"\\r\" ou \"\\n\\r\"");
        }

        this.fileDescriptor = fileDescriptor;
        this.newline = newline;

        String rawContent = loadRawContent(iterable);
        int filesize = rawContent.length();
        int expectedLineLength = fileDescriptor.getLineSize() + newline.length();

        if (filesize % expectedLineLength != 0) {
            String newlineRepr = newline.replace("\n", "\\n").replace("\r", "\\r");
            double linesRatio = (double) filesize / (double) expectedLineLength;
            throw new IllegalArgumentException(
                    String.format(Locale.US, "Algumas linha não tem o tamanho correto (%d) ou não tem a quebra de linha adequada (%s), total de bytes %d e total de linhas %f",
                            fileDescriptor.getLineSize(), newlineRepr, filesize, linesRatio)
            );
        }

        this.lines = splitLines(rawContent, newline);
        this.totalLinesCount = lines.size();
    }

    public Reader(Object iterable, FileDescriptor fileDescriptor) {
        this(iterable, fileDescriptor, "\n\r");
    }

    private static String loadRawContent(Object source) {
        if (source instanceof String strContent) {
            return strContent;
        } else if (source instanceof List<?> list) {
            StringBuilder sb = new StringBuilder();
            for (Object item : list) {
                if (item != null) sb.append(item.toString());
            }
            return sb.toString();
        } else if (source instanceof InputStream is) {
            try {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (source instanceof java.io.Reader r) {
            try {
                StringBuilder sb = new StringBuilder();
                char[] buf = new char[1024];
                int n;
                while ((n = r.read(buf)) != -1) {
                    sb.append(buf, 0, n);
                }
                return sb.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported Iterable");
        }
    }

    private static List<String> splitLines(String content, String newline) {
        List<String> result = new ArrayList<>();
        int step = newline.length();
        int len = content.length();
        int start = 0;
        while (start < len) {
            int idx = content.indexOf(newline, start);
            if (idx != -1) {
                result.add(content.substring(start, idx));
                start = idx + step;
            } else {
                result.add(content.substring(start));
                break;
            }
        }
        return result;
    }

    public int getLinesCount() {
        return totalLinesCount;
    }

    public int getLineNum() {
        return currentLineNum;
    }

    @Override
    public Iterator<Map<String, Object>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return currentLineNum < totalLinesCount;
    }

    @Override
    public Map<String, Object> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        String rowLine = lines.get(currentLineNum);
        currentLineNum++;

        if (fileDescriptor.getHeader() != null && currentLineNum == 1) {
            return fileDescriptor.getHeader().getValues(rowLine);
        } else if (fileDescriptor.getFooter() != null && currentLineNum == totalLinesCount) {
            return fileDescriptor.getFooter().getValues(rowLine);
        } else {
            return fileDescriptor.getDetails().get(0).getValues(rowLine);
        }
    }
}
