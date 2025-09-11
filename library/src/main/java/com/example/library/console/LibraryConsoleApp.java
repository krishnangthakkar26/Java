//package com.example.library.console;
//
//import com.example.library.model.User;
//import com.example.library.service.UserService;
//import com.example.library.service.BookService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Scanner;
//
//@Component
//public class LibraryConsoleApp {
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private BookService bookService;
//
//    private final Scanner sc = new Scanner(System.in);
//
//    public void start() {
//        while (true) {
//            System.out.println("\n--- Library Management ---");
//            System.out.println("1. Sign Up");
//            System.out.println("2. Login");
//            System.out.println("3. Exit");
//            System.out.print("Choose: ");
//            int choice = Integer.parseInt(sc.nextLine());
//
//            switch (choice) {
//                case 1 -> signUp();
//                case 2 -> login();
//                case 3 -> {
//                    System.out.println("Goodbye!");
//                    System.exit(0);
//                }
//                default -> System.out.println("Invalid choice!");
//            }
//        }
//    }
//
//    private void signUp() {
//        System.out.print("Enter username: ");
//        String username = sc.nextLine();
//        System.out.print("Enter password: ");
//        String password = sc.nextLine();
//
//        if (username.equalsIgnoreCase("admin")) {
//            System.out.println("❌ Cannot sign up as admin!");
//            return;
//        }
//
//        if (userService.signUp(username, password)) {
//            System.out.println("✅ User registered successfully! Now login to continue.");
//        } else {
//            System.out.println("⚠️ Username already exists, try another one.");
//        }
//    }
//
//    private void login() {
//        System.out.print("Enter username: ");
//        String username = sc.nextLine();
//        System.out.print("Enter password: ");
//        String password = sc.nextLine();
//
//        userService.login(username, password).ifPresentOrElse(user -> {
//            if (user.isAdmin()) {
//                adminMenu();
//            } else {
//                userMenu(user);
//            }
//        }, () -> System.out.println("❌ Invalid credentials! Please sign up first."));
//    }
//
//    private void adminMenu() {
//        while (true) {
//            System.out.println("\n--- Admin Menu ---");
//            System.out.println("1. View Books");
//            System.out.println("2. Add Book");
//            System.out.println("3. Delete Book");
//            System.out.println("4. Logout");
//            System.out.print("Choose: ");
//            int choice = Integer.parseInt(sc.nextLine());
//
//            switch (choice) {
//                case 1 -> bookService.listBooks().forEach(b ->
//                        System.out.println(b.getId() + ". " + b.getTitle() + " (" + b.getNoOfCopies() + " copies)")
//                );
//                case 2 -> {
//                    System.out.print("Title: ");
//                    String title = sc.nextLine();
//                    System.out.print("Author: ");
//                    String author = sc.nextLine();
//                    System.out.print("ISBN: ");
//                    String isbn = sc.nextLine();
//                    System.out.print("Copies: ");
//                    int copies = Integer.parseInt(sc.nextLine());
//                    bookService.addBook(title, author, isbn, copies);
//                }
//                case 3 -> {
//                    System.out.print("Enter book ID to delete: ");
//                    Long id = Long.parseLong(sc.nextLine());
//                    bookService.deleteBook(id);
//                }
//                case 4 -> { return; }
//            }
//        }
//    }
//
//    private void userMenu(User user) {
//        while (true) {
//            System.out.println("\n--- User Menu ---");
//            System.out.println("1. View Books");
//            System.out.println("2. Borrow Book");
//            System.out.println("3. Return Book");
//            System.out.println("4. Logout");
//            System.out.print("Choose: ");
//            int choice = Integer.parseInt(sc.nextLine());
//
//            switch (choice) {
//                case 1 -> bookService.listBooks().forEach(b ->
//                        System.out.println(b.getId() + ". " + b.getTitle() + " (" + b.getNoOfCopies() + " copies) Borrowed By: " +
//                                (b.getBorrowedBy() == null ? "Available" : b.getBorrowedBy().getUsername()))
//                );
//                case 2 -> {
//                    System.out.print("Enter book ID to borrow: ");
//                    Long id = Long.parseLong(sc.nextLine());
//                    if (bookService.borrowBook(id, user))
//                        System.out.println("✅ Book borrowed successfully!");
//                    else
//                        System.out.println("⚠️ Unable to borrow this book!");
//                }
//                case 3 -> {
//                    System.out.print("Enter book ID to return: ");
//                    Long id = Long.parseLong(sc.nextLine());
//                    if (bookService.returnBook(id, user))
//                        System.out.println("✅ Book returned successfully!");
//                    else
//                        System.out.println("⚠️ You cannot return this book!");
//                }
//                case 4 -> { return; }
//            }
//        }
//    }
//}
