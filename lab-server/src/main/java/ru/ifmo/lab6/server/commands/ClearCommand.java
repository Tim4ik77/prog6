package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.program.Server;

/**
 * Command to clear the collection.
 */
public class ClearCommand implements Command {

    /**
     * Executes the clear command.
     *
     * @param params command parameters. Expects no parameters.
     * @param login
     */
    @Override
    public Response execute(String[] params, StudyGroup group, String login) {
        if (params.length != 0) {
            return new Response("Invalid number of parameters!");
        }
        Server.getCollectionManager().clear();
        return new Response("Collection successfully cleared!");
    }

    /**
     * Returns the description of the command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "очистить коллекцию";
    }
}
