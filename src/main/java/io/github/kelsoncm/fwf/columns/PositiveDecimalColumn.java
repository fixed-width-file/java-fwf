package io.github.kelsoncm.fwf.columns;

/**
 * Positive decimal column with fixed decimal places and leading zeros.
 */
public class PositiveDecimalColumn extends PositiveIntegerColumn {

    protected final int decimals;

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

    public PositiveDecimalColumn(String name, int size, int decimals) {
        this(name, size, decimals, null);
    }

    public PositiveDecimalColumn(String name, int size) {
        this(name, size, 2, null);
    }

    public int getDecimals() {
        return decimals;
    }

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
