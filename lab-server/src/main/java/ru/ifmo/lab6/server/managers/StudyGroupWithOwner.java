package ru.ifmo.lab6.server.managers;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;

public class StudyGroupWithOwner {
    private StudyGroup studyGroup;
    private final String owner;

    public StudyGroupWithOwner(StudyGroup studyGroup, String owner) {
        this.studyGroup = studyGroup;
        this.owner = owner;
    }

    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return studyGroup.toString() + "; Owner: " + owner;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;

        if(!(obj instanceof StudyGroupWithOwner other)) return false;

        return studyGroup.equals(other.studyGroup) && owner.equals(other.owner);
    }


    @Override
    public int hashCode() {
        return studyGroup.hashCode() | owner.hashCode();
    }
}
