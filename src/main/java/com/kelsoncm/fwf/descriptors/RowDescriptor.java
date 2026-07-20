package com.kelsoncm.fwf.descriptors;

import com.kelsoncm.fwf.columns.AbstractColumn;
import com.kelsoncm.fwf.hydrating.Hydrator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Descriptor for a row consisting of multiple ordered columns.
 */
public class RowDescriptor extends Hydrator {

    @SuppressWarnings("unused")
    public static final String[] HYDRATING_ARGS = {"columns"};

    protected final List<AbstractColumn> columns;

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

    public List<AbstractColumn> getColumns() {
        return columns;
    }

    public int getLineSize() {
        return columns.get(columns.size() - 1).getEnd();
    }

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
