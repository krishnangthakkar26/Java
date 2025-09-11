package com.example.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.library.model.Book;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
}
