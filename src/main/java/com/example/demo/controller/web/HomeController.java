package com.example.demo.controller.web;

import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    private final UserService userService;
    private final TaskService taskService;
    
    @Autowired
    public HomeController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        // Get dashboard statistics
        long totalUsers = userService.getUserCount();
        long totalTasks = taskService.findAll().size();
        long completedTasks = taskService.findByStatus(com.example.demo.model.TaskStatus.COMPLETED).size();
        long overdueTasks = taskService.findOverdueTasks().size();
        
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("overdueTasks", overdueTasks);
        
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return home(model); // Reuse the same logic
    }
}
