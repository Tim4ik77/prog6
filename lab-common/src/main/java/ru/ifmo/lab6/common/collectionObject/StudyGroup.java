package ru.ifmo.lab6.common.collectionObject;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * The {@code StudyGroup} class represents a study group with various attributes such as ID, name, coordinates,
 * creation date, number of students, and more. It implements the {@code Comparable} interface to allow comparison
 * based on the number of students in the group.
 */
public class StudyGroup implements Comparable<StudyGroup>, Serializable {

    private int id;
    private final String name;
    private final Coordinates coordinates;
    private final LocalDate creationDate;
    private final long studentsCount;
    private final int shouldBeExpelled;
    private final long transferredStudents;
    private final FormOfEducation formOfEducation;
    private final Person groupAdmin;

    /**
     * Constructs a {@code StudyGroup} object with the specified parameters.
     *
     * @param id the unique identifier for the study group
     * @param name the name of the study group
     * @param coordinates the coordinates of the study group
     * @param creationDate the creation date of the study group
     * @param studentsCount the number of students in the study group
     * @param shouldBeExpelled the number of students who should be expelled
     * @param transferredStudents the number of students who have been transferred
     * @param formOfEducation the form of education for the study group
     * @param groupAdmin the group admin of the study group
     */
    public StudyGroup(int id, String name, Coordinates coordinates, LocalDate creationDate, long studentsCount, int shouldBeExpelled, long transferredStudents, FormOfEducation formOfEducation, Person groupAdmin) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.studentsCount = studentsCount;
        this.shouldBeExpelled = shouldBeExpelled;
        this.transferredStudents = transferredStudents;
        this.formOfEducation = formOfEducation;
        this.groupAdmin = groupAdmin;
    }

    /**
     * Returns the unique identifier of the study group.
     *
     * @return the ID of the study group
     */
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the name of the study group.
     *
     * @return the name of the study group
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of students in the study group.
     *
     * @return the number of students in the study group
     */
    public long getStudentsCount() {
        return studentsCount;
    }

    /**
     * Returns the number of students who have been transferred from the study group.
     *
     * @return the number of transferred students
     */
    public long getTransferredStudents() {
        return transferredStudents;
    }


    /**
     * Returns a string representation of the {@code StudyGroup} object.
     *
     * @return a string representation of the study group
     */
    @Override
    public String toString() {
        return "StudyGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", studentsCount=" + studentsCount +
                ", shouldBeExpelled=" + shouldBeExpelled +
                ", transferredStudents=" + transferredStudents +
                ", formOfEducation=" + formOfEducation +
                ", groupAdmin=" + groupAdmin +
                '}';
    }

    /**
     * Compares this study group to another study group based on the number of students.
     *
     * @param group the study group to compare to
     * @return a negative integer, zero, or a positive integer as this study group has fewer, equal, or more students than the specified study group
     */
    @Override
    public int compareTo(StudyGroup group) {
        return (int) (getStudentsCount() - group.getStudentsCount());
    }
}