package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // Create a new user
    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        return userRepository.save(user);
    }
    
    // Find user by ID
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    // Find user by username
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }
    
    // Find user by email
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
    
    // Get all users
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    // Get all users with their tasks
    @Transactional(readOnly = true)
    public List<User> findAllWithTasks() {
        return userRepository.findAllWithTasks();
    }
    
    // Update user
    public User updateUser(User user) {
        // Check if user exists
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        
        // Check if username is being changed and if new username already exists
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            User currentUser = existingUser.get();
            if (!currentUser.getUsername().equalsIgnoreCase(user.getUsername()) 
                && userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
                throw new RuntimeException("Username already exists: " + user.getUsername());
            }
            
            if (!currentUser.getEmail().equalsIgnoreCase(user.getEmail()) 
                && userRepository.existsByEmailIgnoreCase(user.getEmail())) {
                throw new RuntimeException("Email already exists: " + user.getEmail());
            }
        }
        
        return userRepository.save(user);
    }
    
    // Delete user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    // Search users by full name
    @Transactional(readOnly = true)
    public List<User> searchByFullName(String fullName) {
        return userRepository.findByFullNameContainingIgnoreCase(fullName);
    }
    
    // Check if username exists
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }
    
    // Check if email exists
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }
    
    // Get user statistics
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }
}
