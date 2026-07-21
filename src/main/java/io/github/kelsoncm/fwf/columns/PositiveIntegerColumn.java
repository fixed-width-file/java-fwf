package io.github.kelsoncm.fwf.columns;

/**
 * Positive integer column with leading zeros.
 */
public class PositiveIntegerColumn extends AbstractColumn {

    public PositiveIntegerColumn(String name, int size, String description) {
        super(name, size, description);
    }

    public PositiveIntegerColumn(String name, int size) {
        super(name, size);
    }

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
