package org.chip8_sim.Display;

public class Coordinate {
    private char x;
    private char y;

    public Coordinate(char x, char y) {
       this.x = x;
       this.y = y;
    }

    public char getX() {
        return x;
    }

    public void setX(char x) {
        this.x = x;
    }

    public char getY() {
        return y;
    }

    public void setY(char y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate coordinate = (Coordinate) o;
        return x == coordinate.x && y == coordinate.y;
    }

    @Override
    public String toString() {
        return " X:" + this.getX() + " Y:" + this.getY();
    }
}
