package com.example.demo.repository;

import com.example.demo.model.Task;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find tasks by user
    List<Task> findByUser(User user);
    
    // Find tasks by user ID
    List<Task> findByUserId(Long userId);
    
    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);
    
    // Find tasks by priority
    List<Task> findByPriority(TaskPriority priority);
    
    // Find tasks by status and user
    List<Task> findByStatusAndUser(TaskStatus status, User user);
    
    // Find overdue tasks
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentTime AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("currentTime") LocalDateTime currentTime);
    
    // Find tasks by title containing (case insensitive)
    List<Task> findByTitleContainingIgnoreCase(String title);
    
    // Find tasks by description containing (case insensitive)
    List<Task> findByDescriptionContainingIgnoreCase(String description);
    
    // Find tasks due within a specific time range
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC")
    List<Task> findTasksDueBetween(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    // Find tasks by user and status
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
    
    // Count tasks by status for a specific user
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = :status")
    long countTasksByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TaskStatus status);
    
    // Find tasks with high priority for a specific user
    List<Task> findByUserIdAndPriorityOrderByCreatedAtDesc(Long userId, TaskPriority priority);
}
