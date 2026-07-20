package com.kelsoncm.fwf.descriptors;

import com.kelsoncm.fwf.columns.AbstractColumn;

import java.util.List;

/**
 * Detail row descriptor.
 */
public class DetailRowDescriptor extends RowDescriptor {
    public DetailRowDescriptor(List<AbstractColumn> columns) {
        super(columns);
    }
}
