package com.itmo.telegram.exception;

public class AlreadyJoinedException extends Exception {
    public AlreadyJoinedException(String message){
        super(message);
    }
}
