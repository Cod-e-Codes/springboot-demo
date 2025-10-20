package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    
    // Create a new task
    public Task createTask(Task task) {
        // Validate that the user exists
        if (task.getUser() == null || task.getUser().getId() == null) {
            throw new RuntimeException("Task must be associated with a user");
        }
        
        User user = userRepository.findById(task.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found with id: " + task.getUser().getId()));
        
        task.setUser(user);
        return taskRepository.save(task);
    }
    
    // Create a task for a specific user
    public Task createTaskForUser(Long userId, String title, String description, TaskPriority priority) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Task task = new Task(title, description, priority, user);
        return taskRepository.save(task);
    }
    
    // Find task by ID
    @Transactional(readOnly = true)
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }
    
    // Get all tasks
    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
    
    // Get tasks by user
    @Transactional(readOnly = true)
    public List<Task> findByUser(Long userId) {
        return taskRepository.findByUserId(userId);
    }
    
    // Get tasks by status
    @Transactional(readOnly = true)
    public List<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
    
    // Get tasks by priority
    @Transactional(readOnly = true)
    public List<Task> findByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }
    
    // Get tasks by user and status
    @Transactional(readOnly = true)
    public List<Task> findByUserAndStatus(Long userId, TaskStatus status) {
        return taskRepository.findByUserIdAndStatus(userId, status);
    }
    
    // Get overdue tasks
    @Transactional(readOnly = true)
    public List<Task> findOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now());
    }
    
    // Get high priority tasks for a user
    @Transactional(readOnly = true)
    public List<Task> findHighPriorityTasksByUser(Long userId) {
        return taskRepository.findByUserIdAndPriorityOrderByCreatedAtDesc(userId, TaskPriority.HIGH);
    }
    
    // Update task
    public Task updateTask(Task task) {
        if (!taskRepository.existsById(task.getId())) {
            throw new RuntimeException("Task not found with id: " + task.getId());
        }
        return taskRepository.save(task);
    }
    
    // Update task status
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        
        task.setStatus(status);
        return taskRepository.save(task);
    }
    
    // Update task priority
    public Task updateTaskPriority(Long taskId, TaskPriority priority) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        
        task.setPriority(priority);
        return taskRepository.save(task);
    }
    
    // Set task due date
    public Task setTaskDueDate(Long taskId, LocalDateTime dueDate) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }
    
    // Complete a task
    public Task completeTask(Long taskId) {
        return updateTaskStatus(taskId, TaskStatus.COMPLETED);
    }
    
    // Delete task
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
    
    // Search tasks by title
    @Transactional(readOnly = true)
    public List<Task> searchByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title);
    }
    
    // Search tasks by description
    @Transactional(readOnly = true)
    public List<Task> searchByDescription(String description) {
        return taskRepository.findByDescriptionContainingIgnoreCase(description);
    }
    
    // Get tasks due within a date range
    @Transactional(readOnly = true)
    public List<Task> findTasksDueBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return taskRepository.findTasksDueBetween(startDate, endDate);
    }
    
    // Get task statistics for a user
    @Transactional(readOnly = true)
    public long getTaskCountByUserAndStatus(Long userId, TaskStatus status) {
        return taskRepository.countTasksByUserIdAndStatus(userId, status);
    }
    
    // Get all task statistics for a user
    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatisticsForUser(Long userId) {
        long total = taskRepository.findByUserId(userId).size();
        long pending = taskRepository.countTasksByUserIdAndStatus(userId, TaskStatus.PENDING);
        long inProgress = taskRepository.countTasksByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS);
        long completed = taskRepository.countTasksByUserIdAndStatus(userId, TaskStatus.COMPLETED);
        long cancelled = taskRepository.countTasksByUserIdAndStatus(userId, TaskStatus.CANCELLED);
        
        return new TaskStatistics(total, pending, inProgress, completed, cancelled);
    }
    
    // Inner class for task statistics
    public static class TaskStatistics {
        private final long total;
        private final long pending;
        private final long inProgress;
        private final long completed;
        private final long cancelled;
        
        public TaskStatistics(long total, long pending, long inProgress, long completed, long cancelled) {
            this.total = total;
            this.pending = pending;
            this.inProgress = inProgress;
            this.completed = completed;
            this.cancelled = cancelled;
        }
        
        // Getters
        public long getTotal() { return total; }
        public long getPending() { return pending; }
        public long getInProgress() { return inProgress; }
        public long getCompleted() { return completed; }
        public long getCancelled() { return cancelled; }
        
        @Override
        public String toString() {
            return String.format("Total: %d, Pending: %d, In Progress: %d, Completed: %d, Cancelled: %d",
                    total, pending, inProgress, completed, cancelled);
        }
    }
}
