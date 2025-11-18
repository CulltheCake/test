package com.example.funnysocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SockDatabase {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final Path dbPath;
    private final List<Sock> socks = new ArrayList<>();

    public SockDatabase(String dbFilePath) {
        this.dbPath = Path.of(dbFilePath);
        load();
    }

    private void load() {
        if (Files.exists(dbPath)) {
            try (Reader reader = Files.newBufferedReader(dbPath)) {
                Sock[] array = mapper.readValue(reader, Sock[].class);
                socks.clear();
                if (array != null) {
                    socks.addAll(Arrays.asList(array));
                }
                System.out.println("Loaded " + socks.size() + " socks from " + dbPath);
            } catch (IOException e) {
                System.err.println("Failed to load database: " + e.getMessage());
            }
        } else {
            System.out.println("No existing database found. Starting with an empty collection.");
        }
    }

    public synchronized void save() {
        try {
            Path parent = dbPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(dbPath)) {
                mapper.writeValue(writer, socks);
            }
        } catch (IOException e) {
            System.err.println("Failed to save database: " + e.getMessage());
        }
    }

    public synchronized void addSock(Sock sock) {
        socks.add(sock);
        save();
    }

    public synchronized void addAll(Collection<Sock> newSocks) {
        socks.addAll(newSocks);
        save();
    }

    public synchronized List<Sock> getAll() {
        return new ArrayList<>(socks);
    }

    public synchronized List<Sock> search(String query) {
        String q = query.toLowerCase(Locale.ROOT);
        return socks.stream()
                .filter(s ->
                        containsIgnoreCase(s.getName(), q) ||
                        containsIgnoreCase(s.getColor(), q) ||
                        containsIgnoreCase(s.getPattern(), q) ||
                        containsIgnoreCase(s.getSize(), q) ||
                        containsIgnoreCase(s.getNotes(), q)
                )
                .collect(Collectors.toList());
    }

    private boolean containsIgnoreCase(String field, String q) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(q);
    }
}
