package com.example.optimal.criteria;

public enum SearchOperation {
    EQUALITY,       // :  (equals hoặc LIKE nếu String)
    GREATER_THAN,   // >
    LESS_THAN;      //

    public static SearchOperation fromSymbol(char symbol) {
        return switch (symbol) {
            case '>' -> GREATER_THAN;
            case '<' -> LESS_THAN;
            default -> EQUALITY;
        };
    }
}
