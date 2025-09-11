package com.example.library;

//import com.example.library.console.LibraryConsoleApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//@SpringBootApplication
//public class LibraryApplication {
//    public static void main(String[] args) {
//        ApplicationContext context = SpringApplication.run(LibraryApplication.class, args);
//        LibraryConsoleApp consoleApp = context.getBean(LibraryConsoleApp.class);
//        consoleApp.start();
//    }
//}
@SpringBootApplication
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
