package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.exception.CannotConnectToDataBaseException;
import ru.ifmo.lab6.server.exception.CouldnotAddStudyGroupToDataBaseException;
import ru.ifmo.lab6.server.managers.StudyGroupWithOwner;
import ru.ifmo.lab6.server.program.Server;

/**
 * Command to add a new element to the collection.
 */
public class AddCommand implements Command {

    /**
     * Executes the add command.
     *
     * @param params command parameters. Expects no parameters.
     * @param login
     */
    @Override
    public Response execute(String[] params, StudyGroup group, String login) {
        if (params.length != 0) {
            return new Response("Invalid number of parameters!");
        }

        try {
            Server.getStudyGroupDataBaseService().addNewStudyGroup(group, login);
        } catch (CannotConnectToDataBaseException e) {
            String responseMessage = "Внутрення ошибка с базой данных";
            return new Response(responseMessage, false);
        } catch (CouldnotAddStudyGroupToDataBaseException e) {
            String responseMessage = "Не удалось добавить элемент в базу данных";
            return new Response(responseMessage, false);
        }

        Server.getCollectionManager().addGroup(new StudyGroupWithOwner(group, login));
        return new Response("Added group");
    }

    /**
     * Returns the description of the command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "добавить новый элемент в коллекцию";
    }
}
