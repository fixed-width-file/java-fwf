package com.kelsoncm.fwf.columns;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Date column supporting custom formatting and null-padding.
 */
public class DateColumn extends DateTimeColumn {

    public DateColumn(String name, String format, String description) {
        super(name, format, description, 3);
    }

    public DateColumn(String name, String format) {
        this(name, format, null);
    }

    public DateColumn(String name) {
        this(name, "%d%m%Y", null);
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
            return LocalDate.parse(slice, formatter);
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
        if (!(value instanceof LocalDate ld)) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'date' ou 'None'", name));
        }
        String formatted = ld.format(formatter);
        return validateToStrSize(formatted);
    }
}
