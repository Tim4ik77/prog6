package ru.ifmo.lab6.client.program;

import ru.ifmo.lab6.common.exceptions.InvalidResponseException;
import ru.ifmo.lab6.common.network.CommandInfo;
import ru.ifmo.lab6.client.utils.Parser;
import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Request;
import ru.ifmo.lab6.common.network.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public final class ConsoleApp {

    private final HashMap<String, CommandInfo> commands;
    private final UPDClient updClient;
    private final Scanner sc;
    private final Set<String> processedFiles;

    public ConsoleApp(UPDClient updClient) {
        this.updClient = updClient;
        commands = getCommandInfo();
        sc = new Scanner(System.in);
        processedFiles = new HashSet<>(); // Инициализация множества
    }

    public void run() {
        System.out.println("Для справки введите help: ");
        while (true) {
            System.out.print("> ");
            String input = sc.nextLine();
            processCommand(input);
        }
    }

    public void processCommand(String input) {
        String[] tokens = input.trim().split(" ");
        String commandName = tokens[0];
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (commandName.equals("exit")) {
            System.exit(0);
        } else if (commandName.equals("execute_script")) {
            String path = params[0];

            if (isRecursionDetected(path)) {
                return;
            }

            try {
                List<String> lines = Files.readAllLines(Paths.get(path));
                for (String line : lines) {
                    System.out.println("> " + line);
                    processCommand(line);
                }
            } catch (IOException e) {
                System.out.println("Файл не найден!");
            } finally {
                processedFiles.remove(path);
            }
            return;
        }

        if (!commands.containsKey(commandName)) {
            System.out.println("Введено неправильное имя команды!");
            return;
        }

        CommandInfo commandInfo = commands.get(commandName);

        if (params.length != commandInfo.argumentsNumber()) {
            System.out.println("Неправильное количество параметров!");
            return;
        }

        StudyGroup group = null;
        if (commandInfo.hasObject()) {
            Parser parser = new Parser(sc);
            group = parser.parseStudyGroup();
        }

        Request request = new Request(commandName, params, group);
        try {
            Response response = updClient.send(request);
            System.out.println(response.message());
            if (response.studyGroups() != null) {
                System.out.println(response.studyGroups());
            }
        } catch (InvalidResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean isRecursionDetected(String path) {
        if (processedFiles.contains(path)) {
            System.out.println("Recursion detected, skipping!");
            return true;
        }
        processedFiles.add(path);
        return false;
    }

    public HashMap<String, CommandInfo> getCommandInfo() {
        HashMap<String, CommandInfo> commandInfo = new HashMap<>();
        commandInfo.put("help", new CommandInfo(0, false));
        commandInfo.put("info", new CommandInfo(0, false));
        commandInfo.put("show", new CommandInfo(0, false));
        commandInfo.put("add", new CommandInfo(0, true));
        commandInfo.put("update", new CommandInfo(1, true));
        commandInfo.put("remove_by_id", new CommandInfo(1, false));
        commandInfo.put("clear", new CommandInfo(0, false));
        commandInfo.put("insert_at", new CommandInfo(1, true));
        commandInfo.put("remove_greater", new CommandInfo(0, true));
        commandInfo.put("history", new CommandInfo(0, false));
        commandInfo.put("average_of_students_count", new CommandInfo(0, false));
        commandInfo.put("filter_contains_name", new CommandInfo(1, false));
        commandInfo.put("print_field_descending_transferred_students", new CommandInfo(0, false));
        return commandInfo;
    }
}
