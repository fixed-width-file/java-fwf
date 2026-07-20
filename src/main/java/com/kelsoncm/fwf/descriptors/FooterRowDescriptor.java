package com.kelsoncm.fwf.descriptors;

import com.kelsoncm.fwf.columns.AbstractColumn;

import java.util.List;

/**
 * Footer row descriptor.
 */
public class FooterRowDescriptor extends RowDescriptor {
    public FooterRowDescriptor(List<AbstractColumn> columns) {
        super(columns);
    }
}
