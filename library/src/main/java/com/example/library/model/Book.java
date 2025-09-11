package com.example.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String isbn;
    private int noOfCopies;

    @ManyToOne
    @JoinColumn(name = "borrowed_by_id")
    private User borrowedBy;

    public Book() {}

    public Book(String title, String author, String isbn, int noOfCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.noOfCopies = noOfCopies;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getNoOfCopies() { return noOfCopies; }
    public void setNoOfCopies(int noOfCopies) { this.noOfCopies = noOfCopies; }

    public User getBorrowedBy() { return borrowedBy; }
    public void setBorrowedBy(User borrowedBy) { this.borrowedBy = borrowedBy; }
}
