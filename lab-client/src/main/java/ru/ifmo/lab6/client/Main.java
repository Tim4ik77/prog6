package ru.ifmo.lab6.client;

import ru.ifmo.lab6.client.program.ConsoleApp;
import ru.ifmo.lab6.client.program.UPDClient;

public class Main {
    public static void main(String[] args) {
        UPDClient updClient = new UPDClient("localhost", 5000, 3000);
        ConsoleApp app = new ConsoleApp(updClient);
        app.run();
    }
}
