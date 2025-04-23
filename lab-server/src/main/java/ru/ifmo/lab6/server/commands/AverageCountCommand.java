package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.program.Server;

import java.util.ArrayList;

/**
 * Command to calculate and display the average value of the studentsCount field for all elements in the collection.
 */
public class AverageCountCommand implements Command {

    /**
     * Executes the average count command.
     *
     * @param params command parameters. Expects no parameters.
     */
    @Override
    public Response execute(String[] params, StudyGroup group) {
        if (params.length != 0) {
            return new Response("Invalid number of parameters!");
        }

        ArrayList<StudyGroup> studyGroups = Server.getCollectionManager().getGroups();

        if (studyGroups.isEmpty()) {
            return new Response("No groups available!");
        }

        double averageCount = studyGroups.stream()
                .mapToLong(StudyGroup::getStudentsCount)
                .average()
                .orElse(0);

        return new Response("Average number of students: " + averageCount);

    }

    /**
     * Returns the description of the command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "вывести среднее значение поля studentsCount для всех элементов коллекции";
    }
}
