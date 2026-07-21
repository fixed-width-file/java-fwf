package io.github.kelsoncm.fwf.columns;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Time column supporting custom formatting and zero-padding for null values.
 */
public class TimeColumn extends DateTimeColumn {

    /**
     * Constructs a new {@code TimeColumn} with a name, format, and description.
     *
     * @param name        the column name
     * @param format      Python-style time format string
     * @param description human-readable description
     */
    public TimeColumn(String name, String format, String description) {
        super(name, format, description, 2);
    }

    /**
     * Constructs a new {@code TimeColumn} with a name and format.
     *
     * @param name   the column name
     * @param format Python-style time format string
     */
    public TimeColumn(String name, String format) {
        this(name, format, null);
    }

    /**
     * Constructs a new {@code TimeColumn} with a name, defaulting to format {@code "%H%M"}.
     *
     * @param name the column name
     */
    public TimeColumn(String name) {
        this(name, "%H%M", null);
    }

    /**
     * Converts a raw fixed-width time string slice into a {@link LocalTime} value.
     *
     * @param slice raw fixed-width substring
     * @return parsed {@link LocalTime}, or null if slice consists entirely of zeros
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
            return LocalTime.parse(slice, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    String.format("O valor '%s' do campo '%s' é inválido para o formato '%s'", slice, name, format), e
            );
        }
    }

    /**
     * Formats a {@link LocalTime} or null into a fixed-width string of exact column size.
     *
     * @param value {@link LocalTime} or null to format
     * @return formatted time string or zero-filled string of size {@link #getSize()}
     * @throws IllegalArgumentException if {@code value} is not a {@link LocalTime}
     */
    @Override
    public String toStr(Object value) {
        if (value == null) {
            return "0".repeat(size);
        }
        if (!(value instanceof LocalTime lt)) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'time' ou 'None'", name));
        }
        String formatted = lt.format(formatter);
        return validateToStrSize(formatted);
    }
}
