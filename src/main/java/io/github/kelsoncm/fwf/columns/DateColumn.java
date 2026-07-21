package io.github.kelsoncm.fwf.columns;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Date column supporting custom formatting and zero-padding for null values.
 */
public class DateColumn extends DateTimeColumn {

    /**
     * Constructs a new {@code DateColumn} with a name, format, and description.
     *
     * @param name        the column name
     * @param format      Python-style date format string
     * @param description human-readable description
     */
    public DateColumn(String name, String format, String description) {
        super(name, format, description, 3);
    }

    /**
     * Constructs a new {@code DateColumn} with a name and format.
     *
     * @param name   the column name
     * @param format Python-style date format string
     */
    public DateColumn(String name, String format) {
        this(name, format, null);
    }

    /**
     * Constructs a new {@code DateColumn} with a name, defaulting to format {@code "%d%m%Y"}.
     *
     * @param name the column name
     */
    public DateColumn(String name) {
        this(name, "%d%m%Y", null);
    }

    /**
     * Converts a raw fixed-width date string slice into a {@link LocalDate} value.
     *
     * @param slice raw fixed-width substring
     * @return parsed {@link LocalDate}, or null if slice consists entirely of zeros
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
            return LocalDate.parse(slice, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    String.format("O valor '%s' do campo '%s' é inválido para o formato '%s'", slice, name, format), e
            );
        }
    }

    /**
     * Formats a {@link LocalDate} or null into a fixed-width string of exact column size.
     *
     * @param value {@link LocalDate} or null to format
     * @return formatted date string or zero-filled string of size {@link #getSize()}
     * @throws IllegalArgumentException if {@code value} is not a {@link LocalDate}
     */
    @Override
    public String toStr(Object value) {
        if (value == null) {
            return "0".repeat(size);
        }
        if (!(value instanceof LocalDate ld)) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'date' ou 'None'", name));
        }
        String formatted = ld.format(formatter);
        return validateToStrSize(formatted);
    }
}
