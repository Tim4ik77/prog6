package ru.ifmo.lab6.server.commands;


import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.exceptions.NoElementException;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.exception.CannotConnectToDataBaseException;
import ru.ifmo.lab6.server.exception.CannotDeleteFromDataBaseException;
import ru.ifmo.lab6.server.exception.CouldnotAddStudyGroupToDataBaseException;
import ru.ifmo.lab6.server.program.Server;

/**
 * The RemoveCommand class implements the Command interface and provides functionality
 * to remove a StudyGroup object from the collection by its ID.
 */
public class RemoveCommand implements Command {

    /**
     * Executes the remove command by removing the StudyGroup object with the specified ID
     * from the collection.
     *
     * @param params the command parameters, where the first parameter is the ID of the StudyGroup to remove.
     * @param login
     */
    @Override
    public Response execute(String[] params, StudyGroup group, String login) {
        if (params.length != 1) {
            return new Response("Invalid number of parameters!");
        }

        try {
            int id = Integer.parseInt(params[0]);
            Server.getStudyGroupDataBaseService().deleteStudyGroupById(id, login);
            Server.getCollectionManager().removeGroup(id);
            return new Response("Элемент с id " + id + " успешно удален!");
        } catch (NumberFormatException e) {
            return new Response("Not a number!");
        } catch (CannotConnectToDataBaseException e) {
            String responseMessage = "Внутрення ошибка с базой данных";
            return new Response(responseMessage, false);
        } catch (CannotDeleteFromDataBaseException e) {
            String responseMessage = "Не удалось удалить элемент!";
            return new Response(responseMessage, false);
        }
    }

    /**
     * Returns a description of the remove command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "удалить элемент из коллекции по его id";
    }
}
