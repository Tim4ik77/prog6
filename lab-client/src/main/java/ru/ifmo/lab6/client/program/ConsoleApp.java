package ru.ifmo.lab6.client.program;

import ru.ifmo.lab6.common.network.User;
import ru.ifmo.lab6.common.exceptions.InvalidResponseException;
import ru.ifmo.lab6.common.network.CommandInfo;
import ru.ifmo.lab6.client.utils.Parser;
import ru.ifmo.lab6.common.collectionObject.StudyGroup;
import ru.ifmo.lab6.common.network.Request;
import ru.ifmo.lab6.common.network.Response;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public final class ConsoleApp {

    private final HashMap<String, CommandInfo> commands;
    private final UPDClient updClient;
    private final Scanner sc;
    private User currentUser;

    public ConsoleApp(UPDClient updClient) {
        this.updClient = updClient;
        commands = getCommandInfo();
        sc = new Scanner(System.in);
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
        try {
            Set<String> tempProcessedFiles = new HashSet<>();
            if (!checkRecursion(input.trim(), tempProcessedFiles)) {
                executeCommands(input.trim(), new HashSet<>());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файлов: " + e.getMessage());
        }
    }

    private boolean checkRecursion(String input, Set<String> tempProcessedFiles) throws IOException {
        String[] tokens = input.trim().split(" ");
        String commandName = tokens[0];
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (commandName.equals("execute_script")) {
            if (params.length == 0) return false;
            String path = params[0];
            if (tempProcessedFiles.contains(path)) {
                System.out.println("Обнаружена рекурсия: " + path);
                return true;
            }
            tempProcessedFiles.add(path);

            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                if (checkRecursion(line, tempProcessedFiles)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void executeCommands(String input, Set<String> processedFiles) {
        String[] tokens = input.trim().split(" ");
        String commandName = tokens[0];
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (commandName.equals("exit")) {
            System.exit(0);
        } else if (commandName.equals("execute_script")) {
            String path = params[0];
            if (processedFiles.contains(path)) return;
            processedFiles.add(path);
            try {
                List<String> lines = Files.readAllLines(Paths.get(path));
                for (String line : lines) {
                    System.out.println("> " + line);
                    executeCommands(line, processedFiles);
                }
            } catch (IOException e) {
                System.out.println("Файл не найден: " + path);
            } finally {
                processedFiles.remove(path);
            }
            return;
        } else if (commandName.equals("login")) {
            loginCommand();
            return;
        } else if (commandName.equals("register")) {
            registerCommand();
            return;
        }

        if (currentUser == null) {
            System.out.println("Пожалуйста, сначала выполните вход или регистрацию.");
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

        Request request = new Request(commandName, params, group, currentUser); // ← передаём пользователя
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

    private void registerCommand() {

        System.out.print("Введите имя пользователя: ");
        String name = sc.nextLine();

        System.out.print("Введите пароль: ");
        String password1 = sc.nextLine();

        System.out.print("Введите пароль: ");
        String password2 = sc.nextLine();

        if (!password1.equals(password2)) {
            System.out.println("Пароли не совпадают!");
            return;
        }

        try {
            String hexPass = getMd2(password1);
            User user = new User(name, hexPass);

            Request request = new Request("register", new String[0], null, user);
            Response response = updClient.send(request);

            System.out.println(response.message());
            if (response.success()) {
                currentUser = user;
            }

        } catch (Exception e) {
            System.out.println("Ошибка при регистрации: " + e.getMessage());
        }
    }

    private void loginCommand() {

        System.out.print("Введите имя пользователя: ");
        String name = sc.nextLine();

        System.out.print("Введите пароль: ");
        String password = sc.nextLine();

        try {
            String hexPass = getMd2(password);
            currentUser = new User(name, hexPass);
        } catch (Exception e) {
            System.out.println("Ошибка при входе: " + e.getMessage());
        }
    }

    private String getMd2(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD2");
        byte[] hashBytes = md.digest(str.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
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
