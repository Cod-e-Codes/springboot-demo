package com.example.demo.controller.web;

import com.example.demo.model.Task;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskWebController {
    
    private final TaskService taskService;
    private final UserService userService;
    
    @Autowired
    public TaskWebController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }
    
    @GetMapping
    public String listTasks(@RequestParam(required = false) Long userId, 
                           @RequestParam(required = false) TaskStatus status,
                           @RequestParam(required = false) TaskPriority priority,
                           Model model) {
        List<Task> tasks;
        
        if (userId != null) {
            if (status != null) {
                tasks = taskService.findByUserAndStatus(userId, status);
            } else {
                tasks = taskService.findByUser(userId);
            }
            User user = userService.findById(userId).orElse(null);
            model.addAttribute("selectedUser", user);
        } else {
            tasks = taskService.findAll();
        }
        
        if (priority != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getPriority() == priority)
                    .toList();
        }
        
        List<User> users = userService.findAll();
        model.addAttribute("pageTitle", "Tasks");
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", users);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("taskStatuses", TaskStatus.values());
        model.addAttribute("taskPriorities", TaskPriority.values());
        
        return "tasks/list";
    }
    
    @GetMapping("/create")
    public String createTaskForm(Model model) {
        model.addAttribute("pageTitle", "Create Task");
        model.addAttribute("task", new Task());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("taskPriorities", TaskPriority.values());
        return "tasks/create";
    }
    
    @PostMapping("/create")
    public String createTask(@Valid @ModelAttribute Task task, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            model.addAttribute("taskPriorities", TaskPriority.values());
            return "tasks/create";
        }
        
        try {
            taskService.createTask(task);
            return "redirect:/tasks?success=created";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("users", userService.findAll());
            model.addAttribute("taskPriorities", TaskPriority.values());
            return "tasks/create";
        }
    }
    
    @GetMapping("/{id}")
    public String viewTask(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        model.addAttribute("pageTitle", "Task Details");
        model.addAttribute("task", task);
        return "tasks/view";
    }
    
    @GetMapping("/{id}/edit")
    public String editTaskForm(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        model.addAttribute("task", task);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("taskStatuses", TaskStatus.values());
        model.addAttribute("taskPriorities", TaskPriority.values());
        return "tasks/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id, @Valid @ModelAttribute Task task,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            model.addAttribute("taskStatuses", TaskStatus.values());
            model.addAttribute("taskPriorities", TaskPriority.values());
            return "tasks/edit";
        }
        
        try {
            task.setId(id);
            taskService.updateTask(task);
            return "redirect:/tasks/" + id + "?success=updated";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("users", userService.findAll());
            model.addAttribute("taskStatuses", TaskStatus.values());
            model.addAttribute("taskPriorities", TaskPriority.values());
            return "tasks/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return "redirect:/tasks?success=deleted";
        } catch (RuntimeException e) {
            return "redirect:/tasks?error=" + e.getMessage();
        }
    }
    
    @PostMapping("/{id}/complete")
    public String completeTask(@PathVariable Long id) {
        try {
            taskService.completeTask(id);
            return "redirect:/tasks/" + id + "?success=completed";
        } catch (RuntimeException e) {
            return "redirect:/tasks/" + id + "?error=" + e.getMessage();
        }
    }
    
    @PostMapping("/{id}/status")
    public String updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        try {
            taskService.updateTaskStatus(id, status);
            return "redirect:/tasks/" + id + "?success=status_updated";
        } catch (RuntimeException e) {
            return "redirect:/tasks/" + id + "?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/overdue")
    public String overdueTasks(Model model) {
        List<Task> overdueTasks = taskService.findOverdueTasks();
        model.addAttribute("pageTitle", "Overdue Tasks");
        model.addAttribute("tasks", overdueTasks);
        model.addAttribute("isOverdueView", true);
        return "tasks/overdue";
    }
    
    @GetMapping("/search")
    public String searchTasks(@RequestParam(required = false) String title,
                             @RequestParam(required = false) String description,
                             Model model) {
        List<Task> tasks;
        String searchTerm = "";
        String searchType = "";
        
        if (title != null && !title.trim().isEmpty()) {
            tasks = taskService.searchByTitle(title);
            searchTerm = title;
            searchType = "title";
        } else if (description != null && !description.trim().isEmpty()) {
            tasks = taskService.searchByDescription(description);
            searchTerm = description;
            searchType = "description";
        } else {
            tasks = taskService.findAll();
        }
        
        model.addAttribute("pageTitle", "Search Tasks");
        model.addAttribute("tasks", tasks);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("searchType", searchType);
        model.addAttribute("titleSearch", title != null ? title : "");
        model.addAttribute("descriptionSearch", description != null ? description : "");
        return "tasks/search";
    }
}
