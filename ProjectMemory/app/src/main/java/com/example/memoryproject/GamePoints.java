package com.example.memoryproject;

public class GamePoints {
    int points;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void AddPoints(){
        points = points + 10;
    }

    public void subtractPoints(){
        if (points >= 0) {
            points = points - 5;
        } else if (points <= 0) {
            points = 0;
        }
    }
}
