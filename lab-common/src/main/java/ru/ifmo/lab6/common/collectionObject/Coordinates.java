package ru.ifmo.lab6.common.collectionObject;

import java.io.Serializable;

/**
 * The {@code Coordinates} class represents a pair of coordinates (x, y) where x is a float and y is a Long.
 * The y-coordinate cannot be null.
 */
public class Coordinates implements Serializable {

    private final float x;
    private final Long y;

    /**
     * Constructs a {@code Coordinates} object with the specified x and y values.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point (cannot be null)
     */
    public Coordinates(float x, Long y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a string representation of the {@code Coordinates} object.
     *
     * @return a string representation of the coordinates in the format "Coordinates{x=..., y=...}"
     */
    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public float getX() {
        return x;
    }

    public Long getY() {
        return y;
    }
}