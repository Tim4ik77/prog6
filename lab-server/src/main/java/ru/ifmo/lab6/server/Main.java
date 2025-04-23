package ru.ifmo.lab6.server;

import ru.ifmo.lab6.server.managers.CollectionManager;
import ru.ifmo.lab6.server.managers.CommandManager;
import ru.ifmo.lab6.server.program.Server;

public class Main {
    public static void main(String[] args) {
        CommandManager commandManager = new CommandManager();
        CollectionManager collectionManager = new CollectionManager();
        Server server = new Server(5000, commandManager, collectionManager);
        server.run();
    }
}
