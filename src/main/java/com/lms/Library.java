package com.lms;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.lms.exceptions.UserExistsException;
import com.lms.exceptions.BookNotFoundException;
import com.lms.exceptions.PermissionDeniedException;
import com.lms.exceptions.BookBorrowedException;

import static com.lms.utils.UserValidator.validateUser;
import static com.lms.utils.StringValidator.validateString;
import static com.lms.utils.BookValidator.validateBookNotNull;

public class Library {

    String name;
    private final Map<String, Book> bookList;
    private final Map<String, User> userList;
    private final Map<String, String> borrowedBooksList;
    private final Map<String, Book> borrowedBookDetails;

    public Library(String name) {
        validateString(name, "Library Name Should not be null or empty");
        if(name.length() <= 2) {
            throw new IllegalArgumentException("Library Name Should have at least 2 characters");
        }
        this.name = name;
        this.bookList = new HashMap<String, Book>();
        this.userList = new HashMap<String, User>();
        this.borrowedBooksList = new HashMap<String, String>();
        this.borrowedBookDetails = new HashMap<String, Book>();
    }

    public void addUser(User user) {
        validateUser(user, "User should not be null");
        if(userList.containsKey(user.getUserName())){
            throw new UserExistsException("User already exists in list");
        }
        userList.put(user.getUserName(), user);
    }

    public User getUserByName(String userName) {
        return userList.get(userName);
    }

    public void addBook(User user, Book book) {
        validateUser(user, "User should not be null");
        validateBookNotNull(book,"Book not found");
        if(user.isLibrarian()){
            bookList.put(book.getISBN(), book);
        } else {
            throw new PermissionDeniedException("You are not authorized to add book");
        }
    }

    private boolean listOfBorrowedBooks(String isbn) {
        return borrowedBooksList.containsKey(isbn);
    }

    public void borrowBook(User user, String isbn) {
        validateUser(user, "User should not be null");
        Book book = bookList.get(isbn);

        if(listOfBorrowedBooks(isbn)) {
            throw new BookBorrowedException("Book is already borrowed");
        }

        validateBookNotNull(book,"Book not found");

        borrowedBooksList.put(isbn, user.getUserName());
        borrowedBookDetails.put(isbn, book);
        bookList.remove(isbn);
    }

    public void returnBook(User user, String isbn) {
        validateUser(user, "User should not be null");
        if(!borrowedBooksList.containsKey(isbn)) {
            throw new BookNotFoundException("Book was not borrowed by any user");
        }
        if( !user.getUserName().equals(borrowedBooksList.get(isbn))){
            throw new IllegalArgumentException("book was not borrowed by this user");
        }
        Book book = getBookByISBNFromBorrowedBook(isbn);
        bookList.put(isbn, book);
        borrowedBooksList.remove(isbn);
    }

    public String getBorrowerNameByISBN(String isbn) {
        return borrowedBooksList.get(isbn);
    }

    public Map<String, Book> viewAvailableBooks() {
        return Collections.unmodifiableMap(new HashMap<>(bookList));
    }

    public Book getBookByISBNFromBorrowedBook(String isbn) {
        return borrowedBookDetails.get(isbn);
    }

    public Book getBookByISBN(String isbn) {
        return bookList.get(isbn);
    }
}
