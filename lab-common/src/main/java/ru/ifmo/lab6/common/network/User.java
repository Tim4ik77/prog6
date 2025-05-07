package ru.ifmo.lab6.common.network;

import java.io.Serializable;

public record User(String name, String password) implements Serializable {
}
