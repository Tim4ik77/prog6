package ru.ifmo.lab6.server.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.exceptions.NoElementException;
import ru.ifmo.lab6.server.utils.JsonLoader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

public class CollectionManager {
    private static final Logger logger = LoggerFactory.getLogger(CollectionManager.class);

    private ArrayList<StudyGroup> studyGroups;
    private HashSet<Integer> ids;
    private final LocalDate creationDate;
    private LocalDate lastModificationDate;

    public CollectionManager() {
        logger.info("Инициализация CollectionManager...");
        creationDate = LocalDate.now();
        lastModificationDate = LocalDate.now();
        JsonLoader loader = new JsonLoader();
        studyGroups = loader.loadFile("FILENAME");

        ids = new HashSet<>();
        for (StudyGroup group : studyGroups) {
            ids.add(group.getId());
        }

        logger.info("Загружено {} групп из файла.", studyGroups.size());
    }

    public void addGroup(StudyGroup studyGroup) {
        studyGroup.setId(getUniqId());
        lastModificationDate = LocalDate.now();
        studyGroups.add(studyGroup);
        ids.add(studyGroup.getId());

        logger.info("Добавлена новая группа с id {}", studyGroup.getId());
    }

    public ArrayList<StudyGroup> getGroups() {
        return studyGroups;
    }

    public int getUniqId() {
        int maxId = -1;
        for (StudyGroup studyGroup : studyGroups) {
            if (ids.contains(studyGroup.getId())) {
                maxId = Math.max(maxId, studyGroup.getId());
            }
        }
        return maxId + 1;
    }

    public void replaceGroup(int id, StudyGroup group) throws NoElementException {
        if (groupExists(id)) {
            logger.warn("Попытка заменить несуществующую группу с id {}", id);
            throw new NoElementException("Элемента не существует");
        }

        group.setId(id);
        lastModificationDate = LocalDate.now();
        studyGroups.removeIf(studyGroup -> studyGroup.getId() == id);
        studyGroups.add(group);

        logger.info("Группа с id {} успешно заменена", id);
    }

    public void removeGroup(int id) throws NoElementException {
        if (groupExists(id)) {
            logger.warn("Попытка удалить несуществующую группу с id {}", id);
            throw new NoElementException("Элемента не существует");
        }

        studyGroups.removeIf(studyGroup -> studyGroup.getId() == id);
        lastModificationDate = LocalDate.now();
        ids.remove(id);

        logger.info("Группа с id {} успешно удалена", id);
    }

    public void clear() {
        int removed = studyGroups.size();
        lastModificationDate = LocalDate.now();
        studyGroups.clear();
        ids.clear();

        logger.info("Коллекция очищена. Удалено {} элементов.", removed);
    }

    public void insertGroup(int position, StudyGroup group) {
        group.setId(getUniqId());
        lastModificationDate = LocalDate.now();
        studyGroups.add(position, group);
        ids.add(group.getId());

        logger.info("Группа вставлена на позицию {} с id {}", position, group.getId());
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getLastModificationDate() {
        return lastModificationDate;
    }

    public int getSize() {
        return studyGroups.size();
    }

    public String getType() {
        return studyGroups.getClass().getSimpleName();
    }

    private boolean groupExists(int id) {
        return !ids.contains(id);
    }
}
