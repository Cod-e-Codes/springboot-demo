package com.example.demo.config;

import com.example.demo.model.Task;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.User;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    private final TaskService taskService;
    
    @Autowired
    public DataInitializer(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no users exist
        if (userService.getUserCount() == 0) {
            initializeSampleData();
        }
    }
    
    private void initializeSampleData() {
        // Create sample users
        User john = new User("john_doe", "john.doe@example.com", "John Doe");
        User jane = new User("jane_smith", "jane.smith@example.com", "Jane Smith");
        User bob = new User("bob_wilson", "bob.wilson@example.com", "Bob Wilson");
        User alice = new User("alice_brown", "alice.brown@example.com", "Alice Brown");
        
        try {
            john = userService.createUser(john);
            jane = userService.createUser(jane);
            bob = userService.createUser(bob);
            alice = userService.createUser(alice);
        } catch (Exception e) {
            System.err.println("Error creating users: " + e.getMessage());
        }
        
        // Create sample tasks
        try {
            // Tasks for John
            taskService.createTaskForUser(john.getId(), "Complete project proposal", 
                "Write and submit the project proposal for the new client", TaskPriority.HIGH);
            
            taskService.createTaskForUser(john.getId(), "Review code changes", 
                "Review the latest pull requests from the development team", TaskPriority.MEDIUM);
            
            taskService.createTaskForUser(john.getId(), "Update documentation", 
                "Update API documentation with new endpoints", TaskPriority.LOW);
            
            // Tasks for Jane
            taskService.createTaskForUser(jane.getId(), "Design new UI mockups", 
                "Create wireframes and mockups for the mobile app", TaskPriority.HIGH);
            
            taskService.createTaskForUser(jane.getId(), "Conduct user research", 
                "Interview 5 users to gather feedback on current interface", TaskPriority.MEDIUM);
            
            Task janeTask = taskService.createTaskForUser(jane.getId(), "Prepare presentation", 
                "Prepare slides for the quarterly review meeting", TaskPriority.URGENT);
            taskService.setTaskDueDate(janeTask.getId(), LocalDateTime.now().plusDays(1));
            
            // Tasks for Bob
            taskService.createTaskForUser(bob.getId(), "Fix critical bug in payment system", 
                "Investigate and fix the payment processing issue reported by users", TaskPriority.URGENT);
            
            taskService.createTaskForUser(bob.getId(), "Write unit tests", 
                "Add unit tests for the new authentication module", TaskPriority.MEDIUM);
            
            taskService.createTaskForUser(bob.getId(), "Database optimization", 
                "Optimize database queries for better performance", TaskPriority.LOW);
            
            // Tasks for Alice
            taskService.createTaskForUser(alice.getId(), "Plan team building event", 
                "Organize a team building event for next month", TaskPriority.LOW);
            
            taskService.createTaskForUser(alice.getId(), "Update employee handbook", 
                "Review and update the company employee handbook", TaskPriority.MEDIUM);
            
            Task aliceTask = taskService.createTaskForUser(alice.getId(), "Prepare budget report", 
                "Compile monthly budget report for management review", TaskPriority.HIGH);
            taskService.setTaskDueDate(aliceTask.getId(), LocalDateTime.now().minusDays(2)); // Overdue task
            
            // Mark some tasks as completed
            java.util.List<Task> johnTasks = taskService.findByUser(john.getId());
            if (!johnTasks.isEmpty()) {
                taskService.completeTask(johnTasks.get(0).getId());
            }
            
            java.util.List<Task> janeTasks = taskService.findByUser(jane.getId());
            if (!janeTasks.isEmpty()) {
                taskService.completeTask(janeTasks.get(0).getId());
            }
            
            System.out.println("Sample data initialized successfully!");
            System.out.println("Created " + userService.getUserCount() + " users and " + taskService.findAll().size() + " tasks.");
            
        } catch (Exception e) {
            System.err.println("Error creating tasks: " + e.getMessage());
        }
    }
}
