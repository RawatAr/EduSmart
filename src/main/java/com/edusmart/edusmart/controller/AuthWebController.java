package com.edusmart.edusmart.controller;

import com.edusmart.edusmart.dto.RegisterRequest;
import com.edusmart.edusmart.model.User;
import com.edusmart.edusmart.model.enums.Role;
import com.edusmart.edusmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthWebController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request, 
                          RedirectAttributes redirectAttributes) {
        try {
            // Check if user already exists
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email already registered");
                return "redirect:/register";
            }

            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already taken");
                return "redirect:/register";
            }

            // Create new user
            User user = User.builder()
                    .firstName(request.getFullName() != null ? request.getFullName().split(" ")[0] : "User")
                    .lastName(request.getFullName() != null && request.getFullName().split(" ").length > 1 
                              ? request.getFullName().split(" ")[1] : "")
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole() != null ? request.getRole() : Role.STUDENT)
                    .build();

            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
