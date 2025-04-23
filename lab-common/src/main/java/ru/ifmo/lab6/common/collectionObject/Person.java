package ru.ifmo.lab6.common.collectionObject;

import java.io.Serializable;

/**
 * Represents a person with various attributes such as name, weight, eye color, hair color, and nationality.
 */
public class Person implements Serializable {
    private final String name; // Field cannot be null, string cannot be empty
    private final Integer weight; // Field cannot be null, value must be greater than 0
    private final Color eyeColor; // Field cannot be null
    private final Color hairColor; // Field cannot be null
    private final Country nationality; // Field can be null

    /**
     * Constructs a Person object with the specified attributes.
     *
     * @param name the name of the person (cannot be null or empty)
     * @param weight the weight of the person (cannot be null and must be greater than 0)
     * @param eyeColor the eye color of the person (cannot be null)
     * @param hairColor the hair color of the person (cannot be null)
     * @param nationality the nationality of the person (can be null)
     */
    public Person(String name, Integer weight, Color eyeColor, Color hairColor, Country nationality) {
        this.name = name;
        this.weight = weight;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
    }

    /**
     * Returns the name of the person.
     *
     * @return the name of the person
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of the person.
     *
     * @return a string representation of the person
     */
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                ", eyeColor=" + eyeColor +
                ", hairColor=" + hairColor +
                ", nationality=" + nationality +
                '}';
    }
}
