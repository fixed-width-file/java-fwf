package io.github.kelsoncm.fwf.columns;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DateTime column supporting custom Python-style date-time format placeholders and zero-padding.
 */
public class DateTimeColumn extends AbstractColumn {

    /**
     * The Python-style format string (e.g. {@code "%d%m%Y%H%M"}).
     */
    protected final String format;

    /**
     * The converted Java {@link DateTimeFormatter} pattern string.
     */
    protected final String javaPattern;

    /**
     * The compiled {@link DateTimeFormatter} instance.
     */
    protected final DateTimeFormatter formatter;

    /**
     * The required number of Python format specifiers in the format string.
     */
    protected final int requiredFormatNumElements;

    /**
     * Constructs a new {@code DateTimeColumn} with a name, format, and description.
     *
     * @param name        the column name
     * @param format      Python-style datetime format string
     * @param description human-readable description
     */
    public DateTimeColumn(String name, String format, String description) {
        this(name, format, description, 5);
    }

    /**
     * Protected constructor allowing sub-classes to specify the required number of format specifiers.
     *
     * @param name                       the column name
     * @param format                     Python-style datetime format string
     * @param description                human-readable description
     * @param requiredFormatNumElements number of required specifiers
     */
    protected DateTimeColumn(String name, String format, String description, int requiredFormatNumElements) {
        super(validateName(name), calculateSize(name, format, requiredFormatNumElements), description);
        this.format = format;
        this.requiredFormatNumElements = requiredFormatNumElements;
        this.javaPattern = convertPythonFormatToJavaPattern(format);
        this.formatter = DateTimeFormatter.ofPattern(this.javaPattern);
    }

    /**
     * Constructs a new {@code DateTimeColumn} with a name and format.
     *
     * @param name   the column name
     * @param format Python-style datetime format string
     */
    public DateTimeColumn(String name, String format) {
        this(name, format, null);
    }

    /**
     * Constructs a new {@code DateTimeColumn} with a name, defaulting to format {@code "%d%m%Y%H%M"}.
     *
     * @param name the column name
     */
    public DateTimeColumn(String name) {
        this(name, "%d%m%Y%H%M", null);
    }

    private static String validateName(String name) {
        if (name == null) {
            throw new NullPointerException("O campo name deve ser uma string");
        }
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo column_name deve ser uma string válida e não branca");
        }
        return name;
    }

    /**
     * Calculates column size in characters based on a sample formatting execution.
     *
     * @param name                       column name
     * @param format                     Python format string
     * @param requiredFormatNumElements expected count of specifiers
     * @return total formatted size in characters
     */
    protected static int calculateSize(String name, String format, int requiredFormatNumElements) {
        if (format == null) {
            throw new NullPointerException(String.format("O argumento '_format' do campo '%s' deve ser uma string", name));
        }
        if (format.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format("O argumento '_format' do campo '%s' deve ser uma string válida e não branca", name));
        }

        int count = countPythonSpecifiers(format);
        if (count != requiredFormatNumElements) {
            throw new IllegalArgumentException(
                    String.format("O argumento '_format' (%s) do campo '%s' deve ter um formato de data/hora válido", format, name)
            );
        }

        String javaPat = convertPythonFormatToJavaPattern(format);
        LocalDateTime sample = LocalDateTime.of(2001, 12, 31, 13, 59, 0);
        return sample.format(DateTimeFormatter.ofPattern(javaPat)).length();
    }

    private static int countPythonSpecifiers(String format) {
        Matcher matcher = Pattern.compile("(%[a-zA-Z])").matcher(format);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Converts Python strftime format tokens to Java DateTimeFormatter pattern symbols.
     *
     * @param format Python strftime format string
     * @return Java DateTimeFormatter pattern
     */
    protected static String convertPythonFormatToJavaPattern(String format) {
        if (format == null) return null;
        return format
                .replace("%d", "dd")
                .replace("%m", "MM")
                .replace("%Y", "yyyy")
                .replace("%H", "HH")
                .replace("%M", "mm")
                .replace("%S", "ss");
    }

    /**
     * Gets the Python-style format string.
     *
     * @return Python format string
     */
    public String getFormat() {
        return format;
    }

    /**
     * Converts a raw fixed-width date-time string slice into a {@link LocalDateTime} value.
     *
     * @param slice raw fixed-width substring
     * @return parsed {@link LocalDateTime}, or null if slice consists entirely of zeros
     * @throws IllegalArgumentException if {@code slice} is null or has an invalid format
     */
    @Override
    public Object toValue(String slice) {
        if (slice == null) {
            throw new IllegalArgumentException("Informe uma string para converter corretamente");
        }
        if (slice.length() != size) {
            throw new IllegalArgumentException(String.format("A string deve ter exatamente o tamanho do campo '%s' (%d)", name, size));
        }
        if (slice.equals("0".repeat(size))) {
            return null;
        }
        try {
            return LocalDateTime.parse(slice, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    String.format("O valor '%s' do campo '%s' é inválido para o formato '%s'", slice, name, format), e
            );
        }
    }

    /**
     * Formats a {@link LocalDateTime} or null into a fixed-width string of exact column size.
     *
     * @param value {@link LocalDateTime} or null to format
     * @return formatted date-time string or zero-filled string of size {@link #getSize()}
     * @throws IllegalArgumentException if {@code value} is not a {@link LocalDateTime}
     */
    @Override
    public String toStr(Object value) {
        if (value == null) {
            return "0".repeat(size);
        }
        if (!(value instanceof LocalDateTime ldt)) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'datetime' ou 'None'", name));
        }
        String formatted = ldt.format(formatter);
        return validateToStrSize(formatted);
    }
}
