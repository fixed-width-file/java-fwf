package io.github.kelsoncm.fwf.descriptors;

import io.github.kelsoncm.fwf.columns.AbstractColumn;

import java.util.List;

/**
 * Row descriptor specialized for header records.
 */
public class HeaderRowDescriptor extends RowDescriptor {

    /**
     * Constructs a new {@code HeaderRowDescriptor} with a list of column definitions.
     *
     * @param columns non-null, non-empty list of {@link AbstractColumn} instances
     */
    public HeaderRowDescriptor(List<AbstractColumn> columns) {
        super(columns);
    }
}
