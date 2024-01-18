package com.itmo.telegram.entity;

import lombok.Getter;

@Getter
public enum TaskStatus {
    NOT_STARTED("not started"),
    IN_PROGRESS("in progress"),
    FINISHED("finished"),
    FAILED("failed");

    private final String name;

    TaskStatus(String name){
        this.name = name;
    }


}
