package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.program.Server;

/**
 * Command to add a new element to the collection.
 */
public class AddCommand implements Command {

    /**
     * Executes the add command.
     *
     * @param params command parameters. Expects no parameters.
     */
    @Override
    public Response execute(String[] params, StudyGroup group) {
        if (params.length != 0) {
            return new Response("Invalid number of parameters!");
        }
        Server.getCollectionManager().addGroup(group);
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
