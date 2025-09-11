package com.example.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.library.model.BorrowRecord;
import com.example.library.model.User;
import com.example.library.model.Book;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    Optional<BorrowRecord> findByUserAndBookAndReturnedFalse(User user, Book book);

}
