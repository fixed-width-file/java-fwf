package io.github.kelsoncm.fwf.columns;

/**
 * Positive decimal column with a fixed number of decimal places, formatted with leading zeros.
 */
public class PositiveDecimalColumn extends PositiveIntegerColumn {

    /**
     * Number of implicit decimal places.
     */
    protected final int decimals;

    /**
     * Constructs a new {@code PositiveDecimalColumn} with a name, size, decimals, and description.
     *
     * @param name        the column name
     * @param size        the total column size in characters
     * @param decimals    number of implicit decimal places, must be &gt; 0 and &lt; size
     * @param description human-readable description
     * @throws IllegalArgumentException if {@code decimals} &lt;= 0 or {@code size} &lt;= {@code decimals}
     */
    public PositiveDecimalColumn(String name, int size, int decimals, String description) {
        super(name, size, description);
        if (decimals <= 0) {
            throw new IllegalArgumentException("Os decimais devem ser maior que 0");
        }
        if (size <= decimals) {
            throw new IllegalArgumentException("Os decimais devem ser menores que o size");
        }
        this.decimals = decimals;
    }

    /**
     * Constructs a new {@code PositiveDecimalColumn} with a name, size, and decimals.
     *
     * @param name     the column name
     * @param size     the total column size in characters
     * @param decimals number of implicit decimal places
     */
    public PositiveDecimalColumn(String name, int size, int decimals) {
        this(name, size, decimals, null);
    }

    /**
     * Constructs a new {@code PositiveDecimalColumn} with a name and size, defaulting to 2 decimals.
     *
     * @param name the column name
     * @param size the total column size in characters
     */
    public PositiveDecimalColumn(String name, int size) {
        this(name, size, 2, null);
    }

    /**
     * Gets the number of implicit decimal places.
     *
     * @return number of decimal places
     */
    public int getDecimals() {
        return decimals;
    }

    /**
     * Converts a raw fixed-width zero-padded integer slice into a Double value with decimal precision.
     *
     * @param slice raw fixed-width substring
     * @return parsed Double value
     * @throws IllegalArgumentException if {@code slice} is not a valid positive decimal string
     */
    @Override
    public Object toValue(String slice) {
        try {
            int intVal = ((Number) super.toValue(slice)).intValue();
            return intVal / Math.pow(10, decimals);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Informe uma string para converter corretamente, '%s' não é um 'positive decimal'", slice), e
            );
        }
    }

    /**
     * Formats a non-negative Double/Float or null into a zero-padded string of exact column size.
     *
     * @param value Double, Float or null to format
     * @return zero-padded decimal string of size {@link #getSize()}
     * @throws IllegalArgumentException if {@code value} is negative or not a Double/Float
     */
    @Override
    public String toStr(Object value) {
        if (value == null) {
            return "0".repeat(size);
        }
        if (!(value instanceof Double || value instanceof Float) || value instanceof Boolean) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'positive decimal' ou 'None'", name));
        }
        double dVal = ((Number) value).doubleValue();
        if (dVal < 0.0) {
            throw new IllegalArgumentException(String.format("O campo '%s' só aceita 'positive decimal' ou 'None'", name));
        }
        long intVal = Math.round(dVal * Math.pow(10, decimals));
        String formatted = String.format("%0" + size + "d", intVal);
        return validateToStrSize(formatted);
    }
}
