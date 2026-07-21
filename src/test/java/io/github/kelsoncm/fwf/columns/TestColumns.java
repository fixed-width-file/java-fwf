package io.github.kelsoncm.fwf.columns;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TestColumns {

    @Test
    void testAbstractColumnValidations() {
        assertThrows(NullPointerException.class, () -> new CharColumn(null, 10));
        assertThrows(IllegalArgumentException.class, () -> new CharColumn("", 10));
        assertThrows(IllegalArgumentException.class, () -> new CharColumn("   ", 10));
        assertThrows(IllegalArgumentException.class, () -> new CharColumn("name", 0));
        assertThrows(IllegalArgumentException.class, () -> new CharColumn("name", -5));

        CharColumn col = new CharColumn("name", 10, "desc");
        assertEquals("name", col.getName());
        assertEquals(10, col.getSize());
        assertEquals("desc", col.getDescription());
        assertNull(col.getStart());

        col.setStart(1);
        assertEquals(1, col.getStart());
        assertEquals(10, col.getEnd());

        CharColumn unstartedCol = new CharColumn("name", 10);
        assertThrows(NullPointerException.class, unstartedCol::getEnd);

        unstartedCol.setStart(0);
        assertThrows(IllegalArgumentException.class, unstartedCol::getEnd);

        assertThrows(IllegalArgumentException.class, () -> col.toValue(null));
        assertThrows(IllegalArgumentException.class, () -> col.toValue("short"));
    }

    @Test
    void testCharColumn() {
        CharColumn cd = new CharColumn("name", 4, "desc");
        assertEquals("desc", cd.getDescription());

        CharColumn cdNoDesc = new CharColumn("name", 4);
        assertEquals("name", cdNoDesc.getDescription());

        assertEquals("asdf", cd.toValue("asdf"));
        assertEquals("a", cd.toValue("a   "));
        assertEquals("", cd.toValue("    "));

        assertThrows(IllegalArgumentException.class, () -> cd.toStr(123));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr(true));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr("12345"));

        assertEquals("asdf", cd.toStr("asdf"));
        assertEquals("a   ", cd.toStr("a"));
        assertEquals("    ", cd.toStr(""));
        assertEquals("    ", cd.toStr(null));
    }

    @Test
    void testRightCharColumn() {
        RightCharColumn cd = new RightCharColumn("name", 4, "desc");
        assertEquals("asdf", cd.toStr("asdf"));
        assertEquals("   a", cd.toStr("a"));
        assertEquals("    ", cd.toStr(""));
        assertEquals("    ", cd.toStr(null));

        assertEquals("asdf", cd.toValue("asdf"));
        assertEquals("a", cd.toValue("   a"));
        assertEquals("", cd.toValue("    "));
    }

    @Test
    void testPositiveIntegerColumn() {
        PositiveIntegerColumn cd = new PositiveIntegerColumn("name", 4, "desc");

        assertEquals("0000", cd.toStr(null));
        assertEquals("0000", cd.toStr(0));
        assertEquals("0001", cd.toStr(1));
        assertEquals("0001", cd.toStr(1L));
        assertEquals("1234", cd.toStr(1234));

        assertThrows(IllegalArgumentException.class, () -> cd.toStr(-1));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr(12345));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr("not a number"));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr(true));

        assertEquals(1234, cd.toValue("1234"));
        assertEquals(0, cd.toValue("0000"));
        assertEquals(1, cd.toValue("0001"));
        assertEquals(1, cd.toValue("   1"));

        assertThrows(IllegalArgumentException.class, () -> cd.toValue("abcd"));
        assertThrows(IllegalArgumentException.class, () -> cd.toValue("-001"));
    }

    @Test
    void testPositiveDecimalColumn() {
        assertThrows(IllegalArgumentException.class, () -> new PositiveDecimalColumn("name", 4, 0));
        assertThrows(IllegalArgumentException.class, () -> new PositiveDecimalColumn("name", 4, -1));
        assertThrows(IllegalArgumentException.class, () -> new PositiveDecimalColumn("name", 4, 4));
        assertThrows(IllegalArgumentException.class, () -> new PositiveDecimalColumn("name", 4, 5));

        PositiveDecimalColumn cd = new PositiveDecimalColumn("name", 4, 2, "desc");
        assertEquals(2, cd.getDecimals());

        PositiveDecimalColumn defaultDec = new PositiveDecimalColumn("name", 4);
        assertEquals(2, defaultDec.getDecimals());

        assertEquals("0000", cd.toStr(null));
        assertEquals("0000", cd.toStr(0.0));
        assertEquals("0001", cd.toStr(0.01));
        assertEquals("0100", cd.toStr(1.0));
        assertEquals("1234", cd.toStr(12.34));

        assertThrows(IllegalArgumentException.class, () -> cd.toStr(-1.0));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr(123.45));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr("not a double"));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr(true));

        assertEquals(12.34, cd.toValue("1234"));
        assertEquals(0.0, cd.toValue("0000"));
        assertEquals(0.01, cd.toValue("0001"));

        assertThrows(IllegalArgumentException.class, () -> cd.toValue("abcd"));
    }

    @Test
    void testDateTimeColumn() {
        assertThrows(NullPointerException.class, () -> new DateTimeColumn(null, "%d%m%Y%H%M"));
        assertThrows(IllegalArgumentException.class, () -> new DateTimeColumn("", "%d%m%Y%H%M"));
        assertThrows(IllegalArgumentException.class, () -> new DateTimeColumn("   ", "%d%m%Y%H%M"));

        assertThrows(NullPointerException.class, () -> new DateTimeColumn("dt", null));
        assertThrows(IllegalArgumentException.class, () -> new DateTimeColumn("dt", ""));
        assertThrows(IllegalArgumentException.class, () -> new DateTimeColumn("dt", "   "));

        assertThrows(IllegalArgumentException.class, () -> new DateTimeColumn("dt", "%d%m%Y"));

        DateTimeColumn col = new DateTimeColumn("dt", "%d/%m/%Y %H:%M");
        assertEquals("%d/%m/%Y %H:%M", col.getFormat());

        DateTimeColumn colDefault = new DateTimeColumn("dt");
        assertEquals("%d%m%Y%H%M", colDefault.getFormat());

        DateTimeColumn cd = new DateTimeColumn("dt", "%d%m%Y%H%M%S", "desc", 6);
        assertEquals("00000000000000", cd.toStr(null));

        LocalDateTime ldt = LocalDateTime.of(2001, 12, 31, 23, 59, 59);
        assertEquals("31122001235959", cd.toStr(ldt));

        assertThrows(IllegalArgumentException.class, () -> cd.toStr("not a datetime"));

        assertEquals(ldt, cd.toValue("31122001235959"));
        assertNull(cd.toValue("00000000000000"));

        assertThrows(IllegalArgumentException.class, () -> cd.toValue("invalid_date_time"));
    }
}
