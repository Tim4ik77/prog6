package ru.ifmo.lab6.server.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.lab6.common.network.Request;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.common.network.User;
import ru.ifmo.lab6.server.commands.*;
import ru.ifmo.lab6.server.database.UserDataBaseService;
import ru.ifmo.lab6.server.program.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    private final int historyLength = 15;
    private HashMap<String, Command> commands;
    private ArrayList<String> history;

    public CommandManager() {
        logger.info("Инициализация CommandManager...");

        history = new ArrayList<>();
        commands = new HashMap<>();

        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("add", new AddCommand());
        commands.put("update", new UpdateCommand());
        commands.put("remove_by_id", new RemoveCommand());
        commands.put("clear", new ClearCommand());
        commands.put("insert_at", new InsertCommand());
        commands.put("remove_greater", new RemoveGreaterCommand());
        commands.put("history", new HistoryCommand());
        commands.put("average_of_students_count", new AverageCountCommand());
        commands.put("filter_contains_name", new FilterCommand());
        commands.put("print_field_descending_transferred_students", new PrintTransferredStudentsCommand());

        logger.info("Зарегистрировано {} команд.", commands.size());
    }

    private void updateHistory(String commandName) {
        history.add(commandName);
        if (history.size() > historyLength) {
            history.subList(0, history.size() - historyLength).clear();
        }
        logger.debug("История обновлена: {}", history);
    }

    public Response processCommand(Request request) {
        String commandName = request.command();
        Response resp;

        if (request.user() == null) {
            return new Response("Не авторизованный пользователь", false);
        }

        UserDataBaseService usDb = Server.getUserDataBaseService();
        User user = request.user();

        if (commandName.equals("register")) {
            try {
                usDb.registerNewUser(user.name(), user.password());
                return new Response("Успешно зарегестрирован", true);
            } catch (Exception e) {
                return new Response("Не удалось зарегестрировать пользователя", false);
            }
        }

        if (!usDb.validateCredentials(user.name(), user.password())) {
            return new Response("Не верные данные для входа!", false);
        }

        if (commands.containsKey(commandName)) {
            Command command = commands.get(commandName);
            resp = command.execute(request.args(), request.obj(), user.name());
            logger.info("Команда '{}' успешно выполнена.", commandName);
        } else {
            resp = new Response("Введено неправильное имя команды!");
            logger.warn("Попытка выполнить неизвестную команду: '{}'", commandName);
        }

        updateHistory(commandName);
        return resp;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }
}
