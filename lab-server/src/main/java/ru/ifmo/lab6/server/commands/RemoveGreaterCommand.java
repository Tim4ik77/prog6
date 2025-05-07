package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.managers.CollectionManager;
import ru.ifmo.lab6.server.managers.StudyGroupWithOwner;
import ru.ifmo.lab6.server.program.Server;

import java.util.ArrayList;

/**
 * The RemoveGreaterCommand class implements the Command interface and provides functionality
 * to remove all StudyGroup objects from the collection that are greater than a specified StudyGroup.
 */
public class RemoveGreaterCommand implements Command {

    /**
     * Executes the remove greater command by removing all StudyGroup objects from the collection
     * that are greater than a specified StudyGroup.
     *
     * @param params the command parameters, which should be empty for this command.
     * @param login
     */
    @Override
    public Response execute(String[] params, StudyGroup compareGroup, String login) {
        if (params.length != 0) {
            return new Response("Invalid number of parameters!");
        }

        CollectionManager cm = Server.getCollectionManager();
        ArrayList<StudyGroupWithOwner> groups = cm.getGroups();

        //groups.removeIf(group -> group.compareTo(compareGroup) > 0);

        return new Response("Группы, которые удовлетворяют условию, были удалены!");

    }

    /**
     * Returns a description of the remove greater command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "удалить из коллекции все элементы, превышающие заданный";
    }
}
