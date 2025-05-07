package ru.ifmo.lab6.client.program;

import ru.ifmo.lab6.common.exceptions.InvalidResponseException;
import ru.ifmo.lab6.common.network.Request;
import ru.ifmo.lab6.common.network.Response;
import ru.ifmo.lab6.common.network.SerializationUtils;

import java.io.IOException;
import java.net.*;

public class UPDClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    public UPDClient(String host, int port, int timeout) {
        try {
            address = InetAddress.getByName(host);
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout);
        } catch (SocketException | UnknownHostException e) {
            System.out.println("Не удалось запустить клиент!");
            System.exit(1);
        }
        this.port = port;
    }

    public Response send(Request request) throws InvalidResponseException {
        try {
            byte[] data = SerializationUtils.serialize(request);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);

            byte[] responseBuffer = new byte[65535];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            return (Response) SerializationUtils.deserialize(responsePacket.getData());
        } catch (IOException|ClassNotFoundException e) {
            System.out.println(e.getMessage());
            throw new InvalidResponseException("При выполнении запроса произошла ошибка!");
        }
    }
}
