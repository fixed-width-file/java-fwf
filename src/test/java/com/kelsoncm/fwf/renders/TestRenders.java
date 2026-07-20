package com.kelsoncm.fwf.renders;

import com.kelsoncm.fwf.columns.CharColumn;
import com.kelsoncm.fwf.columns.PositiveIntegerColumn;
import com.kelsoncm.fwf.descriptors.DetailRowDescriptor;
import com.kelsoncm.fwf.descriptors.FileDescriptor;
import com.kelsoncm.fwf.descriptors.FooterRowDescriptor;
import com.kelsoncm.fwf.descriptors.HeaderRowDescriptor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRenders {

    @Test
    void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<RenderUtils> ctor = RenderUtils.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        org.junit.jupiter.api.Assertions.assertNotNull(ctor.newInstance());
    }

    private FileDescriptor createSampleFileDescriptor() {
        CharColumn c1 = new CharColumn("name", 10, "Name of user");
        PositiveIntegerColumn c2 = new PositiveIntegerColumn("age", 3, "Age in years");

        HeaderRowDescriptor header = new HeaderRowDescriptor(List.of(new CharColumn("h_title", 13, "Header Title")));
        DetailRowDescriptor detail = new DetailRowDescriptor(List.of(c1, c2));
        FooterRowDescriptor footer = new FooterRowDescriptor(List.of(new CharColumn("f_title", 13, "Footer Title")));

        return new FileDescriptor(List.of(detail), header, footer);
    }

    @Test
    void testRenderAsMarkdown() throws IOException {
        FileDescriptor fd = createSampleFileDescriptor();
        StringWriter sw = new StringWriter();

        RenderUtils.renderAsMarkdown(fd, sw);
        String result = sw.toString();

        assertTrue(result.contains("# Description"));
        assertTrue(result.contains("## HEADER"));
        assertTrue(result.contains("## DETAILS 1"));
        assertTrue(result.contains("## FOOTER"));
        assertTrue(result.contains("CharColumn"));
        assertTrue(result.contains("PositiveIntegerColumn"));
    }

    @Test
    void testRenderAsRst() throws IOException {
        FileDescriptor fd = createSampleFileDescriptor();
        StringWriter sw = new StringWriter();

        RenderUtils.renderAsRst(fd, sw);
        String result = sw.toString();

        assertTrue(result.contains("File Description"));
        assertTrue(result.contains("HEADER"));
        assertTrue(result.contains("DETAILS 1"));
        assertTrue(result.contains("FOOTER"));
    }

    @Test
    void testRenderAsHtml() throws IOException {
        FileDescriptor fd = createSampleFileDescriptor();
        StringWriter sw = new StringWriter();

        RenderUtils.renderAsHtml(fd, sw);
        String result = sw.toString();

        assertTrue(result.contains("<h1>Description</h1>"));
        assertTrue(result.contains("<h2>HEADER</h2>"));
        assertTrue(result.contains("<h2>DETAILS 1</h2>"));
        assertTrue(result.contains("<h2>FOOTER</h2>"));
        assertTrue(result.contains("<table border=\"1\">"));
    }
}
