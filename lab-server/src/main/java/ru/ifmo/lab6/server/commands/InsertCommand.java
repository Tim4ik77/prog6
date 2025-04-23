package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.managers.CollectionManager;
import ru.ifmo.lab6.server.program.Server;

/**
 * The InsertCommand class implements the Command interface and provides functionality
 * to insert a new StudyGroup object at a specified position in the collection.
 */
public class InsertCommand implements Command {

    /**
     * Executes the insert command by inserting a new StudyGroup object at the specified position
     * in the collection.
     *
     * @param params the command parameters, where the first parameter is the position at which to insert the StudyGroup.
     */
    @Override
    public Response execute(String[] params, StudyGroup group) {
        if (params.length != 1) {
            return new Response("Invalid number of parameters!");
        }

        try {
            int position = Integer.parseInt(params[0]);
            CollectionManager cm = Server.getCollectionManager();

            if (position > cm.getSize()) {
                return new Response("Invalid position!");
            }

            cm.insertGroup(position, group);
            return new Response("Элемент успешно вставлен на позицию " + position + "!");

        } catch (NumberFormatException e) {
            return new Response("Not a number!");
        }

    }

    /**
     * Returns a description of the insert command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "добавить новый элемент в заданную позицию";
    }
}
