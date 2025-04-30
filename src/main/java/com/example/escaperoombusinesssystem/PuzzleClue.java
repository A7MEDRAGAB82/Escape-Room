package com.example.escaperoombusinesssystem;

public class PuzzleClue extends Clue {
    private String puzzleDetails;

    public PuzzleClue(String description, String slution,String puzzleDetails ) {
        super(description,slution,"puzzle");
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
