package io.github.kelsoncm.fwf.columns;

/**
 * Positive integer column represented as a zero-padded string of digits.
 */
public class PositiveIntegerColumn extends AbstractColumn {

    /**
     * Constructs a new {@code PositiveIntegerColumn} with a name, size, and description.
     *
     * @param name        the column name
     * @param size        the column size in characters
     * @param description human-readable description
     */
    public PositiveIntegerColumn(String name, int size, String description) {
        super(name, size, description);
    }

    /**
     * Constructs a new {@code PositiveIntegerColumn} with a name and size.
     *
     * @param name the column name
     * @param size the column size in characters
     */
    public PositiveIntegerColumn(String name, int size) {
        super(name, size);
    }

    /**
     * Converts a raw fixed-width zero-padded integer slice into an Integer value.
     *
     * @param slice raw fixed-width substring
     * @return parsed integer value
     * @throws IllegalArgumentException if {@code slice} is not a valid non-negative integer
     */
    @Override
    public Object toValue(String slice) {
        super.toValue(slice);
        try {
            int val = Integer.parseInt(slice.trim());
            if (val < 0) {
                throw new IllegalArgumentException(
                        String.format("Informe uma string para converter corretamente, '%s' não é um 'positive int'", slice)
                );
            }
            return val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Informe uma string para converter corretamente, '%s' não é um 'positive int'", slice), e
            );
        }
    }

    /**
     * Formats a non-negative Number or null into a zero-padded string of exact column size.
     *
     * @param value Number or null to format
     * @return zero-padded string of size {@link #getSize()}
     * @throws IllegalArgumentException if {@code value} is negative or not a valid number
     */
    @Override
    public String toStr(Object value) {
        if (value == null) {
            return "0".repeat(size);
        }
        if (!(value instanceof Number number) || value instanceof Boolean) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'positive int' ou 'None'", name));
        }
        long longVal = number.longValue();
        if (longVal < 0) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'positive int' ou 'None'", name));
        }
        String formatted = String.format("%0" + size + "d", longVal);
        return validateToStrSize(formatted);
    }
}
