package io.github.kelsoncm.fwf.descriptors;

import io.github.kelsoncm.fwf.columns.AbstractColumn;

import java.util.List;

/**
 * Footer row descriptor.
 */
public class FooterRowDescriptor extends RowDescriptor {
    public FooterRowDescriptor(List<AbstractColumn> columns) {
        super(columns);
    }
}
