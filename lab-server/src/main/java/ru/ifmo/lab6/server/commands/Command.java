package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;

/**
 * Interface representing a command that can be executed with parameters.
 */
public interface Command {

    /**
     * Executes the command with the given parameters.
     *
     * @param params the parameters for the command.
     * @param login
     */

    Response execute(String[] params, StudyGroup group, String login);

    /**
     * Returns a description of the command.
     *
     * @return a description of the command.
     */
    String description();
}
