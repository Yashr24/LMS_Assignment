package com.lms.utils;

import com.lms.Book;
import com.lms.exceptions.BookNotFoundException;

public class BookValidator {
    public static void validateBookNotNull(Book book, String message) {
        if (book == null) {
            throw new BookNotFoundException(message);
        }
    }
}
