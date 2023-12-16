package ru.practicum.shareit.exception;

public class UnknownItemRequestException extends RuntimeException {
    public UnknownItemRequestException(String message) {
        super(message);
    }
}
