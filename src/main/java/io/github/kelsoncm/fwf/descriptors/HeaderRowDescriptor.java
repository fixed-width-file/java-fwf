package io.github.kelsoncm.fwf.descriptors;

import io.github.kelsoncm.fwf.columns.AbstractColumn;

import java.util.List;

/**
 * Header row descriptor.
 */
public class HeaderRowDescriptor extends RowDescriptor {
    public HeaderRowDescriptor(List<AbstractColumn> columns) {
        super(columns);
    }
}
