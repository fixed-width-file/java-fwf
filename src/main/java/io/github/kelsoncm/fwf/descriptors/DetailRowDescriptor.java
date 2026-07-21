package io.github.kelsoncm.fwf.descriptors;

import io.github.kelsoncm.fwf.columns.AbstractColumn;

import java.util.List;

/**
 * Detail row descriptor.
 */
public class DetailRowDescriptor extends RowDescriptor {
    public DetailRowDescriptor(List<AbstractColumn> columns) {
        super(columns);
    }
}
