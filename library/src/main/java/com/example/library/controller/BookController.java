package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.response.ApiResponse;
import com.example.library.service.BookService;
import com.example.library.service.UserService;
import com.example.library.dto.BorrowedBookDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<ApiResponse<List<Book>>> listBooks() {
        List<Book> books = bookService.listBooks();
        return ResponseEntity.ok(ApiResponse.success("Books fetched successfully", books));
    }

    // ✅ UPDATED: Add book with optional image

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<ApiResponse<String>> addBook(@RequestParam String title,
                                                     @RequestParam String author,
                                                     @RequestParam String isbn,
                                                     @RequestParam int  noOfCopies,
                                                     @RequestParam(required = false) MultipartFile file) {
        String imagePath = null;

        try {
            // 🔹 DEBUG: Check if file is received
            if (file != null) {
                System.out.println("📂 Received file: " + file.getOriginalFilename()
                        + " | Size: " + file.getSize());
            } else {
                System.out.println("⚠️ No file received (file is null)");
            }

            if (file != null && !file.isEmpty()) {
                String uploadDirPath = System.getProperty("user.dir") + File.separator + "uploads";
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir, filename);
                file.transferTo(dest);

                imagePath = "/images/" + filename;
                System.out.println("✅ Saved file: " + dest.getAbsolutePath());
            }

            bookService.addBookWithImage(title, author, isbn, noOfCopies, imagePath);
            return ResponseEntity.ok(ApiResponse.success("Book added successfully", imagePath));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.failure("Error uploading image"));
        }
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<Void>> updateBook(@PathVariable Long id,
//                                                        @RequestParam(required = false) String title,
//                                                        @RequestParam(required = false) String author,
//                                                        @RequestParam(required = false) String isbn,
//                                                        @RequestParam(required = false) Integer copies) {
//        boolean updated = bookService.updateBook(id, title, author, isbn, copies);
//
//        if (updated) {
//            return ResponseEntity.ok(ApiResponse.success("Book updated successfully!"));
//        } else {
//            return ResponseEntity.badRequest().body(ApiResponse.failure("Book not found"));
//        }
//    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateBook(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Integer copies,
            @RequestParam(required = false) MultipartFile file) {

        try {
            String imagePath = null;

            // ✅ Save new image if uploaded
            if (file != null && !file.isEmpty()) {
                String uploadDirPath = System.getProperty("user.dir") + File.separator + "uploads";
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir, filename);
                file.transferTo(dest);

                imagePath = "/images/" + filename;
                System.out.println("✅ Updated image: " + dest.getAbsolutePath());
            }

            boolean updated = bookService.updateBook(id, title, author, isbn, copies, imagePath);

            if (updated) {
                return ResponseEntity.ok(ApiResponse.success("Book updated successfully!"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.failure("Book not found"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.failure("Error updating image"));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted"));
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<ApiResponse<Void>> borrowBook(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam String password) {

        var userOpt = userService.login(username, password);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("Invalid user"));
        }

        User user = userOpt.get();
        if (bookService.borrowBook(id, user)) {
            return ResponseEntity.ok(ApiResponse.success("Book borrowed successfully!"));
        }

        return ResponseEntity.badRequest().body(ApiResponse.failure("Unable to borrow this book"));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<Void>> returnBook(@PathVariable Long id,
                                                        @RequestParam String username,
                                                        @RequestParam String password) {
        var userOpt = userService.login(username, password);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("Invalid user"));
        }

        User user = userOpt.get();
        if (bookService.returnBook(id, user)) {
            return ResponseEntity.ok(ApiResponse.success("Book returned successfully!"));
        }

        return ResponseEntity.badRequest().body(ApiResponse.failure("Unable to return this book"));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<ApiResponse<List<BorrowedBookDTO>>> getBorrowedBooks(
            @RequestParam String username,
            @RequestParam String password) {

        var userOpt = userService.login(username, password);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse.failure("Unauthorized"));
        }

        User user = userOpt.get();
        List<BorrowedBookDTO> borrowedBooks = bookService.getBorrowedBooksByUser(user);

        return ResponseEntity.ok(ApiResponse.success("Borrowed books fetched successfully", borrowedBooks));
    }

    // ✅ Still available: upload image separately
    @PostMapping("/{id}/uploadImage")
    public ResponseEntity<ApiResponse<String>> uploadBookImage(@PathVariable Long id,
                                                               @RequestParam("file") MultipartFile file) {
        try {
            String uploadDirPath = System.getProperty("user.dir") + File.separator + "uploads";
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir, filename);
            file.transferTo(dest);

            String imagePath = "/images/" + filename;
            System.out.println("✅ Saved file: " + dest.getAbsolutePath());

            boolean updated = bookService.updateBookImage(id, imagePath);

            if (updated) {
                return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", imagePath));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.failure("Book not found"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.failure("Error saving file"));
        }
    }
}
