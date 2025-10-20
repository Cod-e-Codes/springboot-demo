package com.example.demo.controller.api;

import com.example.demo.dto.TaskDTO;
import com.example.demo.model.Task;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.TaskStatus;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskApiController {
    
    private final TaskService taskService;
    private final UserService userService;
    
    @Autowired
    public TaskApiController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }
    
    // GET /api/tasks - Get all tasks
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<Task> tasks = taskService.findAll();
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }
    
    // GET /api/tasks/{id} - Get task by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.findById(id);
        return task.map(t -> ResponseEntity.ok(new TaskDTO(t)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET /api/tasks/user/{userId} - Get tasks by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasksByUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.findByUser(userId);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }
    
    // GET /api/tasks/status/{status} - Get tasks by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<Task> tasks = taskService.findByStatus(status);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }
    
    // GET /api/tasks/priority/{priority} - Get tasks by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDTO>> getTasksByPriority(@PathVariable TaskPriority priority) {
        List<Task> tasks = taskService.findByPriority(priority);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }
    
    // GET /api/tasks/user/{userId}/status/{status} - Get tasks by user and status
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByUserAndStatus(@PathVariable Long userId, @PathVariable TaskStatus status) {
        List<Task> tasks = taskService.findByUserAndStatus(userId, status);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }
    
    // GET /api/tasks/overdue - Get overdue tasks
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks() {
        List<Task> tasks = taskService.findOverdueTasks();
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }
    
    // POST /api/tasks - Create new task
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(new TaskDTO(createdTask));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // POST /api/tasks/user/{userId} - Create task for specific user
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createTaskForUser(
            @PathVariable Long userId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false, defaultValue = "MEDIUM") TaskPriority priority) {
        try {
            // Validate user exists
            if (!userService.findById(userId).isPresent()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("User not found with id: " + userId));
            }
                Task task = taskService.createTaskForUser(userId, title, description, priority);
                return ResponseEntity.status(HttpStatus.CREATED).body(new TaskDTO(task));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // PUT /api/tasks/{id} - Update task
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        try {
            task.setId(id); // Ensure the ID is set
            Task updatedTask = taskService.updateTask(task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // PATCH /api/tasks/{id}/status - Update task status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        try {
            Task task = taskService.updateTaskStatus(id, status);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // PATCH /api/tasks/{id}/priority - Update task priority
    @PatchMapping("/{id}/priority")
    public ResponseEntity<?> updateTaskPriority(@PathVariable Long id, @RequestParam TaskPriority priority) {
        try {
            Task task = taskService.updateTaskPriority(id, priority);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // PATCH /api/tasks/{id}/complete - Complete a task
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Long id) {
        try {
            Task task = taskService.completeTask(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // PATCH /api/tasks/{id}/due-date - Set task due date
    @PatchMapping("/{id}/due-date")
    public ResponseEntity<?> setTaskDueDate(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {
        try {
            Task task = taskService.setTaskDueDate(id, dueDate);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // DELETE /api/tasks/{id} - Delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().body(new SuccessResponse("Task deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // GET /api/tasks/search/title - Search tasks by title
    @GetMapping("/search/title")
    public ResponseEntity<List<Task>> searchTasksByTitle(@RequestParam String title) {
        List<Task> tasks = taskService.searchByTitle(title);
        return ResponseEntity.ok(tasks);
    }
    
    // GET /api/tasks/search/description - Search tasks by description
    @GetMapping("/search/description")
    public ResponseEntity<List<Task>> searchTasksByDescription(@RequestParam String description) {
        List<Task> tasks = taskService.searchByDescription(description);
        return ResponseEntity.ok(tasks);
    }
    
    // GET /api/tasks/due-between - Get tasks due between dates
    @GetMapping("/due-between")
    public ResponseEntity<List<Task>> getTasksDueBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Task> tasks = taskService.findTasksDueBetween(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }
    
    // GET /api/tasks/user/{userId}/high-priority - Get high priority tasks for user
    @GetMapping("/user/{userId}/high-priority")
    public ResponseEntity<List<Task>> getHighPriorityTasksByUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.findHighPriorityTasksByUser(userId);
        return ResponseEntity.ok(tasks);
    }
    
    // GET /api/tasks/user/{userId}/statistics - Get task statistics for user
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<TaskService.TaskStatistics> getTaskStatisticsForUser(@PathVariable Long userId) {
        TaskService.TaskStatistics statistics = taskService.getTaskStatisticsForUser(userId);
        return ResponseEntity.ok(statistics);
    }
    
    // Error response class
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    // Success response class
    public static class SuccessResponse {
        private String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
