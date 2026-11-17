package hk.edu.polyu.comp.comp2021.crobs.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REQ1 — records every executed command into an HTML table and a plain TXT file.
 */
public class CommandLogger {
    private final String htmlPath;
    private final String txtPath;
    private final List<String> commands;
    private int nextIndex;

    public CommandLogger(String htmlPath, String txtPath) {
        this.htmlPath = htmlPath;
        this.txtPath = txtPath;
        this.commands = new ArrayList<String>();
        this.nextIndex = 1;
    }

    /**
     * Appends one command to both log targets (rewrites full files to keep HTML consistent).
     *
     * @param command the raw command line (trimmed)
     */
    public void log(String command) {
        if (command == null) {
            return;
        }
        commands.add(command);
        nextIndex++;
        flush();
    }

    public int getLoggedCount() {
        return commands.size();
    }

    public List<String> getCommands() {
        return new ArrayList<String>(commands);
    }

    private void flush() {
        writeTxt();
        writeHtml();
    }

    private void writeTxt() {
        if (txtPath == null || txtPath.isEmpty()) {
            return;
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(txtPath, false));
            for (int i = 0; i < commands.size(); i++) {
                writer.write(commands.get(i));
                writer.newLine();
            }
        } catch (IOException e) {
            // Logging failure should not crash the CLI session.
        } finally {
            closeQuietly(writer);
        }
    }

    private void writeHtml() {
        if (htmlPath == null || htmlPath.isEmpty()) {
            return;
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(htmlPath, false));
            writer.write("<html>");
            writer.newLine();
            writer.write("<body>");
            writer.newLine();
            writer.write("<table>");
            writer.newLine();
            for (int i = 0; i < commands.size(); i++) {
                writer.write("<tr><td>");
                writer.write(Integer.toString(i + 1));
                writer.write("</td><td>");
                writer.write(escapeHtml(commands.get(i)));
                writer.write("</td></tr>");
                writer.newLine();
            }
            writer.write("</table>");
            writer.newLine();
            writer.write("</body>");
            writer.newLine();
            writer.write("</html>");
            writer.newLine();
        } catch (IOException e) {
            // Logging failure should not crash the CLI session.
        } finally {
            closeQuietly(writer);
        }
    }

    private static String escapeHtml(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private static void closeQuietly(BufferedWriter writer) {
        if (writer == null) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ignored) {
            // ignore
        }
    }
}
