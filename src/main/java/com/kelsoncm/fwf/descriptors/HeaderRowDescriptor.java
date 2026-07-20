package com.kelsoncm.fwf.descriptors;

import com.kelsoncm.fwf.columns.AbstractColumn;

import java.util.List;

/**
 * Header row descriptor.
 */
public class HeaderRowDescriptor extends RowDescriptor {
    public HeaderRowDescriptor(List<AbstractColumn> columns) {
        super(columns);
    }
}
