package com.lms;

import java.util.Map;
import java.time.Year;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.lms.exceptions.UserExistsException;
import com.lms.exceptions.BookNotFoundException;
import com.lms.exceptions.PermissionDeniedException;
import com.lms.exceptions.BookBorrowedException;


class LibraryTest {

    Library library = new Library("BIT MESRA");
    @Test
    public void testShouldFailWithoutProperConstructor() {
        assertNotNull(library);
    }

    @Test
    public void testLibraryNameShouldNotbeNull() {
        assertThrows(IllegalArgumentException.class, () -> new Library(null));
    }

    @Test
    public void testLibraryNameShouldNotBeEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Library(""));
    }

    @Test
    public void testLibraryNameShouldBeGreaterThan2Characters() {
        assertThrows(IllegalArgumentException.class, () -> new Library("BI"));
    }

    @Test
    public void testShouldThrowExceptionIfUserIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> library.addUser(null));
        assertEquals("User should not be null", exception.getMessage());
    }

    @Test
    public void testShouldAllowOnlyPermittedUserToAddBook() {
        User user = new User("Yash", User.Role.LIBRARIAN);

        Book book = new Book("9780060977498", "The God of Small Things", "Arundhati Roy", Year.of(1997));
        library.addBook(user, book);

        Book storedBook = library.getBookByISBN("9780060977498");

        assertNotNull(storedBook);
        assertEquals(book, storedBook);
    }

    @Test
    public void testShouldThrowExceptionIfUnauthorizedUserAddBook() {
        User user = new User("Rahul", User.Role.USER);

        Book book = new Book("9780099578512", "Midnight's Children", "Salman Rushdie", Year.of(1981));
        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () -> library.addBook(user, book));
        assertEquals("You are not authorized to add book", exception.getMessage());
    }

    @Test
    public void testShouldAddUserToLibrary() {
        User librarian = new User("Yash", User.Role.LIBRARIAN);

        library.addUser(librarian);

        User user = library.getUserByName("Yash");
        assertEquals(librarian, user);
    }

    @Test
    public void testShouldNotAllowDuplicateUsers() {
        User primaryLibrarian = new User("Yash", User.Role.LIBRARIAN);
        User secondaryLibrarian = new User("Yash", User.Role.LIBRARIAN);

        library.addUser(primaryLibrarian);
        UserExistsException exception = assertThrows(UserExistsException.class, () -> library.addUser(secondaryLibrarian));
        assertEquals("User already exists in list", exception.getMessage());
    }

    @Test
    public void testShouldFetchUserByUsername() {
        User primaryLibrarian = new User("Yash", User.Role.LIBRARIAN);

        library.addUser(primaryLibrarian);
        User fetchedUser = library.getUserByName("Yash");
        assertEquals(primaryLibrarian, fetchedUser);
    }

    @Test
    public void testShouldRetrieveAllAvailableBooks() {
        User librarian = new User("Yash", User.Role.LIBRARIAN);
        //Book book1 = new Book("9780132350884", "Clean Code", "Robert Cecil Martin", Year.of(2012));
        //Book book2 = new Book("9780134685991", "Effective Java", "Joshua Bloch", Year.of(2018));
        Book book1 = new Book("9780060977498", "The God of Small Things", "Arundhati Roy", Year.of(1997));
        Book book2 = new Book("9780099578512", "Midnight's Children", "Salman Rushdie", Year.of(1981));
        library.addUser(librarian);
        library.addBook(librarian, book1);
        library.addBook(librarian, book2);

        Map<String, Book> availableBooks = library.viewAvailableBooks();

        assertEquals(2, availableBooks.size());
        assertTrue(availableBooks.containsKey("9780060977498"));
        assertTrue(availableBooks.containsKey("9780099578512"));
    }

    @Test
    public void testShouldReturnUnmodifiableHashMap() {
        User librarian = new User("Yash", User.Role.LIBRARIAN);
        Book book1 = new Book("9780060977498", "The God of Small Things", "Arundhati Roy", Year.of(1997));


        library.addUser(librarian);
        library.addBook(librarian, book1);

        Map<String, Book> availableBooks = library.viewAvailableBooks();

        assertThrows(UnsupportedOperationException.class, () -> availableBooks.put("9780134685991", new Book("9780134685991", "A Suitable Boy", "Vikram Seth", Year.of(1993))));
    }

    @Test
    public void testShouldAllowToBorrowBookFromLibrary() {
        User librarian = new User("Yash", User.Role.LIBRARIAN);
        User user = new User("Ram", User.Role.USER);
        Book book = new Book("9780060977498", "The God of Small Things", "Arundhati Roy", Year.of(1997));

        library.addUser(librarian);
        library.addUser(user);
        library.addBook(librarian, book);

        library.borrowBook(user, "9780060977498");

        Book borrowedBook = library.getBookByISBN("9780060977498");
        assertNull(borrowedBook, "borrowedBook should be null as it has been borrowed earlier.");
    }

    @Test
    public void testShouldThrowExceptionWhenBookNotFoundDuringBorrowRequest() {

        User user = new User("Raj", User.Role.USER);

        library.addUser(user);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> library.borrowBook(user, "9780060977498"));
        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    public void testShouldThrowExceptionWhenBookIsAlreadyBorrowed() {

        User librarian = new User("Yash", User.Role.LIBRARIAN);
        User user1 = new User("Rahul", User.Role.USER);
        User user2 = new User("Raj", User.Role.USER);
        Book book = new Book("9780060977498", "The God of Small Things", "Arundhati Roy", Year.of(1997));

        library.addUser(librarian);
        library.addUser(user1);
        library.addUser(user2);
        library.addBook(librarian, book);

        library.borrowBook(user1, "9780060977498");

        BookBorrowedException exception = assertThrows(BookBorrowedException.class, () -> library.borrowBook(user2, "9780060977498"));
        assertEquals("Book is already borrowed", exception.getMessage());
    }

    @Test
    public void testShouldReturnBorrowerNameWhoBorrowedBook() {
        User librarian = new User("Yash", User.Role.LIBRARIAN);
        User user = new User("Rahul", User.Role.USER);
        Book book = new Book("9780060977498", "The God of Small Things", "Arundhati Roy", Year.of(1997));

        library.addUser(librarian);
        library.addUser(user);
        library.addBook(librarian, book);

        library.borrowBook(user, "9780060977498");

        String borrowerName = library.getBorrowerNameByISBN("9780060977498");

        assertEquals(user.getUserName(), borrowerName);
    }

    @Test
    public void testShouldAllowUserToReturnBookToLibrary() {
        User librarian = new User("Yash", User.Role.LIBRARIAN);
        User user = new User("Rahul", User.Role.USER);
        Book book = new Book("9780060977498", "The God of Small Things", "Arundhati Roy", Year.of(1997));

        library.addUser(librarian);
        library.addUser(user);
        library.addBook(librarian, book);

        library.borrowBook(user, "9780060977498");
        library.returnBook(user, "9780060977498");

        Book returnedBook = library.getBookByISBN("9780060977498");
        assertNotNull(returnedBook, "Returned book have be available in the books catalog.");
    }

    @Test
    public void testShouldThrowExceptionWhenUserReturnsBookThatIsNotBorrowedByHim() {
        User librarian = new User("Yash", User.Role.LIBRARIAN);
        User user1 = new User("Rahul", User.Role.USER);
        User user2 = new User("Raj", User.Role.USER);
        Book book = new Book("9780060977498", "The God of Small Things", "Arundhati Roy", Year.of(1997));

        library.addUser(librarian);
        library.addUser(user1);
        library.addUser(user2);
        library.addBook(librarian, book);

        library.borrowBook(user1, "9780060977498");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> library.returnBook(user2, "9780060977498"));
        assertEquals("book was not borrowed by this user", exception.getMessage());
    }

    @Test
    public void testShouldThrowExceptionWhenNoOneBorrowedBook() {
        User librarian = new User("Yash", User.Role.LIBRARIAN);
        User user1 = new User("Rahul", User.Role.USER);
        Book book = new Book("9780060977498", "The God of Small Things", "Arundhati Roy", Year.of(1997));

        library.addUser(librarian);
        library.addUser(user1);
        library.addBook(librarian, book);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> library.returnBook(user1, "9780060977498"));
        assertEquals("Book was not borrowed by any user", exception.getMessage());
    }


}
