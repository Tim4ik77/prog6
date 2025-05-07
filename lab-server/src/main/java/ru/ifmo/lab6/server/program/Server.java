package ru.ifmo.lab6.server.program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.lab6.common.network.Request;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.common.network.SerializationUtils;
import ru.ifmo.lab6.server.database.StudyGroupDataBaseService;
import ru.ifmo.lab6.server.database.UserDataBaseService;
import ru.ifmo.lab6.server.managers.*;
import ru.ifmo.lab6.server.utils.JsonSaver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final int port;
    private static CommandManager commandManager;
    private static CollectionManager collectionManager;
    private static UserDataBaseService userDataBaseService;
    private static StudyGroupDataBaseService studyGroupDataBaseService;

    private final ExecutorService readExecutor = Executors.newCachedThreadPool();
    private final ExecutorService processExecutor = Executors.newCachedThreadPool();
    private final ForkJoinPool sendPool = new ForkJoinPool();

    private volatile boolean running = true;

    public Server(int port, CommandManager commandManager, CollectionManager collectionManager, UserDataBaseService userDataBaseService, StudyGroupDataBaseService studyGroupDataBaseService) {
        this.port = port;
        Server.commandManager = commandManager;
        Server.collectionManager = collectionManager;
        Server.userDataBaseService = userDataBaseService;
        Server.studyGroupDataBaseService = studyGroupDataBaseService;
    }

    public static CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static UserDataBaseService getUserDataBaseService() {
        return userDataBaseService;
    }
    public static StudyGroupDataBaseService getStudyGroupDataBaseService() {
        return studyGroupDataBaseService;
    }

    public void run() {
        try (DatagramChannel serverChannel = DatagramChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port));

            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_READ);

            logger.info("Сервер запущен на порту {}", port);

            while (running) {
                selector.select(100);

                if (System.in.available() > 0 && scanner.hasNextLine()) {
                    handleConsoleCommand(scanner.nextLine());
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isReadable()) {
                        DatagramChannel channel = (DatagramChannel) key.channel();
                        readExecutor.submit(() -> receiveAndHandle(channel));
                    }
                }
            }

        } catch (IOException e) {
            logger.error("Ошибка в работе сервера: {}", e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void receiveAndHandle(DatagramChannel channel) {
        ByteBuffer buffer = ByteBuffer.allocate(65535);

        try {
            SocketAddress clientAddress = channel.receive(buffer);
            if (clientAddress == null) return;

            buffer.flip();
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);

            processExecutor.submit(() -> processRequest(data, channel, clientAddress));

        } catch (IOException e) {
            logger.error("Ошибка при чтении данных: {}", e.getMessage());
        }
    }

    private void processRequest(byte[] data, DatagramChannel channel, SocketAddress clientAddress) {
        try {
            Request request = (Request) SerializationUtils.deserialize(data);
            Response response = commandManager.processCommand(request);

            sendPool.submit(() -> sendResponse(response, channel, clientAddress));

        } catch (Exception e) {
            logger.error("Ошибка при обработке запроса: {}", e.getMessage());
        }
    }

    private void sendResponse(Response response, DatagramChannel channel, SocketAddress address) {
        try {
            byte[] data = SerializationUtils.serialize(response);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            channel.send(buffer, address);

            logger.info("Ответ отправлен клиенту.");
        } catch (IOException e) {
            logger.error("Ошибка при отправке ответа: {}", e.getMessage());
        }
    }

    private void handleConsoleCommand(String input) {
        switch (input.trim().toLowerCase()) {
            case "exit":
                shutdown();
            default:
                logger.warn("Неизвестная команда: {}", input);
        }
    }

    private void shutdown() {
        logger.info("Остановка сервера...");

        readExecutor.shutdown();
        processExecutor.shutdown();
        sendPool.shutdown();

        try {
            readExecutor.awaitTermination(3, TimeUnit.SECONDS);
            processExecutor.awaitTermination(3, TimeUnit.SECONDS);
            sendPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("Прерывание при завершении потоков.");
        }

        logger.info("Сервер остановлен.");
    }
}
