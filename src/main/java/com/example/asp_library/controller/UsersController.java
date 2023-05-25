package com.example.asp_library.controller;

import com.example.asp_library.domain.User;
import com.example.asp_library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsersController {
    @Autowired
    private UserService userService;

    private void prepareView(Model model, User user) {
        Iterable<User> users = userService.findAll();

        model.addAttribute("users", users);
        model.addAttribute("user", user);
        model.addAttribute("selectedUser", new User());
    }

    private void prepareView(Model model, User user, User selectedUser) {
        Iterable<User> users = userService.findAll();

        model.addAttribute("users", users);
        model.addAttribute("user", user);
        model.addAttribute("selectedUser", selectedUser);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("users")
    public String users(Model model, @AuthenticationPrincipal User user) {
        this.prepareView(model, user);
        return "users";
    }

    @PostMapping("selectUser")
    public String selectUser(
            @AuthenticationPrincipal User user,
            @RequestParam Long id,
            Model model) {
        User selectedUser = userService.getUserById(id);

        this.prepareView(model, user, selectedUser);
        return "users";
    }

    @PostMapping("saveUser")
    public String saveUser(
            @AuthenticationPrincipal User user,
            @ModelAttribute User selectedUser,
            Model model) {

        if (userService.updateUser(selectedUser)) {
            model.addAttribute("okayMessage", "Изменения успешно сохранены!");
        } else {
            model.addAttribute("errorMessage", "Пользователь с таким логином уже существует!");
        }

        this.prepareView(model, user, selectedUser);
        return "users";
    }

    @PostMapping("deleteUser")
    public String deleteUser(
            @AuthenticationPrincipal User user,
            @RequestParam Long id,
            Model model) {

        userService.deleteUser(id);
        model.addAttribute("okayMessage", "Пользователь успешно удален!");

        this.prepareView(model, user);
        return "users";
    }
}
