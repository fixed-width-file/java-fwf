package com.kelsoncm.fwf.columns;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DateTime column supporting custom formatting and null-padding.
 */
public class DateTimeColumn extends AbstractColumn {

    @SuppressWarnings("unused")
    public static final String[] HYDRATING_ARGS = {"name", "format", "description"};

    protected final String format;
    protected final String javaPattern;
    protected final DateTimeFormatter formatter;
    protected final int requiredFormatNumElements;

    public DateTimeColumn(String name, String format, String description) {
        this(name, format, description, 5);
    }

    protected DateTimeColumn(String name, String format, String description, int requiredFormatNumElements) {
        super(validateName(name), calculateSize(name, format, requiredFormatNumElements), description);
        this.format = format;
        this.requiredFormatNumElements = requiredFormatNumElements;
        this.javaPattern = convertPythonFormatToJavaPattern(format);
        this.formatter = DateTimeFormatter.ofPattern(this.javaPattern);
    }

    public DateTimeColumn(String name, String format) {
        this(name, format, null);
    }

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

    public String getFormat() {
        return format;
    }

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
