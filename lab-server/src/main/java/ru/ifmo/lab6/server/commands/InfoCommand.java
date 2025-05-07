package ru.ifmo.lab6.server.commands;


import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.managers.CollectionManager;
import ru.ifmo.lab6.server.program.Server;

/**
 * The InfoCommand class implements the Command interface and provides functionality
 * to display information about the collection, such as its type, creation date,
 * last modification date, and the number of elements.
 */
public class InfoCommand implements Command {

    /**
     * Executes the info command by printing information about the collection
     * to the standard output.
     *
     * @param params the command parameters, which should be empty for this command.
     * @param login
     */
    @Override
    public Response execute(String[] params, StudyGroup group, String login) {
        if (params.length != 0) {
            return new Response("Invalid number of parameters!");
        }

        CollectionManager cm = Server.getCollectionManager();

        String info = String.join("\n",
                "Дата создания коллекции: " + cm.getCreationDate(),
                "Дата последней модификации коллекции: " + cm.getLastModificationDate(),
                "Количество элементов в коллекции: " + cm.getSize(),
                "Тип коллекции: " + cm.getType()
        );

        return new Response(info);

    }

    /**
     * Returns a description of the info command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)";
    }
}
