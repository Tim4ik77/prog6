package ru.ifmo.lab6.server.commands;


import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.program.Server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to filter collection elements by a substring in the name.
 */
public class FilterCommand implements Command {

    /**
     * Executes the filter command.
     *
     * @param params command parameters. Expects one parameter - the substring to filter by.
     */
    @Override
    public Response execute(String[] params, StudyGroup group) {
        if (params.length != 1) {
            return new Response("Invalid number of parameters!");
        }

        ArrayList<StudyGroup> groups = Server.getCollectionManager().getGroups().stream()
                .filter(studyGroup -> studyGroup.getName().contains(params[0]))
                .collect(Collectors.toCollection(ArrayList::new));

        groups.sort(Comparator.comparing(StudyGroup::getName));

        return new Response("Группы отфильтрованы!", groups);

    }

    /**
     * Returns the description of the command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "вывести элементы, значение поля name которых содержит заданную подстроку";
    }
}
