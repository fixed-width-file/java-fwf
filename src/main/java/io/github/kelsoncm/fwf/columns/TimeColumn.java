package io.github.kelsoncm.fwf.columns;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Time column supporting custom formatting and null-padding.
 */
public class TimeColumn extends DateTimeColumn {

    public TimeColumn(String name, String format, String description) {
        super(name, format, description, 2);
    }

    public TimeColumn(String name, String format) {
        this(name, format, null);
    }

    public TimeColumn(String name) {
        this(name, "%H%M", null);
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
            return LocalTime.parse(slice, formatter);
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
        if (!(value instanceof LocalTime lt)) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'time' ou 'None'", name));
        }
        String formatted = lt.format(formatter);
        return validateToStrSize(formatted);
    }
}
