package com.example.escaperoombusinesssystem;

public class Clue implements Solvable {
    private  String description;
    private  String solution;
    private  boolean solved;
    private String type;

    public Clue(String description, String solution, String type) {
        if (description == null ) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (solution == null) {
            throw new IllegalArgumentException("Solution cannot be null or empty");
        }
        if (type == null ) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }

        this.description = description;
        this.solution = solution;
        this.type = type;
    }
   public boolean isSolved(){
        return solved;
    }
public String getHint(){
        return "The Mirror shows us nothing more or less than the deepest, most desperate desire of our hearts\n";
}
public void solve(){
        solved=true;
}

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }
}
