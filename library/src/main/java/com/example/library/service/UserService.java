package com.example.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.dto.AdminUserDTO;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @PostConstruct
    public void initAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(new User("admin", "admin123", true));
        }
    }

    public Optional<User> login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    public boolean signUp(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false;
        }
        userRepository.save(new User(username, password, false));
        return true;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean deleteUser(Long id) {
        return userRepository.findById(id).map(u -> {
            if (!u.isAdmin()) {
                userRepository.delete(u);
                return true;
            }
            return false;
        }).orElse(false);
    }

    public List<AdminUserDTO> getAllUsersWithBorrowCount() {
        return userRepository.findAll().stream()
                .filter(u -> !u.isAdmin())
                .map(u -> {
                    int borrowedCount = (int) borrowRecordRepository.findAll().stream()
                            .filter(br -> br.getUser().getId().equals(u.getId()) && !br.isReturned())
                            .count();
                    return new AdminUserDTO(u.getId(), u.getUsername(), borrowedCount);
                })
                .toList();
    }
}
