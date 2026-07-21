package io.github.kelsoncm.fwf.descriptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Descriptor representing the layout structure of a Fixed Width File.
 */
public class FileDescriptor {

    private final List<DetailRowDescriptor> details;
    private final HeaderRowDescriptor header;
    private final FooterRowDescriptor footer;

    public FileDescriptor(List<DetailRowDescriptor> details, HeaderRowDescriptor header, FooterRowDescriptor footer) {
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("FileDescriptor must contain at least one DetailRowDescriptor.");
        }
        this.details = Collections.unmodifiableList(new ArrayList<>(details));
        this.header = header;
        this.footer = footer;

        validateLineSizes();
    }

    public FileDescriptor(List<DetailRowDescriptor> details, HeaderRowDescriptor header) {
        this(details, header, null);
    }

    public FileDescriptor(List<DetailRowDescriptor> details) {
        this(details, null, null);
    }

    private void validateLineSizes() {
        int expectedSize = details.get(0).getLineSize();

        for (DetailRowDescriptor detail : details) {
            if (detail.getLineSize() != expectedSize) {
                throw new IllegalArgumentException("All DetailRowDescriptors must have the same line size.");
            }
        }

        if (header != null && header.getLineSize() != expectedSize) {
            throw new IllegalArgumentException("HeaderRowDescriptor line size (" + header.getLineSize() +
                    ") does not match detail line size (" + expectedSize + ").");
        }

        if (footer != null && footer.getLineSize() != expectedSize) {
            throw new IllegalArgumentException("FooterRowDescriptor line size (" + footer.getLineSize() +
                    ") does not match detail line size (" + expectedSize + ").");
        }
    }

    public List<DetailRowDescriptor> getDetails() {
        return details;
    }

    public HeaderRowDescriptor getHeader() {
        return header;
    }

    public FooterRowDescriptor getFooter() {
        return footer;
    }

    public int getLineSize() {
        return details.get(0).getLineSize();
    }
}
