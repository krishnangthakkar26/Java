package com.example.library.dto;

public record BorrowedBookDTO(Long bookId, String title, String author, String isbn, boolean returned) {}
