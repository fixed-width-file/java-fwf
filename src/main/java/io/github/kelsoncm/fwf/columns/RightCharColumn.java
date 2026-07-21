package io.github.kelsoncm.fwf.columns;

/**
 * Character column representing a right-aligned, space-padded string.
 */
public class RightCharColumn extends CharColumn {

    /**
     * Constructs a new {@code RightCharColumn} with a name, size, and description.
     *
     * @param name        the column name
     * @param size        the column size in characters
     * @param description human-readable description
     */
    public RightCharColumn(String name, int size, String description) {
        super(name, size, description);
    }

    /**
     * Constructs a new {@code RightCharColumn} with a name and size.
     *
     * @param name the column name
     * @param size the column size in characters
     */
    public RightCharColumn(String name, int size) {
        super(name, size);
    }

    /**
     * Formats a String or null into a right-aligned, space-padded string of exact column size.
     *
     * @param value String or null to format
     * @return right-padded string of size {@link #getSize()}
     * @throws IllegalArgumentException if {@code value} is not a String or exceeds column size
     */
    @Override
    public String toStr(Object value) {
        if (value == null) {
            return " ".repeat(size);
        }
        if (!(value instanceof String strVal)) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'str' ou 'None'", name));
        }
        if (strVal.length() > size) {
            throw new IllegalArgumentException(
                    String.format("O valor a ser serializado para o campo '%s' não pode ser diferente de %d ", name, size)
            );
        }
        String padded = String.format("%" + size + "s", strVal);
        return validateToStrSize(padded);
    }
}
