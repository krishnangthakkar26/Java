package com.example.library.controller;

import com.example.library.dto.UserDTO;
import com.example.library.dto.AdminUserDTO;
import com.example.library.model.User;
import com.example.library.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //ser Signup
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestParam String username,
                                         @RequestParam String password) {
        if (username.equalsIgnoreCase("admin")) {
            return ResponseEntity.badRequest().body("❌ Cannot sign up as admin!");
        }
        if (userService.signUp(username, password)) {
            return ResponseEntity.ok("✅ User registered successfully!");
        }
        return ResponseEntity.badRequest().body("⚠️ Username already exists!");
    }

    //User Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password) {
        Optional<User> userOpt = userService.login(username, password);

        if (userOpt.isEmpty() || userOpt.get().isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Invalid credentials");
        }

        User user = userOpt.get();
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.isAdmin()));
    }

    //Admin Login
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestParam String username,
                                        @RequestParam String password) {
        Optional<User> userOpt = userService.login(username, password);

        if (userOpt.isEmpty() || !userOpt.get().isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Invalid admin credentials");
        }

        User admin = userOpt.get();
        return ResponseEntity.ok(new UserDTO(admin.getId(), admin.getUsername(), admin.isAdmin()));
    }

    //List Users
    @GetMapping
    public List<UserDTO> listUsers() {
        return userService.getAllUsers().stream()
                .filter(u -> !u.isAdmin())
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.isAdmin()))
                .toList();
    }

    // List Users for Admin (with borrowed books)
    @GetMapping("/admin/list")
    public List<AdminUserDTO> listUsersForAdmin() {
        return userService.getAllUsersWithBorrowCount();
    }

    // Delete User (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok("✅ User deleted successfully!");
        }
        return ResponseEntity.badRequest().body("⚠️ Cannot delete this user");
    }
}
