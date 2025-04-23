package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.program.Server;

import java.util.stream.Collectors;

/**
 * The HelpCommand class implements the Command interface and provides functionality
 * to display help information for all available commands.
 */
public class HelpCommand implements Command {

    /**
     * Executes the help command by printing descriptions of all available commands.
     *
     * @param params the command parameters, which should be empty for this command.
     */
    @Override
    public Response execute(String[] params, StudyGroup group) {
        if (params.length != 0) {
            return new Response("Invalid number of parameters!");
        }

        String commandsList = Server.getCommandManager().getCommands().entrySet().stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue().description())
                .collect(Collectors.joining("\n"));

        return new Response(commandsList);

    }

    /**
     * Returns a description of the help command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "вывести справку по доступным командам";
    }
}
