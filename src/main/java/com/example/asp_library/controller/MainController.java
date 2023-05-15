package com.example.asp_library.controller;

import com.example.asp_library.domain.FileDB;
import com.example.asp_library.domain.User;
import com.example.asp_library.repository.FileDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class MainController {

    @GetMapping("/")
    public String mainPage(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        return "main";
    }

    @GetMapping("login")
    public String login(Model model, @AuthenticationPrincipal User user) {
        if (user != null && user.isCredentialsNonExpired()) {
            return "redirect:/";
        }
        else {
            model.addAttribute("user", user);
            return "login";
        }
    }
}
