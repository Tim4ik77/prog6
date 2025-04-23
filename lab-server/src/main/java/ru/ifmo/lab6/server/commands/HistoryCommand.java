package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.program.Server;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * The HistoryCommand class implements the Command interface and provides functionality
 * to display the last 15 commands executed by the user.
 */
public class HistoryCommand implements Command {

    /**
     * Executes the history command by printing the last 15 commands executed by the user.
     *
     * @param params the command parameters, which should be empty for this command.
     */
    @Override
    public Response execute(String[] params, StudyGroup group) {
        if (params.length != 0) {
            return new Response("Invalid number of parameters!");
        }

        String historyOutput = String.join("\n", Server.getCommandManager().getHistory());

        return new Response(historyOutput);

    }

    /**
     * Returns a description of the history command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "вывести последние 15 команд (без их аргументов)";
    }
}
