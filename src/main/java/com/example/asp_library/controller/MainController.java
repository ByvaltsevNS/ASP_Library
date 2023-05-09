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
    @Autowired
    private FileDBRepository fileDBRepository;

    @Value("${upload.path}")
    private String uploadPath;

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

    @GetMapping("newFiles")
    public String files(Model model, @AuthenticationPrincipal User user) {
        Iterable<FileDB> files = fileDBRepository.findAll();

        model.addAttribute("files", files);
        model.addAttribute("user", user);

        return "newFiles";
    }

    @PostMapping("addNewFile")
    public String add(
            @AuthenticationPrincipal User user,
            Model model,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            File newFile = new File(uploadPath + "/" + file.getOriginalFilename());
            if (!newFile.exists() || newFile.isDirectory()) {
                FileDB fileDB = new FileDB(file.getOriginalFilename(), file.getContentType(), file.getBytes(), user.getId());
                fileDBRepository.save(fileDB);
                file.transferTo(newFile);
                model.addAttribute("okayMessage", "Файл успешно добавлен!");
            } else {
                model.addAttribute("errorMessage", "Файл с таким именем уже существует!");
            }
        }
        Iterable<FileDB> files = fileDBRepository.findAll();
        model.addAttribute("files", files);
        model.addAttribute("user", user);

        return "newFiles";
    }

    @PostMapping("deleteNewFile")
    public String deleteNewFile(
            @AuthenticationPrincipal User user,
            @RequestParam Long id,
            Model model) {
        FileDB fileDB = fileDBRepository.findById(id).get();
        File file = new File(uploadPath  + "/" +  fileDB.getName());
        file.delete();
        fileDBRepository.delete(fileDB);

        Iterable<FileDB> files = fileDBRepository.findAll();
        model.addAttribute("files", files);
        model.addAttribute("user", user);
        model.addAttribute("okayMessage", "Файл успешно удален!");

        return "newFiles";
    }

    @PostMapping("newClingoRun")
    public String clingoRun(
            @AuthenticationPrincipal User user,
            @RequestParam String fileName,
            Model model) {
        Runtime runtime = Runtime.getRuntime();
        Process cmdStart = null;
        try {
            cmdStart = runtime.exec("cmd.exe /c cd files/ && cmd.exe /c clingo " + fileName + " > data.txt");

            if (cmdStart != null) {
                model.addAttribute("okayMessage", "it's Ok!");
            } else {
                model.addAttribute("errorMessage", "it's NOT Ok!");
            }
        } catch (IOException e) {
            cmdStart.destroy();
        }

        Iterable<FileDB> files = fileDBRepository.findAll();
        model.addAttribute("files", files);
        model.addAttribute("user", user);

        return "newFiles";
    }
}
