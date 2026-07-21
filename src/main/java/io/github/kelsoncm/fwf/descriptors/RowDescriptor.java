package io.github.kelsoncm.fwf.descriptors;

import io.github.kelsoncm.fwf.columns.AbstractColumn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Descriptor for a row consisting of multiple ordered columns.
 *
 * <p>Automatically computes 1-based start/end positions for every column in order and validates
 * that column boundaries are contiguous without overlaps or gaps.</p>
 */
public class RowDescriptor {

    /**
     * Ordered list of column definitions for this row.
     */
    protected final List<AbstractColumn> columns;

    /**
     * Constructs a new {@code RowDescriptor} with a list of column definitions.
     *
     * @param columns non-null, non-empty list of {@link AbstractColumn} instances
     * @throws IllegalArgumentException if {@code columns} is null or empty
     */
    public RowDescriptor(List<AbstractColumn> columns) {
        if (columns == null) {
            throw new IllegalArgumentException("columns deve ser uma List");
        }
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("columns deve ter ao menos 1 elemento");
        }
        this.columns = columns;

        AbstractColumn last = null;
        for (AbstractColumn column : columns) {
            column.setStart(last != null ? last.getEnd() + 1 : 1);
            last = column;
        }
        validatePositions();
    }

    /**
     * Gets the list of column definitions.
     *
     * @return unmodifiable/ordered list of columns
     */
    public List<AbstractColumn> getColumns() {
        return columns;
    }

    /**
     * Gets the total line size (in characters) required by this row.
     *
     * @return total line size in characters
     */
    public int getLineSize() {
        return columns.get(columns.size() - 1).getEnd();
    }

    /**
     * Validates that column positions start at 1 and are strictly contiguous.
     *
     * @throws IllegalArgumentException if column positions are invalid or contain gaps/overlaps
     */
    public void validatePositions() {
        AbstractColumn prev = null;
        for (AbstractColumn col : columns) {
            if (prev == null) {
                if (col.getStart() != 1) {
                    throw new IllegalArgumentException(String.format("A coluna %s deve começar com 1", col.getName()));
                }
            } else {
                if (prev.getEnd() + 1 != col.getStart()) {
                    throw new IllegalArgumentException(
                            String.format("A coluna %s (starts in %d) deve começar imediatamente após a coluna %s (ends in %d)",
                                    col.getName(), col.getStart(), prev.getName(), prev.getEnd())
                    );
                }
            }
            prev = col;
        }
    }

    /**
     * Parses a raw line string into a map of column names to parsed values.
     *
     * @param rowLine raw fixed-width row line
     * @return map of column names to parsed values
     */
    public Map<String, Object> getValues(String rowLine) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (AbstractColumn col : columns) {
            int startIdx = col.getStart() - 1;
            int endIdx = col.getEnd();
            String slice = rowLine.substring(startIdx, endIdx);
            result.put(col.getName(), col.toValue(slice));
        }
        return result;
    }
}
