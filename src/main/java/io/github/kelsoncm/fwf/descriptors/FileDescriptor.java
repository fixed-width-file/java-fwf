package io.github.kelsoncm.fwf.descriptors;

import io.github.kelsoncm.fwf.hydrating.Hydrator;

import java.util.ArrayList;
import java.util.List;

/**
 * File descriptor composing header, details, and footer descriptors.
 */
public class FileDescriptor extends Hydrator {

    @SuppressWarnings("unused")
    public static final String[] HYDRATING_ARGS = {"details", "header", "footer"};

    private final HeaderRowDescriptor header;
    private final FooterRowDescriptor footer;
    private final List<DetailRowDescriptor> details;
    private final int lineSize;

    public FileDescriptor(List<DetailRowDescriptor> details, HeaderRowDescriptor header, FooterRowDescriptor footer) {
        if (details == null) {
            throw new IllegalArgumentException("details deve ser uma List");
        }
        if (details.isEmpty()) {
            throw new IllegalArgumentException("details deve ser uma List com ao menos 1 DetailRowDescriptor");
        }
        for (Object detail : details) {
            if (!(detail instanceof DetailRowDescriptor)) {
                throw new IllegalArgumentException("details deve ser uma List de DetailRowDescriptor");
            }
        }

        this.details = details;
        this.header = header;
        this.footer = footer;

        validateSizes();
        this.lineSize = details.get(0).getLineSize();
    }

    public FileDescriptor(List<DetailRowDescriptor> details) {
        this(details, null, null);
    }

    public HeaderRowDescriptor getHeader() {
        return header;
    }

    public FooterRowDescriptor getFooter() {
        return footer;
    }

    public List<DetailRowDescriptor> getDetails() {
        return details;
    }

    public int getLineSize() {
        return lineSize;
    }

    public void validateSizes() {
        int h = (header != null) ? header.getLineSize() : 0;
        int f = (footer != null) ? footer.getLineSize() : 0;
        int d = details.get(0).getLineSize();

        List<Integer> detailSizes = new ArrayList<>();
        boolean allDetailsEqual = true;
        for (DetailRowDescriptor detail : details) {
            int size = detail.getLineSize();
            detailSizes.add(size);
            if (size != d) {
                allDetailsEqual = false;
            }
        }

        boolean headerMatch = (h == 0 || h == d);
        boolean footerMatch = (f == 0 || f == d);

        if (!allDetailsEqual || !headerMatch || !footerMatch) {
            throw new IllegalArgumentException(
                    String.format("O tamanho das linhas header (%d), footer (%d) e das details (%s) devem ser iguais", h, f, detailSizes)
            );
        }
    }
}
