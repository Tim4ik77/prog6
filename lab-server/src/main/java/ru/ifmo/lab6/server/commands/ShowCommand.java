package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.managers.StudyGroupWithOwner;
import ru.ifmo.lab6.server.program.Server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The ShowCommand class implements the Command interface and provides functionality
 * to display all StudyGroup objects in the collection.
 */
public class ShowCommand implements Command {

    /**
     * Executes the show command by printing all StudyGroup objects in the collection
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

        ArrayList<StudyGroup> groups = Server.getCollectionManager()
                .getGroups() // List<StudyGroupWithOwner>
                .stream()
                .map(StudyGroupWithOwner::getStudyGroup) // Преобразование в StudyGroup
                .sorted(Comparator.comparing(StudyGroup::getName)) // Сортировка по имени
                .collect(Collectors.toCollection(ArrayList::new));

        return new Response("Список учебных групп: ", groups);

    }

    /**
     * Returns a description of the show command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }
}
