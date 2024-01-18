package com.itmo.telegram.entity;

import com.itmo.telegram.exception.WrongEnumArgument;
import lombok.Getter;

@Getter
public enum Priority {
    LOW(0,"low"),
    NORMAL(1, "normal"),
    HIGH(2,"high");

    private final int priorityNumber;
    private final String priorityName;

    Priority(int priorityNumber, String priorityName){
        this.priorityName = priorityName;
        this.priorityNumber = priorityNumber;
    }

    public static Priority getByName(String name) throws WrongEnumArgument {
        for (Priority priority : values()){
            if (priority.name().equalsIgnoreCase(name)) return priority;
        }
        throw new WrongEnumArgument("No priority with given name");
    }

    public static Priority getByNumber(Integer integer) throws WrongEnumArgument {
        return switch (integer) {
            case 0 -> LOW;
            case 1 -> NORMAL;
            case 2 -> HIGH;
            default -> throw new WrongEnumArgument("No priority with given name");
        };
    }
}
