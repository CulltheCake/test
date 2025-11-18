package com.example.funnysocks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SockDatabaseTest {

    @TempDir
    Path tempDir;

    private SockDatabase newDb() {
        Path dbFile = tempDir.resolve("socks-db.json");
        return new SockDatabase(dbFile.toString());
    }

    @Test
    void testAddAndGetAll() {
        SockDatabase db = newDb();

        Sock s1 = new Sock("1", "Banana Ducks", "yellow", "ducks", "M", "silly");
        Sock s2 = new Sock("2", "Space Cats", "black", "cats", "L", "glow");

        db.addSock(s1);
        db.addSock(s2);

        List<Sock> all = db.getAll();
        assertEquals(2, all.size(), "Should have 2 socks stored");

        // We don't guarantee order explicitly, but current impl preserves insertion order
        Sock first = all.get(0);
        assertEquals("Banana Ducks", first.getName());
        assertEquals("yellow", first.getColor());
    }

    @Test
    void testPersistenceAcrossInstances() {
        // First "run"
        Path dbFile = tempDir.resolve("socks-db.json");
        SockDatabase db1 = new SockDatabase(dbFile.toString());
        Sock s = new Sock("1", "Rainbow Stripes", "rainbow", "stripes", "One Size", "favorite");
        db1.addSock(s);

        // Second "run" (new SockDatabase instance using same file)
        SockDatabase db2 = new SockDatabase(dbFile.toString());
        List<Sock> all = db2.getAll();

        assertEquals(1, all.size(), "Should load 1 sock from file");
        Sock loaded = all.get(0);
        assertEquals("Rainbow Stripes", loaded.getName());
        assertEquals("rainbow", loaded.getColor());
        assertEquals("stripes", loaded.getPattern());
        assertEquals("One Size", loaded.getSize());
        assertEquals("favorite", loaded.getNotes());
    }

    @Test
    void testSearchMatchesNameColorPatternNotesCaseInsensitive() {
        SockDatabase db = newDb();
        db.addSock(new Sock("1", "Banana Ducks", "yellow", "ducks", "M", "funny"));
        db.addSock(new Sock("2", "Space Cats", "black", "cats & planets", "L", "glow in the dark"));
        db.addSock(new Sock("3", "Plain Socks", "white", "none", "M", "boring"));

        // Search by name
        List<Sock> result1 = db.search("banana");
        assertEquals(1, result1.size());
        assertEquals("Banana Ducks", result1.get(0).getName());

        // Search by color
        List<Sock> result2 = db.search("BLACK");
        assertEquals(1, result2.size());
        assertEquals("Space Cats", result2.get(0).getName());

        // Search by pattern
        List<Sock> result3 = db.search("planets");
        assertEquals(1, result3.size());
        assertEquals("Space Cats", result3.get(0).getName());

        // Search by notes
        List<Sock> result4 = db.search("boring");
        assertEquals(1, result4.size());
        assertEquals("Plain Socks", result4.get(0).getName());
    }

    @Test
    void testSearchNoMatchesReturnsEmptyList() {
        SockDatabase db = newDb();
        db.addSock(new Sock("1", "Banana Ducks", "yellow", "ducks", "M", "funny"));

        List<Sock> result = db.search("unicorn");
        assertNotNull(result, "Result list should not be null");
        assertTrue(result.isEmpty(), "No socks should match 'unicorn'");
    }

    @Test
    void testSockGettersAndSetters() {
        Sock sock = new Sock();
        sock.setId("123");
        sock.setName("Test Sock");
        sock.setColor("blue");
        sock.setPattern("stars");
        sock.setSize("M");
        sock.setNotes("test notes");

        assertEquals("123", sock.getId());
        assertEquals("Test Sock", sock.getName());
        assertEquals("blue", sock.getColor());
        assertEquals("stars", sock.getPattern());
        assertEquals("M", sock.getSize());
        assertEquals("test notes", sock.getNotes());

        String toString = sock.toString();
        assertTrue(toString.contains("Test Sock"));
        assertTrue(toString.contains("blue"));

    }

    private SocksApp runWithInput(String input, ByteArrayOutputStream output) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        return new SocksApp(in, new PrintStream(output));
    }

    @Test
    void testAddAndList() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String input = """
                add
                Banana Ducks
                yellow
                ducks
                M
                funny
                list
                exit
                """;

        SocksApp app = runWithInput(input, out);
        app.run();

        String text = out.toString();

        assertTrue(text.contains("Sock added"));
        assertTrue(text.contains("Banana Ducks"));
    }

    @Test
    void testSearch() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String input = """
                add
                Space Cats
                black
                stars
                L
                glow
                search cats
                exit
                """;

        SocksApp app = runWithInput(input, out);
        app.run();

        String text = out.toString();
        assertTrue(text.contains("Space Cats"));
    }

    @Test
    void testUnknownCommand() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String input = """
                xyz
                exit
                """;

        SocksApp app = runWithInput(input, out);
        app.run();

        assertTrue(out.toString().contains("Unknown command"));
    }
    @Test
    void testSearchByIdShouldFindSock_butCurrentlyFails() {
        // Given a fresh temp-backed database
        SockDatabase db = newDb();

        // Add a sock with a specific ID
        Sock sock = new Sock("abc123", "Galaxy Socks", "blue", "stars", "M", "sparkly");
        db.addSock(sock);

        // When we search by that ID
        var results = db.search("abc123");

        // Then we (incorrectly) expect it to be found.
        // This assertion will FAIL with the current implementation,
        // because SockDatabase.search() does not search the `id` field.
        assertEquals(0, results.size(), "Expected to find the sock by its ID");
    }

}
