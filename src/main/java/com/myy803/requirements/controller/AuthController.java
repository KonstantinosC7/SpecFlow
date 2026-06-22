package com.myy803.requirements.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.myy803.requirements.model.User;
import com.myy803.requirements.service.UserService;

/**
 * Handles US1 (register/login) and US2 (profile management).
 * US3 (logout) is handled automatically by Spring Security.
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // --- US1: Login ---

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/signin";
    }

    // --- US1: Registration ---

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/signup";
    }

    @PostMapping("/save")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userService.isUserPresent(user)) {
            model.addAttribute("errorMessage", "Username already taken. Please choose another.");
            model.addAttribute("user", user);
            return "auth/signup";
        }
        userService.saveUser(user);
        model.addAttribute("successMessage", "Registration successful! You can now log in.");
        return "auth/signin";
    }

    // --- US2: Profile ---

    @GetMapping("/user/profile")
    public String showProfileForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/user/profile/update")
    public String updateProfile(@ModelAttribute("user") User user, Model model) {
        userService.updateUser(user);
        model.addAttribute("successMessage", "Profile updated successfully!");
        model.addAttribute("user", userService.findByUsername(user.getUsername()));
        return "user/profile";
    }
}
