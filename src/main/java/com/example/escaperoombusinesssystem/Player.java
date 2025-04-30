package com.example.escaperoombusinesssystem;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<Clue> solvedClues;
    private LocalDateTime startTime;

    public Player(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("com.example.escaperoombusinesssystem.Player 's name can't be empty");
        }
        else {
            this.name = name;
        }

        this.solvedClues = new ArrayList<>();
        this.startTime = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void addSolvedClue(Clue clue) {
        solvedClues.add(clue);
    }

    public ArrayList<Clue> getSolvedClues() {
        return solvedClues;
    }

    public Duration getTimeElapsed() {
        return Duration.between(startTime, LocalDateTime.now());
    }
}
