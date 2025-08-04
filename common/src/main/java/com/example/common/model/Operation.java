package com.example.common.model;

import lombok.Getter;

@Getter
public enum Operation {
    SUM("sum"),
    SUBTRACT("subtract"),
    MULTIPLY("multiply"),
    DIVIDE("divide");

    private final String operation;

    Operation(String operation) {
        this.operation = operation;
    }

    public static Operation fromString(String operation) {
        for (Operation op : Operation.values()) {
            if (op.operation.equalsIgnoreCase(operation)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operation: " + operation);
    }
    public static boolean isValidOperation(String operation) {
        for (Operation op : Operation.values()) {
            if (op.operation.equalsIgnoreCase(operation)) {
                return true;
            }
        }
        return false;
    }
}
