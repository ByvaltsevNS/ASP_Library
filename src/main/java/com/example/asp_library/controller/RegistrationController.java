package com.example.asp_library.controller;

import com.example.asp_library.domain.Role;
import com.example.asp_library.domain.User;
import com.example.asp_library.repository.UserRepository;
import com.example.asp_library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
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
            return "redirect:/";
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
        User userFromDb = userRepository.findByUsername(newUser.getUsername());
        if (userFromDb != null) {
            model.addAttribute("errorMessage", "Пользователь с таким логином уже существует!");
            return "registration";
        }
        userFromDb = userRepository.findByStudentId(newUser.getStudentId());
        if (userFromDb != null) {
            model.addAttribute("errorMessage", "Пользователь с таким номером студенческого уже существует!");
            return "registration";
        }

        newUser.setActive(true);
        newUser.setRoles(Collections.singleton(Role.USER));
        userService.createUser(newUser);

        model.addAttribute("successMessage", "Пользователь успешно зарегистрирован!");

        return "login";
    }
}
