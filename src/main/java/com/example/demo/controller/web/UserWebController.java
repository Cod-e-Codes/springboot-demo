package com.example.demo.controller.web;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserWebController {
    
    private final UserService userService;
    
    @Autowired
    public UserWebController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/list";
    }
    
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/create";
    }
    
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "users/create";
        }
        
        try {
            userService.createUser(user);
            return "redirect:/users?success=created";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "users/create";
        }
    }
    
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        model.addAttribute("user", user);
        return "users/view";
    }
    
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        model.addAttribute("user", user);
        return "users/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute User user, 
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        
        try {
            user.setId(id);
            userService.updateUser(user);
            return "redirect:/users/" + id + "?success=updated";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "users/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/users?success=deleted";
        } catch (RuntimeException e) {
            return "redirect:/users?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/search")
    public String searchUsers(@RequestParam(required = false) String name, Model model) {
        if (name != null && !name.trim().isEmpty()) {
            List<User> users = userService.searchByFullName(name);
            model.addAttribute("users", users);
            model.addAttribute("searchTerm", name);
        } else {
            model.addAttribute("users", userService.findAll());
        }
        return "users/list";
    }
}
