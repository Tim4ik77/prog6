package ru.ifmo.lab6.server.commands;


import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.exceptions.NoElementException;
import ru.ifmo.lab6.common.network.Response;
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
     */
    @Override
    public Response execute(String[] params, StudyGroup group) {
        if (params.length != 1) {
            return new Response("Invalid number of parameters!");
        }

        try {
            int id = Integer.parseInt(params[0]);
            Server.getCollectionManager().removeGroup(id);
            return new Response("Элемент с id " + id + " успешно удален!");
        } catch (NoElementException e) {
            return new Response(e.getMessage());
        } catch (NumberFormatException e) {
            return new Response("Not a number!");
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
