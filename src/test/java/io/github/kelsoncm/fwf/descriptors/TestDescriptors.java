package io.github.kelsoncm.fwf.descriptors;

import io.github.kelsoncm.fwf.columns.AbstractColumn;
import io.github.kelsoncm.fwf.columns.CharColumn;
import io.github.kelsoncm.fwf.columns.PositiveIntegerColumn;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestDescriptors {

    @Test
    void testRowDescriptorValidations() {
        assertThrows(IllegalArgumentException.class, () -> new RowDescriptor(null));
        assertThrows(IllegalArgumentException.class, () -> new RowDescriptor(Collections.emptyList()));

        CharColumn c1 = new CharColumn("c1", 5);
        CharColumn c2 = new CharColumn("c2", 5);

        RowDescriptor rd = new RowDescriptor(List.of(c1, c2));
        assertEquals(1, c1.getStart());
        assertEquals(5, c1.getEnd());
        assertEquals(6, c2.getStart());
        assertEquals(10, c2.getEnd());
        assertEquals(10, rd.getLineSize());

        Map<String, Object> values = rd.getValues("HELLO12345");
        assertEquals("HELLO", values.get("c1"));
        assertEquals("12345", values.get("c2"));
    }

    @Test
    void testRowDescriptorPositionValidationFail() {
        CharColumn c1 = new CharColumn("c1", 5);
        CharColumn c2 = new CharColumn("c2", 5);
        RowDescriptor rd = new RowDescriptor(List.of(c1, c2));

        c2.setStart(7);
        assertThrows(IllegalArgumentException.class, rd::validatePositions);

        c1.setStart(2);
        assertThrows(IllegalArgumentException.class, rd::validatePositions);
    }

    @Test
    void testFileDescriptorValidations() {
        CharColumn c1 = new CharColumn("c1", 5);
        PositiveIntegerColumn c2 = new PositiveIntegerColumn("c2", 5);

        DetailRowDescriptor detail1 = new DetailRowDescriptor(List.of(c1, c2));
        DetailRowDescriptor detail2 = new DetailRowDescriptor(List.of(c1, c2));
        HeaderRowDescriptor header = new HeaderRowDescriptor(List.of(c1, c2));
        FooterRowDescriptor footer = new FooterRowDescriptor(List.of(c1, c2));

        FileDescriptor fd = new FileDescriptor(List.of(detail1, detail2), header, footer);
        assertEquals(10, fd.getLineSize());
        assertEquals(header, fd.getHeader());
        assertEquals(footer, fd.getFooter());
        assertEquals(2, fd.getDetails().size());

        FileDescriptor fdSimple = new FileDescriptor(List.of(detail1));
        assertNull(fdSimple.getHeader());
        assertNull(fdSimple.getFooter());

        assertThrows(IllegalArgumentException.class, () -> new FileDescriptor(null));
        assertThrows(IllegalArgumentException.class, () -> new FileDescriptor(Collections.emptyList()));

        HeaderRowDescriptor validHeader = new HeaderRowDescriptor(List.of(c1, c2));
        FooterRowDescriptor validFooter = new FooterRowDescriptor(List.of(c1, c2));
        FileDescriptor fdFull = new FileDescriptor(List.of(detail1), validHeader, validFooter);
        assertEquals(10, fdFull.getLineSize());

        CharColumn c3 = new CharColumn("c3", 8);
        DetailRowDescriptor mismatchDetail = new DetailRowDescriptor(List.of(c3));
        assertThrows(IllegalArgumentException.class, () -> new FileDescriptor(List.of(detail1, mismatchDetail)));

        HeaderRowDescriptor mismatchHeader = new HeaderRowDescriptor(List.of(c3));
        assertThrows(IllegalArgumentException.class, () -> new FileDescriptor(List.of(detail1), mismatchHeader, null));

        FooterRowDescriptor mismatchFooter = new FooterRowDescriptor(List.of(c3));
        assertThrows(IllegalArgumentException.class, () -> new FileDescriptor(List.of(detail1), null, mismatchFooter));
    }
}
