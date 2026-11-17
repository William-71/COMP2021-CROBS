package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandLoggerTest {

    @TempDir
    Path tempDir;

    @Test
    void logsToTxtAndHtmlInOrder() throws IOException {
        Path html = tempDir.resolve("log.html");
        Path txt = tempDir.resolve("log.txt");
        CommandLogger logger = new CommandLogger(html.toString(), txt.toString());

        logger.log("login u0");
        logger.log("addUser Alice");
        logger.log("logout");

        assertEquals(3, logger.getLoggedCount());
        assertEquals("login u0", logger.getCommands().get(0));

        String txtContent = new String(Files.readAllBytes(txt), StandardCharsets.UTF_8);
        assertTrue(txtContent.contains("login u0"));
        assertTrue(txtContent.contains("addUser Alice"));
        assertTrue(txtContent.contains("logout"));

        String htmlContent = new String(Files.readAllBytes(html), StandardCharsets.UTF_8);
        assertTrue(htmlContent.contains("<table>"));
        assertTrue(htmlContent.contains("<tr><td>1</td><td>login u0</td></tr>"));
        assertTrue(htmlContent.contains("<tr><td>2</td><td>addUser Alice</td></tr>"));
        assertTrue(htmlContent.contains("<tr><td>3</td><td>logout</td></tr>"));
    }

    @Test
    void escapesHtmlSpecialCharacters() throws IOException {
        Path html = tempDir.resolve("esc.html");
        Path txt = tempDir.resolve("esc.txt");
        CommandLogger logger = new CommandLogger(html.toString(), txt.toString());
        logger.log("search a <b> & \"x\"");

        String htmlContent = new String(Files.readAllBytes(html), StandardCharsets.UTF_8);
        assertTrue(htmlContent.contains("&lt;"));
        assertTrue(htmlContent.contains("&amp;"));
        assertTrue(htmlContent.contains("&quot;"));
    }

    @Test
    void nullPathsDoNotThrow() {
        CommandLogger logger = new CommandLogger(null, null);
        logger.log("login u0");
        assertEquals(1, logger.getLoggedCount());
    }
}
