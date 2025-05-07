package ru.ifmo.lab6.server.database;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);

    private static Session sshSession;
    private static Connection connection;

    private static final String SSH_USER = "s467363";
    private static final String SSH_PASSWORD = "tim14112006";
    private static final String SSH_HOST = "se.ifmo.ru";
    private static final int SSH_PORT = 2222;

    private static final String DB_USER = "s467363";
    private static final String DB_PASSWORD = "8rAixtRfAK5JRV0O";
    private static final String DB_NAME = "studs";
    private static final String REMOTE_DB_HOST = "localhost";
    private static final int REMOTE_DB_PORT = 5432;
    private static final int LOCAL_FORWARD_PORT = 5432;

    private static boolean initialized = false;

    public static synchronized Connection getConnection() throws Exception {
        if (!initialized) {
            setupSshTunnel();
            setupDatabaseConnection();
            initialized = true;
        }

        if (connection == null || connection.isClosed()) {
            setupDatabaseConnection();
        }

        return connection;
    }

    private static void setupSshTunnel() throws Exception {
        if (sshSession != null && sshSession.isConnected()) return;

        JSch jsch = new JSch();
        sshSession = jsch.getSession(SSH_USER, SSH_HOST, SSH_PORT);
        sshSession.setPassword(SSH_PASSWORD);
        sshSession.setConfig("StrictHostKeyChecking", "no");
        sshSession.connect();

        sshSession.setPortForwardingL(LOCAL_FORWARD_PORT, REMOTE_DB_HOST, REMOTE_DB_PORT);

        logger.info("SSH соединение установлено");
    }

    private static void setupDatabaseConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:" + LOCAL_FORWARD_PORT + "/" + DB_NAME;
        connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);

        logger.info("Соединение с базой данной установлено");
    }

    public static synchronized void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
            if (sshSession != null && sshSession.isConnected()) sshSession.disconnect();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            connection = null;
            sshSession = null;
            initialized = false;
        }
    }
}
