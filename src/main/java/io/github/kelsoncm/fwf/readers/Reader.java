package io.github.kelsoncm.fwf.readers;

import io.github.kelsoncm.fwf.descriptors.FileDescriptor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Sequential reader for Fixed Width Files.
 *
 * <p>Implements {@link Iterable} and {@link Iterator} to stream fixed-width records line by line as structured maps.
 * Validates line size and line break delimiters during initialization.</p>
 */
public class Reader implements Iterable<Map<String, Object>>, Iterator<Map<String, Object>> {

    private final List<String> lines;
    private final FileDescriptor fileDescriptor;
    private final String newline;
    private final int totalLinesCount;
    private int currentLineNum = 0;

    /**
     * Constructs a new {@code Reader} with a content source, file descriptor, and newline delimiter.
     *
     * @param iterable       source object (String, List of Strings, InputStream, or java.io.Reader)
     * @param fileDescriptor non-null {@link FileDescriptor}
     * @param newline        newline delimiter string ({@code "\n"}, {@code "\r"}, or {@code "\n\r"})
     * @throws IllegalArgumentException if any argument is invalid or line sizes do not match
     */
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

    /**
     * Constructs a new {@code Reader} defaulting to newline delimiter {@code "\n\r"}.
     *
     * @param iterable       source object
     * @param fileDescriptor non-null {@link FileDescriptor}
     */
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

    /**
     * Gets the total number of lines in the file.
     *
     * @return line count
     */
    public int getLinesCount() {
        return totalLinesCount;
    }

    /**
     * Gets the 1-based index of the line currently being processed.
     *
     * @return current 1-based line number
     */
    public int getLineNum() {
        return currentLineNum;
    }

    /**
     * Returns an iterator over the parsed row maps.
     *
     * @return iterator
     */
    @Override
    public Iterator<Map<String, Object>> iterator() {
        return this;
    }

    /**
     * Checks if more lines remain to be read.
     *
     * @return true if lines remain, false otherwise
     */
    @Override
    public boolean hasNext() {
        return currentLineNum < totalLinesCount;
    }

    /**
     * Reads and parses the next line into a map of column names to parsed values.
     *
     * @return parsed row map
     * @throws NoSuchElementException if no more lines remain
     */
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
