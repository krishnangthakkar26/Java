package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.service.BookService;
import com.example.library.service.UserService;
import com.example.library.dto.BorrowedBookDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final UserService userService;

    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/xyz")
    public List<Book> listBooks() {
        return bookService.listBooks();
    }

    @PostMapping
    public ResponseEntity<String> addBook(@RequestParam String title,
                                          @RequestParam String author,
                                          @RequestParam String isbn,
                                          @RequestParam int copies) {
        bookService.addBook(title, author, isbn, copies);
        return ResponseEntity.ok("Book added successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@PathVariable Long id,
                                             @RequestParam(required = false) String title,
                                             @RequestParam(required = false) String author,
                                             @RequestParam(required = false) String isbn,
                                             @RequestParam(required = false) Integer copies) {
        boolean updated = bookService.updateBook(id, title, author, isbn, copies);
        if (updated) {
            return ResponseEntity.ok("Book updated successfully!");
        } else {
            return ResponseEntity.badRequest().body("Book not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted");
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<String> borrowBook(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam String password) {
        var userOpt = userService.login(username, password);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid user");

        User user = userOpt.get();
        if (bookService.borrowBook(id, user)) {
            return ResponseEntity.ok("Book borrowed successfully!");
        }
        return ResponseEntity.badRequest().body("Unable to borrow this book");
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<String> returnBook(@PathVariable Long id,
                                             @RequestParam String username,
                                             @RequestParam String password) {
        var userOpt = userService.login(username, password);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid user");

        User user = userOpt.get();
        if (bookService.returnBook(id, user)) {
            return ResponseEntity.ok("Book returned successfully!");
        }
        return ResponseEntity.badRequest().body("Unable to return this book");
    }

    @GetMapping("/borrowed")
    public ResponseEntity<List<BorrowedBookDTO>> getBorrowedBooks(
            @RequestParam String username,
            @RequestParam String password) {

        var userOpt = userService.login(username, password);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        List<BorrowedBookDTO> borrowedBooks = bookService.getBorrowedBooksByUser(user);
        return ResponseEntity.ok(borrowedBooks);
    }
}
