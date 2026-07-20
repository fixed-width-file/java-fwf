package com.kelsoncm.fwf.columns;

import com.kelsoncm.fwf.hydrating.Hydrator;

/**
 * Base abstract class for fixed-width file columns.
 */
public abstract class AbstractColumn extends Hydrator {

    protected final String name;
    protected final int size;
    protected final String description;
    protected Integer start;

    public AbstractColumn(String name, int size, String description) {
        if (name == null) {
            throw new NullPointerException("O campo name deve ser uma string");
        }
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo column_name deve ser uma string válida e não branca");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("O campo size deve ser maior que 0");
        }

        this.name = name;
        this.size = size;
        this.description = (description == null) ? name : description;
    }

    public AbstractColumn(String name, int size) {
        this(name, size, null);
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public int getEnd() {
        if (start == null) {
            throw new NullPointerException("O campo start deve ser um inteiro");
        }
        if (start <= 0) {
            throw new IllegalArgumentException("O campo start deve ser maior que 0");
        }
        return start + size - 1;
    }

    public Object toValue(String slice) {
        if (slice == null) {
            throw new IllegalArgumentException("Informe uma string para converter corretamente");
        }
        if (slice.length() != size) {
            throw new IllegalArgumentException(String.format("A string deve ter exatamente o tamanho do campo '%s' (%d)", name, size));
        }
        return slice;
    }

    protected String validateToStrSize(String value) {
        if (value.length() != size) {
            throw new IllegalArgumentException(
                    String.format("O valor a ser serializado para o campo '%s' não pode ser diferente de %d ", name, size)
            );
        }
        return value;
    }

    public abstract String toStr(Object value);
}
