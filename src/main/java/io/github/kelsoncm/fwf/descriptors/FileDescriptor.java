package io.github.kelsoncm.fwf.descriptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Descriptor representing the layout structure of a Fixed Width File.
 *
 * <p>Contains details, optional header, and optional footer descriptors. Validates that line sizes across
 * all sections are equal.</p>
 */
public class FileDescriptor {

    private final List<DetailRowDescriptor> details;
    private final HeaderRowDescriptor header;
    private final FooterRowDescriptor footer;

    /**
     * Constructs a new {@code FileDescriptor} with detail, header, and footer descriptors.
     *
     * @param details list of {@link DetailRowDescriptor} instances, must not be empty
     * @param header  optional {@link HeaderRowDescriptor}, or null if file has no header
     * @param footer  optional {@link FooterRowDescriptor}, or null if file has no footer
     * @throws IllegalArgumentException if {@code details} is null or empty, or line sizes do not match
     */
    public FileDescriptor(List<DetailRowDescriptor> details, HeaderRowDescriptor header, FooterRowDescriptor footer) {
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("FileDescriptor must contain at least one DetailRowDescriptor.");
        }
        this.details = Collections.unmodifiableList(new ArrayList<>(details));
        this.header = header;
        this.footer = footer;

        validateLineSizes();
    }

    /**
     * Constructs a new {@code FileDescriptor} with detail and header descriptors.
     *
     * @param details list of {@link DetailRowDescriptor} instances
     * @param header  optional {@link HeaderRowDescriptor}
     */
    public FileDescriptor(List<DetailRowDescriptor> details, HeaderRowDescriptor header) {
        this(details, header, null);
    }

    /**
     * Constructs a new {@code FileDescriptor} with detail descriptors only.
     *
     * @param details list of {@link DetailRowDescriptor} instances
     */
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

    /**
     * Gets the list of detail row descriptors.
     *
     * @return unmodifiable list of detail descriptors
     */
    public List<DetailRowDescriptor> getDetails() {
        return details;
    }

    /**
     * Gets the header row descriptor.
     *
     * @return header descriptor or null if not present
     */
    public HeaderRowDescriptor getHeader() {
        return header;
    }

    /**
     * Gets the footer row descriptor.
     *
     * @return footer descriptor or null if not present
     */
    public FooterRowDescriptor getFooter() {
        return footer;
    }

    /**
     * Gets the expected line size (in characters) for the file.
     *
     * @return line size in characters
     */
    public int getLineSize() {
        return details.get(0).getLineSize();
    }
}
