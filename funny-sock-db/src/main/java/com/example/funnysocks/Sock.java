package com.example.funnysocks;

public class Sock {
    private String id;
    private String name;     // e.g. "Banana Ducks"
    private String color;    // e.g. "yellow"
    private String pattern;  // e.g. "ducks, bananas"
    private String size;     // e.g. "M", "US 9-11"
    private String notes;    // any extra info

    // Required no-arg constructor for Jackson
    public Sock() {
    }

    public Sock(String id, String name, String color, String pattern, String size, String notes) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.pattern = pattern;
        this.size = size;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Sock{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", pattern='" + pattern + '\'' +
                ", size='" + size + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
