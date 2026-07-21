package io.github.kelsoncm.fwf.descriptors;

import io.github.kelsoncm.fwf.columns.AbstractColumn;

import java.util.List;

/**
 * Row descriptor specialized for detail (body) records.
 */
public class DetailRowDescriptor extends RowDescriptor {

    /**
     * Constructs a new {@code DetailRowDescriptor} with a list of column definitions.
     *
     * @param columns non-null, non-empty list of {@link AbstractColumn} instances
     */
    public DetailRowDescriptor(List<AbstractColumn> columns) {
        super(columns);
    }
}
