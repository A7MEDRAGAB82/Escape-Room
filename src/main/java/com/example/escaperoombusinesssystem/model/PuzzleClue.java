package com.example.escaperoombusinesssystem.model;

public class PuzzleClue extends Clue {
    private String puzzleDetails;

    public PuzzleClue(String description, String solution,String puzzleDetails ) {
        super(description,solution,"puzzle");
        if (puzzleDetails == null ) {
            throw new IllegalArgumentException("Puzzle details cannot be null or empty");
        }
        this.puzzleDetails=puzzleDetails;
    }

    public String getPuzzleDetails() {
        return
                "puzzleDetails : " + puzzleDetails ;
    }
}
