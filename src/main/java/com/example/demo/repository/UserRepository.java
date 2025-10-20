package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find by username (case insensitive)
    Optional<User> findByUsernameIgnoreCase(String username);
    
    // Find by email (case insensitive)
    Optional<User> findByEmailIgnoreCase(String email);
    
    // Check if username exists
    boolean existsByUsernameIgnoreCase(String username);
    
    // Check if email exists
    boolean existsByEmailIgnoreCase(String email);
    
    // Find users by full name containing (case insensitive)
    List<User> findByFullNameContainingIgnoreCase(String fullName);
    
    // Custom query to find users with task count
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tasks")
    List<User> findAllWithTasks();
    
    // Custom query to find users by task status
    @Query("SELECT DISTINCT u FROM User u JOIN u.tasks t WHERE t.status = :status")
    List<User> findUsersByTaskStatus(@Param("status") String status);
}
