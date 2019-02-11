package com.gojek.domain;

public class Slot {

    private int number;
    private boolean occupied;

    public Slot(int number, boolean occupied) {
        this.number = number;
        this.occupied = occupied;
    }

    public int getNumber() {
        return number;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slot)) return false;

        Slot slot = (Slot) o;

        if (getNumber() != slot.getNumber()) return false;
        return isOccupied() == slot.isOccupied();
    }

    @Override
    public int hashCode() {
        int result = getNumber();
        result = 31 * result + (isOccupied() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "number=" + number +
                ", occupied=" + occupied +
                '}';
    }
}
