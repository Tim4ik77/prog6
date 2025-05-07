package ru.ifmo.lab6.server.commands;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.server.program.Server;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The PrintTransferredStudentsCommand class implements the Command interface and provides functionality
 * to print the number of transferred students for all StudyGroup objects in descending order.
 */
public class PrintTransferredStudentsCommand implements Command {

    /**
     * Executes the print transferred students command by printing the number of transferred students
     * for all StudyGroup objects in descending order.
     *
     * @param params the command parameters, which should be empty for this command.
     * @param login
     */
    @Override
    public Response execute(String[] params, StudyGroup group, String login) {
        if (params.length != 0) {
            return new Response("Invalid number of parameters!");
        }

        String result = "Количество переведенных студентов по убыванию:\n" +
                Server.getCollectionManager().getGroups().stream()
                        .map(studyGroupWithOwner -> studyGroupWithOwner.getStudyGroup().getTransferredStudents())
                        .sorted(Comparator.reverseOrder())
                        .map(String::valueOf)
                        .collect(Collectors.joining("\n"));

        return new Response(result);

    }

    /**
     * Returns a description of the print transferred students command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "вывести значения поля transferredStudents всех элементов в порядке убывания";
    }
}
