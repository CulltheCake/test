package com.example.funnysocks;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SocksApp {

    private final Scanner scanner;
    private final PrintStream out;
    private final SockDatabase db;

    private static final String DB_FILE = "data/socks-db.json";

    // Default constructor used in real runs
    public SocksApp() {
        this(System.in, System.out);
    }

    // Testable constructor
    public SocksApp(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
        this.db = new SockDatabase(DB_FILE);
    }

    public void run() {
        out.println("Welcome to your Funny Socks Database 🧦");
        printHelp();

        boolean running = true;
        while (running && scanner.hasNextLine()) {
            out.print("\n> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String rest = parts.length > 1 ? parts[1].trim() : "";

            switch (command) {
                case "help" -> printHelp();
                case "list" -> handleList();
                case "add" -> handleAdd();
                case "import" -> handleImport(rest);
                case "search" -> handleSearch(rest);
                case "exit", "quit" -> running = false;
                default -> out.println("Unknown command. Type 'help'.");
            }
        }
    }

    private void printHelp() {
        out.println("""
                Commands:
                  help
                  list
                  add
                  import <file>
                  search <query>
                  exit
                """);
    }

    private void handleList() {
        List<Sock> socks = db.getAll();
        if (socks.isEmpty()) {
            out.println("You have no socks in your database yet.");
            return;
        }

        out.println("You have " + socks.size() + " socks:");
        for (Sock s : socks) {
            out.println("- " + s.getName());
        }
    }

    private void handleAdd() {
        out.println("Adding a new sock…");

        out.print("Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            out.println("Name is required; sock not added.");
            return;
        }

        out.print("Color: ");
        String color = scanner.nextLine();

        out.print("Pattern: ");
        String pattern = scanner.nextLine();

        out.print("Size: ");
        String size = scanner.nextLine();

        out.print("Notes: ");
        String notes = scanner.nextLine();

        db.addSock(new Sock(UUID.randomUUID().toString(), name,
                color, pattern, size, notes));

        out.println("Sock added!");
    }

    private void handleSearch(String query) {
        if (query.isEmpty()) {
            out.println("Usage: search <query>");
            return;
        }

        List<Sock> results = db.search(query);
        if (results.isEmpty()) {
            out.println("No socks matched your search.");
            return;
        }

        out.println("Found " + results.size() + " matching socks:");
        for (Sock s : results) {
            out.println("- " + s.getName());
        }
    }

    private void handleImport(String filePath) {
        if (filePath.isEmpty()) {
            out.println("Usage: import <file>");
            return;
        }

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            out.println("File not found: " + filePath);
            return;
        }

        int count = 0;
        boolean first = true;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (first && line.toLowerCase().startsWith("name,")) {
                    first = false;
                    continue;
                }
                first = false;

                String[] c = line.split(",", -1);
                if (c.length == 0 || c[0].isBlank()) continue;

                db.addSock(new Sock(UUID.randomUUID().toString(),
                        c[0], c.length > 1 ? c[1] : "",
                        c.length > 2 ? c[2] : "",
                        c.length > 3 ? c[3] : "",
                        c.length > 4 ? c[4] : ""
                ));
                count++;
            }

            out.println("Imported " + count + " socks.");

        } catch (IOException e) {
            out.println("Import failed: " + e.getMessage());
        }
    }
}
