package io.github.kelsoncm.fwf.columns;

/**
 * Base abstract class representing a column within a Fixed Width File layout.
 *
 * <p>Manages column metadata such as name, size, description, 1-based start/end position calculation,
 * value parsing, and string formatting.</p>
 */
public abstract class AbstractColumn {

    /**
     * The unique name of the column.
     */
    protected final String name;

    /**
     * The fixed character length (size) of the column.
     */
    protected final int size;

    /**
     * Human-readable description of the column.
     */
    protected final String description;

    /**
     * 1-based starting character position of the column within a line.
     */
    protected Integer start;

    /**
     * Constructs a new {@code AbstractColumn} with a name, size, and description.
     *
     * @param name        the column name, must not be null or blank
     * @param size        the fixed character size of the column, must be greater than 0
     * @param description human-readable description of the column, defaults to name if null
     * @throws NullPointerException     if {@code name} is null
     * @throws IllegalArgumentException if {@code name} is blank or {@code size} &lt;= 0
     */
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

    /**
     * Constructs a new {@code AbstractColumn} with a name and size.
     *
     * @param name the column name, must not be null or blank
     * @param size the fixed character size of the column, must be greater than 0
     * @throws NullPointerException     if {@code name} is null
     * @throws IllegalArgumentException if {@code name} is blank or {@code size} &lt;= 0
     */
    public AbstractColumn(String name, int size) {
        this(name, size, null);
    }

    /**
     * Gets the column name.
     *
     * @return column name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the column size in characters.
     *
     * @return column size
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the column description.
     *
     * @return column description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the 1-based start position of the column.
     *
     * @return 1-based start index, or null if not yet positioned
     */
    public Integer getStart() {
        return start;
    }

    /**
     * Sets the 1-based start position of the column.
     *
     * @param start 1-based start index
     */
    public void setStart(Integer start) {
        this.start = start;
    }

    /**
     * Calculates and returns the 1-based end position of the column.
     *
     * @return 1-based end index
     * @throws NullPointerException     if {@code start} is null
     * @throws IllegalArgumentException if {@code start} &lt;= 0
     */
    public int getEnd() {
        if (start == null) {
            throw new NullPointerException("O campo start deve ser um inteiro");
        }
        if (start <= 0) {
            throw new IllegalArgumentException("O campo start deve ser maior que 0");
        }
        return start + size - 1;
    }

    /**
     * Converts a raw fixed-width string slice into a parsed object value.
     *
     * @param slice raw substring sliced from a fixed-width line
     * @return parsed value object
     * @throws IllegalArgumentException if {@code slice} is null or does not match column size
     */
    public Object toValue(String slice) {
        if (slice == null) {
            throw new IllegalArgumentException("Informe uma string para converter corretamente");
        }
        if (slice.length() != size) {
            throw new IllegalArgumentException(String.format("A string deve ter exatamente o tamanho do campo '%s' (%d)", name, size));
        }
        return slice;
    }

    /**
     * Validates that the formatted string output exactly matches the column size.
     *
     * @param value formatted string to check
     * @return the verified string
     * @throws IllegalArgumentException if string length does not equal column size
     */
    protected String validateToStrSize(String value) {
        if (value.length() != size) {
            throw new IllegalArgumentException(
                    String.format("O valor a ser serializado para o campo '%s' não pode ser diferente de %d ", name, size)
            );
        }
        return value;
    }

    /**
     * Formats a domain value into a fixed-width string representation of exact column size.
     *
     * @param value domain value to format
     * @return fixed-width formatted string of length {@link #getSize()}
     * @throws IllegalArgumentException if the value cannot be serialized or formatted
     */
    public abstract String toStr(Object value);
}
