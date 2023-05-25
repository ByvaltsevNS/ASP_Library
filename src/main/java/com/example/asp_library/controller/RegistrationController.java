package com.example.asp_library.controller;

import com.example.asp_library.domain.Role;
import com.example.asp_library.domain.User;
import com.example.asp_library.repository.UserRepository;
import com.example.asp_library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Set;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(
            @AuthenticationPrincipal User user,
            Model model) {
        if (user != null && user.isCredentialsNonExpired()) {
            //return "redirect:/login";
            return "registration";
        }
        else {
            model.addAttribute("user", user);
            return "registration";
        }
    }

    @PostMapping("/registration")
    public String addUser(
            @AuthenticationPrincipal User user,
            User newUser,
            Model model) {

        if (userService.createUser(newUser)) {
            model.addAttribute("successMessage", "Пользователь успешно зарегистрирован!");
        } else {
            model.addAttribute("errorMessage", "Пользователь с таким логином уже существует!");
        }
        model.addAttribute("user", user);

        return "login";
    }
}
