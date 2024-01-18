package com.itmo.telegram.exception;

public class WrongEnumArgument extends Exception {
    public WrongEnumArgument(String name) {
        super(name);
    }
}
