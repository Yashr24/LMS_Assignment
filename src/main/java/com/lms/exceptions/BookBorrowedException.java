package com.lms.exceptions;

public class BookBorrowedException extends RuntimeException {
    public BookBorrowedException(String message) {
        super(message);
    }
}
