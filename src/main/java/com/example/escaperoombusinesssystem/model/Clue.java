package com.example.escaperoombusinesssystem.model;

public class Clue implements Solvable {
    private static int nextId = 1;
    private final int id;
    private String description;
    private String solution;
    private boolean solved;
    private String type;

    public Clue(String description, String solution, String type) {
        this.id = nextId++;
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (solution == null) {
            throw new IllegalArgumentException("Solution cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }

        this.description = description;
        this.solution = solution;
        this.type = type;
    }

    public boolean isSolved() {
        return solved;
    }

    public String getHint() {
        return "The Mirror shows us nothing more or less than the deepest, most desperate desire of our hearts\n";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getSolution() {
        return solution;
    }

    public void solve() {
        solved = true;
    }

    public void unsolve() {
        this.solved = false;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
