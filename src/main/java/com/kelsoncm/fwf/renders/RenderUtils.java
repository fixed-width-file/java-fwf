package com.kelsoncm.fwf.renders;

import com.kelsoncm.fwf.columns.AbstractColumn;
import com.kelsoncm.fwf.descriptors.DetailRowDescriptor;
import com.kelsoncm.fwf.descriptors.FileDescriptor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Render utility to export file descriptors into Markdown, ReStructuredText (RST), and HTML format.
 */
public final class RenderUtils {

    private RenderUtils() {
    }

    public static void renderAsMarkdown(FileDescriptor fileDescriptor, Writer out) throws IOException {
        out.write("# Description\n\n");

        if (fileDescriptor.getHeader() != null) {
            tableMarkdown("HEADER", fileDescriptor.getHeader().getColumns(), out, true);
        }

        int detailNum = 1;
        for (DetailRowDescriptor detail : fileDescriptor.getDetails()) {
            tableMarkdown("DETAILS " + detailNum, detail.getColumns(), out, true);
            detailNum++;
        }

        if (fileDescriptor.getFooter() != null) {
            tableMarkdown("FOOTER", fileDescriptor.getFooter().getColumns(), out, false);
        }
    }

    private static void tableMarkdown(String title, List<AbstractColumn> cols, Writer out, boolean trailing) throws IOException {
        int maxColNameSize = cols.stream().mapToInt(c -> c.getName().length()).max().orElse(6);
        int maxColTypeSize = cols.stream().mapToInt(c -> c.getClass().getSimpleName().length()).max().orElse(4);

        out.write(String.format("## %s\n\n", title));
        out.write(String.format(
                "|    # | %-" + maxColNameSize + "s | Size | Start |  End | %-" + maxColTypeSize + "s | Description\n",
                "Column", "Type"
        ));
        out.write(String.format(
                "| ---- | %s | ---- | ----- | ---- | %s | -----------\n",
                "-".repeat(Math.max(maxColNameSize, 6)),
                "-".repeat(Math.max(maxColTypeSize, 4))
        ));

        int line = 1;
        for (AbstractColumn col : cols) {
            out.write(String.format(
                    "| %4d | %-" + maxColNameSize + "s | %4d | %5d | %4d | %-" + maxColTypeSize + "s | %s\n",
                    line, col.getName(), col.getSize(), col.getStart(), col.getEnd(), col.getClass().getSimpleName(), col.getDescription()
            ));
            line++;
        }
        if (trailing) {
            out.write("\n");
        }
    }

    public static void renderAsRst(FileDescriptor fileDescriptor, Writer out) throws IOException {
        out.write("File Description\n===============\n\n");

        if (fileDescriptor.getHeader() != null) {
            tableRst("HEADER", fileDescriptor.getHeader().getColumns(), out, true);
        }

        int detailNum = 1;
        for (DetailRowDescriptor detail : fileDescriptor.getDetails()) {
            tableRst("DETAILS " + detailNum, detail.getColumns(), out, true);
            detailNum++;
        }

        if (fileDescriptor.getFooter() != null) {
            tableRst("FOOTER", fileDescriptor.getFooter().getColumns(), out, false);
        }
    }

    private static void tableRst(String title, List<AbstractColumn> cols, Writer out, boolean trailing) throws IOException {
        String[] headers = {"#", "Column", "Size", "Start", "End", "Type", "Description"};
        List<String[]> rows = new ArrayList<>();
        int idx = 1;
        for (AbstractColumn col : cols) {
            rows.add(new String[]{
                    String.valueOf(idx),
                    col.getName(),
                    String.valueOf(col.getSize()),
                    String.valueOf(col.getStart()),
                    String.valueOf(col.getEnd()),
                    col.getClass().getSimpleName(),
                    col.getDescription()
            });
            idx++;
        }

        List<String[]> allRows = new ArrayList<>();
        allRows.add(headers);
        allRows.addAll(rows);

        int[] colWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            int finalI = i;
            colWidths[i] = allRows.stream().mapToInt(r -> r[finalI].length()).max().orElse(0);
        }

        StringBuilder sepBuilder = new StringBuilder();
        for (int i = 0; i < colWidths.length; i++) {
            if (i > 0) sepBuilder.append(" ");
            sepBuilder.append("=".repeat(colWidths[i]));
        }
        String sep = sepBuilder.toString() + "\n";

        out.write(title + "\n--------------------\n\n");
        out.write(sep);

        StringBuilder headerLine = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            if (i > 0) headerLine.append(" ");
            headerLine.append(String.format("%-" + colWidths[i] + "s", headers[i]));
        }
        out.write(headerLine.toString() + "\n");
        out.write(sep);

        for (String[] row : rows) {
            StringBuilder rowLine = new StringBuilder();
            for (int i = 0; i < row.length; i++) {
                if (i > 0) rowLine.append(" ");
                rowLine.append(String.format("%-" + colWidths[i] + "s", row[i]));
            }
            out.write(rowLine.toString() + "\n");
        }
        out.write(sep);
        if (trailing) {
            out.write("\n");
        }
    }

    public static void renderAsHtml(FileDescriptor fileDescriptor, Writer out) throws IOException {
        out.write("<h1>Description</h1>\n");

        if (fileDescriptor.getHeader() != null) {
            tableHtml("HEADER", fileDescriptor.getHeader().getColumns(), out, true);
        }

        int detailNum = 1;
        for (DetailRowDescriptor detail : fileDescriptor.getDetails()) {
            tableHtml("DETAILS " + detailNum, detail.getColumns(), out, true);
            detailNum++;
        }

        if (fileDescriptor.getFooter() != null) {
            tableHtml("FOOTER", fileDescriptor.getFooter().getColumns(), out, false);
        }
    }

    private static void tableHtml(String title, List<AbstractColumn> cols, Writer out, boolean trailing) throws IOException {
        out.write(String.format("<h2>%s</h2>\n", title));
        out.write("<table border=\"1\">\n");
        out.write("<tr><th>#</th><th>Column</th><th>Size</th><th>Start</th><th>End</th><th>Type</th><th>Description</th></tr>\n");

        int idx = 1;
        for (AbstractColumn col : cols) {
            out.write(String.format(
                    "<tr><td>%d</td><td>%s</td><td>%d</td><td>%d</td><td>%d</td><td>%s</td><td>%s</td></tr>\n",
                    idx, col.getName(), col.getSize(), col.getStart(), col.getEnd(), col.getClass().getSimpleName(), col.getDescription()
            ));
            idx++;
        }
        out.write("</table>\n");
        if (trailing) {
            out.write("<br/>\n");
        }
    }
}
