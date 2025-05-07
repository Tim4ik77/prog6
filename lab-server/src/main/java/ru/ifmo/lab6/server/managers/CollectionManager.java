package ru.ifmo.lab6.server.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.lab6.common.exceptions.NoElementException;
import ru.ifmo.lab6.server.database.StudyGroupDataBaseService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CollectionManager {
    private static final Logger logger = LoggerFactory.getLogger(CollectionManager.class);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private ArrayList<StudyGroupWithOwner> studyGroups;
    private HashSet<Integer> ids;
    private final LocalDate creationDate;
    private LocalDate lastModificationDate;

    public CollectionManager(StudyGroupDataBaseService studyGroupDataBaseService) {
        logger.info("Инициализация CollectionManager...");
        creationDate = LocalDate.now();
        lastModificationDate = LocalDate.now();

        lock.writeLock().lock();
        try {
            studyGroups = studyGroupDataBaseService.loadCollection();
            ids = new HashSet<>();
            for (StudyGroupWithOwner group : studyGroups) {
                ids.add(group.getStudyGroup().getId());
            }
        } finally {
            lock.writeLock().unlock();
        }

        logger.info("Загружено {} групп из файла.", studyGroups.size());
    }

    public void addGroup(StudyGroupWithOwner studyGroup) {
        lock.writeLock().lock();
        try {
            studyGroup.getStudyGroup().setId(getUniqId());
            lastModificationDate = LocalDate.now();
            studyGroups.add(studyGroup);
            ids.add(studyGroup.getStudyGroup().getId());
            logger.info("Добавлена новая группа с id {}", studyGroup.getStudyGroup().getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public ArrayList<StudyGroupWithOwner> getGroups() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(studyGroups); // Возвращаем копию для безопасности
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getUniqId() {
        lock.readLock().lock();
        try {
            int maxId = -1;
            for (StudyGroupWithOwner studyGroup : studyGroups) {
                if (ids.contains(studyGroup.getStudyGroup().getId())) {
                    maxId = Math.max(maxId, studyGroup.getStudyGroup().getId());
                }
            }
            return maxId + 1;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void replaceGroup(int id, StudyGroupWithOwner group) throws NoElementException {
        lock.writeLock().lock();
        try {
            if (groupExists(id)) {
                logger.warn("Попытка заменить несуществующую группу с id {}", id);
                throw new NoElementException("Элемента не существует");
            }

            group.getStudyGroup().setId(id);
            lastModificationDate = LocalDate.now();
            studyGroups.removeIf(studyGroup -> studyGroup.getStudyGroup().getId() == id);
            studyGroups.add(group);
            logger.info("Группа с id {} успешно заменена", id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeGroup(int id) throws NoElementException {
        lock.writeLock().lock();
        try {
            if (groupExists(id)) {
                logger.warn("Попытка удалить несуществующую группу с id {}", id);
                throw new NoElementException("Элемента не существует");
            }

            studyGroups.removeIf(studyGroup -> studyGroup.getStudyGroup().getId() == id);
            lastModificationDate = LocalDate.now();
            ids.remove(id);
            logger.info("Группа с id {} успешно удалена", id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            int removed = studyGroups.size();
            lastModificationDate = LocalDate.now();
            studyGroups.clear();
            ids.clear();
            logger.info("Коллекция очищена. Удалено {} элементов.", removed);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void insertGroup(int position, StudyGroupWithOwner group) {
        lock.writeLock().lock();
        try {
            group.getStudyGroup().setId(getUniqId());
            lastModificationDate = LocalDate.now();
            studyGroups.add(position, group);
            ids.add(group.getStudyGroup().getId());
            logger.info("Группа вставлена на позицию {} с id {}", position, group.getStudyGroup().getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getLastModificationDate() {
        lock.readLock().lock();
        try {
            return lastModificationDate;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getSize() {
        lock.readLock().lock();
        try {
            return studyGroups.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getType() {
        lock.readLock().lock();
        try {
            return studyGroups.getClass().getSimpleName();
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean groupExists(int id) {
        lock.readLock().lock();
        try {
            return !ids.contains(id);
        } finally {
            lock.readLock().unlock();
        }
    }
}