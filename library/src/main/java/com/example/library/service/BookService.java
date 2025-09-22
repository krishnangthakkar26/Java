package com.example.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.model.BorrowRecord;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.dto.BorrowedBookDTO;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    public List<Book> listBooks() {
        return bookRepository.findAll();
    }

    public void addBook(String title, String author, String isbn, int copies) {
        bookRepository.findByIsbn(isbn).ifPresentOrElse(existingBook -> {
            existingBook.setNoOfCopies(existingBook.getNoOfCopies() + copies);
            bookRepository.save(existingBook);
        }, () -> {
            bookRepository.save(new Book(title, author, isbn, copies));
        });
    }

    public void deleteBook(Long bookId) {
        bookRepository.findById(bookId).ifPresent(book -> {
            if (book.getNoOfCopies() > 1) {
                book.setNoOfCopies(book.getNoOfCopies() - 1);
                bookRepository.save(book);
            } else {
                bookRepository.delete(book);
            }
        });
    }

    public boolean borrowBook(Long bookId, User user) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book != null && book.getNoOfCopies() > 0) {
            if (borrowRecordRepository.findByUserAndBookAndReturnedFalse(user, book).isPresent()) {
                return false;
            }
            BorrowRecord record = new BorrowRecord(user, book);
            borrowRecordRepository.save(record);
            book.setNoOfCopies(book.getNoOfCopies() - 1);
            bookRepository.save(book);
            return true;
        }
        return false;
    }

    public boolean returnBook(Long bookId, User user) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book != null) {
            var recordOpt = borrowRecordRepository.findByUserAndBookAndReturnedFalse(user, book);
            if (recordOpt.isPresent()) {
                BorrowRecord record = recordOpt.get();
                record.setReturned(true);
                borrowRecordRepository.save(record);
                book.setNoOfCopies(book.getNoOfCopies() + 1);
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }

    // Fetch borrowed books per user
    public List<BorrowedBookDTO> getBorrowedBooksByUser(User user) {
        return borrowRecordRepository.findAll().stream()
                .filter(br -> br.getUser().getId().equals(user.getId()))
                .map(br -> new BorrowedBookDTO(
                        br.getBook().getId(),
                        br.getBook().getTitle(),
                        br.getBook().getAuthor(),
                        br.getBook().getIsbn(),
                        br.isReturned()
                ))
                .toList();
    }

    // Update book details (admin)
//    public boolean updateBook(Long bookId, String title, String author, String isbn, Integer copies) {
//        return bookRepository.findById(bookId).map(book -> {
//            if (title != null && !title.isEmpty()) book.setTitle(title);
//            if (author != null && !author.isEmpty()) book.setAuthor(author);
//            if (isbn != null && !isbn.isEmpty()) book.setIsbn(isbn);
//            if (copies != null && copies >= 0) book.setNoOfCopies(copies);
//            bookRepository.save(book);
//            return true;
//        }).orElse(false);
//    }

    public boolean updateBook(Long bookId, String title, String author, String isbn, Integer copies, String imagePath) {
        return bookRepository.findById(bookId).map(book -> {
            if (title != null && !title.isEmpty()) book.setTitle(title);
            if (author != null && !author.isEmpty()) book.setAuthor(author);
            if (isbn != null && !isbn.isEmpty()) book.setIsbn(isbn);
            if (copies != null && copies >= 0) book.setNoOfCopies(copies);
            if (imagePath != null && !imagePath.isEmpty()) book.setImagePath(imagePath); // ✅ update only if new file is uploaded
            bookRepository.save(book);
            return true;
        }).orElse(false);
    }


    // Update only the image path of a book
    public boolean updateBookImage(Long bookId, String imagePath) {
        return bookRepository.findById(bookId).map(book -> {
            book.setImagePath(imagePath);
            bookRepository.save(book);
            return true;
        }).orElse(false);
    }

    // Add book with optional image path
    public void addBookWithImage(String title, String author, String isbn, int copies, String imagePath) {
        bookRepository.findByIsbn(isbn).ifPresentOrElse(existingBook -> {
            existingBook.setNoOfCopies(existingBook.getNoOfCopies() + copies);
            if (imagePath != null) {
                existingBook.setImagePath(imagePath); // update image if provided
            }
            bookRepository.save(existingBook);
        }, () -> {
            Book newBook = new Book(title, author, isbn, copies);
            newBook.setImagePath(imagePath);
            bookRepository.save(newBook);
        });
    }

}
