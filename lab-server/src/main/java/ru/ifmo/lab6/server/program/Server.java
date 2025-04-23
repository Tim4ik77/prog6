package ru.ifmo.lab6.server.program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.lab6.common.network.Request;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.common.network.SerializationUtils;
import ru.ifmo.lab6.server.managers.*;
import ru.ifmo.lab6.server.utils.JsonSaver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final int port;
    private static CommandManager commandManager;
    private static CollectionManager collectionManager;
    private DatagramChannel serverChannel;
    private Scanner sc;

    public Server(int port, CommandManager commandManager, CollectionManager collectionManager) {
        this.port = port;
        Server.commandManager = commandManager;
        Server.collectionManager = collectionManager;
        sc = new Scanner(System.in);
    }

    public static CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public void run() {
        try (DatagramChannel serverChannel = DatagramChannel.open()) {
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port));

            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_READ);

            ByteBuffer buffer = ByteBuffer.allocate(65535);
            logger.info("Сервер запущен на порту {}", port);

            while (true) {
                selector.select(100);

                if (System.in.available() > 0) {
                    executeChatCommand();
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isReadable()) {
                        DatagramChannel channel = (DatagramChannel) key.channel();

                        buffer.clear();
                        SocketAddress clientAddress = channel.receive(buffer);
                        if (clientAddress == null) continue;

                        buffer.flip();
                        byte[] receivedData = new byte[buffer.limit()];
                        buffer.get(receivedData);

                        try {
                            Request request = (Request) SerializationUtils.deserialize(receivedData);
                            Response resp = commandManager.processCommand(request);

                            byte[] responseData = SerializationUtils.serialize(resp);
                            ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
                            channel.send(responseBuffer, clientAddress);

                            logger.info("Ответ отправлен клиенту.");
                        } catch (ClassNotFoundException e) {
                            logger.error("Ошибка при десериализации: {}", e.getMessage());
                        }
                    }
                }
            }

        } catch (IOException e) {
            logger.error("Ошибка в работе сервера: {}", e.getMessage());
        }
    }

    private void executeChatCommand() {
        String input = sc.nextLine();

        if (input.equalsIgnoreCase("save") || input.equalsIgnoreCase("exit")) {
            handleSave();
            if (input.equalsIgnoreCase("exit")) {
                System.exit(0);
            }
        } else {
            logger.warn("Команда не найдена: {}", input);
        }
    }

    private void handleSave() {
        try {
            JsonSaver.saveFile(Server.getCollectionManager().getGroups());
            logger.info("Коллекция сохранена!");
        } catch (IOException e) {
            logger.error("Не удалось сохранить коллекцию: {}", e.getMessage());
        }
    }

}
