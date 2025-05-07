package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.exception.CannotConnectToDataBaseException;
import ru.ifmo.lab6.server.exception.CouldnotAddStudyGroupToDataBaseException;
import ru.ifmo.lab6.server.managers.CollectionManager;
import ru.ifmo.lab6.server.managers.StudyGroupWithOwner;
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
     * @param login
     */
    @Override
    public Response execute(String[] params, StudyGroup group, String login) {
        if (params.length != 1) {
            return new Response("Invalid number of parameters!");
        }

        try {
            Server.getStudyGroupDataBaseService().updateStudyGroup(group, login);
        } catch (CannotConnectToDataBaseException e) {
            String responseMessage = "Внутрення ошибка с базой данных";
            return new Response(responseMessage, false);
        } catch (CouldnotAddStudyGroupToDataBaseException e) {
            String responseMessage = "Не удалось добавить элемент в базу данных";
            return new Response(responseMessage, false);
        }

        try {
            int position = Integer.parseInt(params[0]);
            CollectionManager cm = Server.getCollectionManager();

            if (position > cm.getSize()) {
                return new Response("Invalid position!");
            }

            cm.insertGroup(position, new StudyGroupWithOwner(group, login));
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
