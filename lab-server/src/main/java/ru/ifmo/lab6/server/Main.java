package ru.ifmo.lab6.server;

import ru.ifmo.lab6.server.database.DatabaseConnector;
import ru.ifmo.lab6.server.database.StudyGroupDataBaseService;
import ru.ifmo.lab6.server.database.UserDataBaseService;
import ru.ifmo.lab6.server.managers.CollectionManager;
import ru.ifmo.lab6.server.managers.CommandManager;
import ru.ifmo.lab6.server.program.Server;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection connection = DatabaseConnector.getConnection();
            UserDataBaseService userDataBaseService = new UserDataBaseService(connection);
            StudyGroupDataBaseService collectionDataBaseService = new StudyGroupDataBaseService(connection);

            userDataBaseService.init();
            collectionDataBaseService.init();

            CommandManager commandManager = new CommandManager();
            CollectionManager collectionManager = new CollectionManager(collectionDataBaseService);

            Server server = new Server(5000, commandManager, collectionManager, userDataBaseService, collectionDataBaseService);
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Аварийное завершение программы!");
        }
    }
}
