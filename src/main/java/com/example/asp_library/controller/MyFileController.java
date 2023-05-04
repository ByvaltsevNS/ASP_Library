package com.example.asp_library.controller;

import com.example.asp_library.domain.FileDB;
import com.example.asp_library.repository.FileDBRepository;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class MyFileController {
    @Autowired
    private FileDBRepository fileDBRepository;

    @GetMapping("/myfiles")
    public String files(Model model) {
        Iterable<FileDB> files = fileDBRepository.findAll();

        model.addAttribute("files", files);

        return "files";
    }

    @PostMapping("addFile")
    public String addFile(@RequestParam String name, @RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file != null || file.getName().isEmpty()) {
            FileDB fileDB = new FileDB(name, file.getContentType(), file.getBytes());
            fileDBRepository.save(fileDB);
        }
        Iterable<FileDB> files = fileDBRepository.findAll();
        model.addAttribute("files", files);

        return "files";
    }

    @PostMapping("deleteFile")
    public String deleteFile(@RequestParam Long id, Model model) {
        FileDB fileDB = fileDBRepository.findById(id).get();
        fileDBRepository.delete(fileDB);

        Iterable<FileDB> files = fileDBRepository.findAll();
        model.addAttribute("files", files);

        return "files";
    }

    @PostMapping("clingoRun")
    public String clingoRun(Model model) {
        Runtime runtime = Runtime.getRuntime();
        try {
            //Запуск командной строки и передача ей параметров для выполнения системой clingo примера задачи
            //и запись результата в файл data.txt
            Process cmdStart = runtime.exec("cmd.exe /c cd " + "src/data/" + "task1"+ "& cmd.exe /k " + "clingo " + "\""+ "solution.lp"+ "\"" +
                    " > data.txt");

            if (cmdStart != null) {
                model.addAttribute("message", "it's Ok!");
            } else {
                model.addAttribute("message", "it's NOT Ok!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterable<FileDB> files = fileDBRepository.findAll();
        model.addAttribute("files", files);

        return "files";
    }
}
