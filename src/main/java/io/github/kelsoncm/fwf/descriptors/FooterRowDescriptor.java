package io.github.kelsoncm.fwf.descriptors;

import io.github.kelsoncm.fwf.columns.AbstractColumn;

import java.util.List;

/**
 * Row descriptor specialized for footer records.
 */
public class FooterRowDescriptor extends RowDescriptor {

    /**
     * Constructs a new {@code FooterRowDescriptor} with a list of column definitions.
     *
     * @param columns non-null, non-empty list of {@link AbstractColumn} instances
     */
    public FooterRowDescriptor(List<AbstractColumn> columns) {
        super(columns);
    }
}
