package ru.ifmo.lab6.common.network;

import java.io.*;

public class SerializationUtils {

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(obj);
        objStream.flush();
        return byteStream.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        ObjectInputStream objStream = new ObjectInputStream(byteStream);
        return objStream.readObject();
    }
}

