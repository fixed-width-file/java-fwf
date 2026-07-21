package io.github.kelsoncm.fwf.columns;

/**
 * Left-aligned, space-padded character column.
 */
public class CharColumn extends AbstractColumn {

    @SuppressWarnings("unused")
    public static final String[] HYDRATING_ARGS = {"name", "size", "description"};

    public CharColumn(String name, int size, String description) {
        super(name, size, description);
    }

    public CharColumn(String name, int size) {
        super(name, size);
    }

    @Override
    public String toValue(String slice) {
        String base = (String) super.toValue(slice);
        return base.trim();
    }

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
        String padded = String.format("%-" + size + "s", strVal);
        return validateToStrSize(padded);
    }
}
