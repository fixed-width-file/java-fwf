package io.github.kelsoncm.fwf.columns;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestColumns {

    @Test
    void testCharColumnConstructor() {
        CharColumn cd = new CharColumn("type", 2, "desc");
        assertEquals("type", cd.getName());
        assertEquals(2, cd.getSize());
        assertEquals("desc", cd.getDescription());

        CharColumn cd2 = new CharColumn("name", 1);
        assertEquals(1, cd2.getSize());
        assertEquals("name", cd2.getDescription());

        assertThrows(NullPointerException.class, () -> new CharColumn(null, 1, ""));
        assertThrows(IllegalArgumentException.class, () -> new CharColumn("", 1, ""));
        assertThrows(IllegalArgumentException.class, () -> new CharColumn(" ", 1, ""));
        assertThrows(IllegalArgumentException.class, () -> new CharColumn("name", 0));
    }

    @Test
    void testCharColumnEnd() {
        CharColumn cd = new CharColumn("name", 2, "desc");
        cd.setStart(1);
        assertEquals(2, cd.getEnd());

        cd.setStart(null);
        assertThrows(NullPointerException.class, cd::getEnd);

        cd.setStart(0);
        assertThrows(IllegalArgumentException.class, cd::getEnd);
    }

    @Test
    void testCharColumnToValue() {
        CharColumn cd = new CharColumn("name", 2, "desc");
        assertThrows(IllegalArgumentException.class, () -> cd.toValue(null));
        assertThrows(IllegalArgumentException.class, () -> cd.toValue("1"));
        assertThrows(IllegalArgumentException.class, () -> cd.toValue("333"));

        assertEquals("22", cd.toValue("22"));
        assertEquals("2", cd.toValue("2 "));
        assertEquals("2", cd.toValue(" 2"));
    }

    @Test
    void testCharColumnToStr() {
        CharColumn cd = new CharColumn("name", 4, "desc");
        assertThrows(IllegalArgumentException.class, () -> cd.toStr(123));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr(true));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr("12345"));

        assertEquals("asdf", cd.toStr("asdf"));
        assertEquals("a   ", cd.toStr("a"));
        assertEquals("    ", cd.toStr(""));
        assertEquals("    ", cd.toStr(null));
    }

    @Test
    void testCharColumnDehydrate() {
        CharColumn col1 = new CharColumn("col_name", 20, "col_desc");
        Map<String, Object> map1 = col1.dehydrate();
        assertEquals("io.github.kelsoncm.fwf.columns.CharColumn", map1.get("_hydrate_as"));

        CharColumn col2 = new CharColumn("col_name", 20);
        Map<String, Object> map2 = col2.dehydrate();
        assertEquals("io.github.kelsoncm.fwf.columns.CharColumn", map2.get("_hydrate_as"));
    }

    @Test
    void testRightCharColumn() {
        RightCharColumn cd = new RightCharColumn("name", 4, "desc");
        assertEquals("asdf", cd.toStr("asdf"));
        assertEquals("   a", cd.toStr("a"));
        assertEquals("    ", cd.toStr(""));
        assertEquals("    ", cd.toStr(null));

        assertThrows(IllegalArgumentException.class, () -> cd.toStr(123));
        assertThrows(IllegalArgumentException.class, () -> cd.toStr("12345"));

        cd.setStart(1);
        assertEquals(4, cd.getEnd());

        RightCharColumn cd2 = new RightCharColumn("name", 4);
        assertEquals(4, cd2.getSize());
    }

    @Test
    void testPositiveIntegerColumn() {
        PositiveIntegerColumn pic = new PositiveIntegerColumn("age", 3, "desc");
        assertEquals("age", pic.getName());
        assertEquals(3, pic.getSize());

        PositiveIntegerColumn pic2 = new PositiveIntegerColumn("age", 3);
        assertEquals(3, pic2.getSize());

        assertEquals(45, pic.toValue("045"));
        assertEquals(0, pic.toValue("000"));
        assertThrows(IllegalArgumentException.class, () -> pic.toValue("abc"));
        assertThrows(IllegalArgumentException.class, () -> pic.toValue("-05"));

        assertEquals("045", pic.toStr(45));
        assertEquals("000", pic.toStr(0));
        assertEquals("000", pic.toStr(null));

        assertThrows(IllegalArgumentException.class, () -> pic.toStr(-10));
        assertThrows(IllegalArgumentException.class, () -> pic.toStr(1000));
        assertThrows(IllegalArgumentException.class, () -> pic.toStr("45"));
        assertThrows(IllegalArgumentException.class, () -> pic.toStr(true));
    }

    @Test
    void testPositiveDecimalColumn() {
        PositiveDecimalColumn pdc = new PositiveDecimalColumn("val", 6, 2, "desc");
        assertEquals(2, pdc.getDecimals());

        assertThrows(IllegalArgumentException.class, () -> new PositiveDecimalColumn("val", 6, 0));
        assertThrows(IllegalArgumentException.class, () -> new PositiveDecimalColumn("val", 2, 2));

        assertEquals(12.34, (Double) pdc.toValue("001234"), 0.001);
        assertThrows(IllegalArgumentException.class, () -> pdc.toValue("abc   "));

        assertEquals("001234", pdc.toStr(12.34));
        assertEquals("000000", pdc.toStr(null));

        assertThrows(IllegalArgumentException.class, () -> pdc.toStr(-1.0));
        assertThrows(IllegalArgumentException.class, () -> pdc.toStr(12345.67));
        assertThrows(IllegalArgumentException.class, () -> pdc.toStr("12.34"));
        assertThrows(IllegalArgumentException.class, () -> pdc.toStr(true));

        PositiveDecimalColumn pdc2 = new PositiveDecimalColumn("val", 6, 2);
        assertEquals(2, pdc2.getDecimals());

        PositiveDecimalColumn pdcDefaults = new PositiveDecimalColumn("val", 5);
        assertEquals(2, pdcDefaults.getDecimals());
    }

    @Test
    void testDateTimeColumn() {
        DateTimeColumn dtc = new DateTimeColumn("dt", "%d%m%Y%H%M", "desc");
        assertEquals("%d%m%Y%H%M", dtc.getFormat());

        assertThrows(NullPointerException.class, () -> new DateTimeColumn("dt", null));
        assertThrows(IllegalArgumentException.class, () -> new DateTimeColumn("dt", "  "));
        assertThrows(IllegalArgumentException.class, () -> new DateTimeColumn("dt", "%d%m"));

        LocalDateTime expected = LocalDateTime.of(2026, 7, 20, 15, 30);
        assertEquals(expected, dtc.toValue("200720261530"));
        assertNull(dtc.toValue("000000000000"));
        assertThrows(IllegalArgumentException.class, () -> dtc.toValue(null));
        assertThrows(IllegalArgumentException.class, () -> dtc.toValue("123"));
        assertThrows(IllegalArgumentException.class, () -> dtc.toValue("999999999999"));

        assertEquals("200720261530", dtc.toStr(expected));
        assertEquals("000000000000", dtc.toStr(null));
        assertThrows(IllegalArgumentException.class, () -> dtc.toStr("invalid"));

        DateTimeColumn dtc2 = new DateTimeColumn("dt", "%d%m%Y%H%M");
        assertEquals("%d%m%Y%H%M", dtc2.getFormat());

        DateTimeColumn dtcDefaults = new DateTimeColumn("dt");
        assertEquals("%d%m%Y%H%M", dtcDefaults.getFormat());

        assertNull(DateTimeColumn.convertPythonFormatToJavaPattern(null));
        assertThrows(NullPointerException.class, () -> DateTimeColumn.calculateSize("dt", null, 5));
    }

    @Test
    void testDateColumn() {
        DateColumn dc = new DateColumn("dt", "%d%m%Y", "desc");
        assertEquals("%d%m%Y", dc.getFormat());

        LocalDate expected = LocalDate.of(2026, 7, 20);
        assertEquals(expected, dc.toValue("20072026"));
        assertNull(dc.toValue("00000000"));
        assertThrows(IllegalArgumentException.class, () -> dc.toValue(null));
        assertThrows(IllegalArgumentException.class, () -> dc.toValue("123"));
        assertThrows(IllegalArgumentException.class, () -> dc.toValue("99999999"));

        assertEquals("20072026", dc.toStr(expected));
        assertEquals("00000000", dc.toStr(null));
        assertThrows(IllegalArgumentException.class, () -> dc.toStr("invalid"));

        DateColumn dc2 = new DateColumn("dt", "%d%m%Y");
        assertEquals("%d%m%Y", dc2.getFormat());

        DateColumn dcDefaults = new DateColumn("dt");
        assertEquals("%d%m%Y", dcDefaults.getFormat());
    }

    @Test
    void testTimeColumn() {
        TimeColumn tc = new TimeColumn("tm", "%H%M", "desc");
        assertEquals("%H%M", tc.getFormat());

        LocalTime expected = LocalTime.of(15, 30);
        assertEquals(expected, tc.toValue("1530"));
        assertNull(tc.toValue("0000"));
        assertThrows(IllegalArgumentException.class, () -> tc.toValue(null));
        assertThrows(IllegalArgumentException.class, () -> tc.toValue("123"));
        assertThrows(IllegalArgumentException.class, () -> tc.toValue("9999"));

        assertEquals("1530", tc.toStr(expected));
        assertEquals("0000", tc.toStr(null));
        assertThrows(IllegalArgumentException.class, () -> tc.toStr("invalid"));

        TimeColumn tc2 = new TimeColumn("tm", "%H%M");
        assertEquals("%H%M", tc2.getFormat());

        TimeColumn tcDefaults = new TimeColumn("tm");
        assertEquals("%H%M", tcDefaults.getFormat());
    }
}
