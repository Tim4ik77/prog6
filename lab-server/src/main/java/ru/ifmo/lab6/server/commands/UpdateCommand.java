package ru.ifmo.lab6.server.commands;


import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.exceptions.NoElementException;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.exception.CannotConnectToDataBaseException;
import ru.ifmo.lab6.server.exception.CouldnotAddStudyGroupToDataBaseException;
import ru.ifmo.lab6.server.managers.StudyGroupWithOwner;
import ru.ifmo.lab6.server.program.Server;

/**
 * The UpdateCommand class implements the Command interface and provides functionality
 * to update a StudyGroup object in the collection by its ID.
 */
public class UpdateCommand implements Command {

    /**
     * Executes the update command by parsing the parameters and updating the StudyGroup
     * with the specified ID in the collection.
     *
     * @param params the command parameters, where the first parameter is the ID of the StudyGroup to update.
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
            int id = Integer.parseInt(params[0]);
            Server.getCollectionManager().replaceGroup(id, new StudyGroupWithOwner(group, login));
            return new Response("Элемент с id " + id + " успешно изменен!");
        } catch (NoElementException e) {
            return new Response(e.getMessage());
        } catch (NumberFormatException e) {
            return new Response("Not a number!");
        }

    }

    /**
     * Returns a description of the update command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "обновить значение элемента коллекции, id которого равен заданному";
    }
}
