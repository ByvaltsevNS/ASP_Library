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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Controller
public class FileController {
    @Autowired
    private FileDBRepository fileDBRepository;

    @Value("${upload.path}")
    private String uploadPath;

    //метод определения расширения файла
    public static String getFileExtension(String fileName) {
        // если в имени файла есть точка и она не является первым символом в названии файла
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            // то вырезаем все знаки после последней точки в названии файла, то есть ХХХХХ.txt -> txt
            return fileName.substring(fileName.lastIndexOf(".") + 1);
            // в противном случае возвращаем заглушку, то есть расширение не найдено
        else return "";
    }

    private StringBuilder readFileText(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        FileReader fr = new FileReader(uploadPath + "/" + fileName);
        Scanner scan = new Scanner(fr);

        while(scan.hasNextLine()) {
            stringBuilder.append(scan.nextLine()+"\n");
        }

        fr.close();
        return stringBuilder;
    }

    private File createFileFromTextArea(StringBuilder text) throws IOException {
        File file = new File(uploadPath + "/program.lp");
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            if (file.createNewFile()) {
                FileWriter nFile = new FileWriter(file);

                nFile.write(text.toString());

                nFile.close();
            } else {
                return null;
            }
        } else {
            return null;
        }
        return file;
    }

    private void prepareView(Model model, User user) {
        Iterable<FileDB> files = fileDBRepository.findAll();

        model.addAttribute("files", files);
        model.addAttribute("user", user);
        model.addAttribute("textFile", null);
        model.addAttribute("fileId", null);
        model.addAttribute("resultFileText", null);
    }

    private void prepareView(Model model, User user, StringBuilder textFile, Long fileId) {
        Iterable<FileDB> files = fileDBRepository.findAll();

        model.addAttribute("files", files);
        model.addAttribute("user", user);
        model.addAttribute("textFile", textFile);
        model.addAttribute("fileId", fileId);
        model.addAttribute("resultFileText", null);
    }

    private void prepareView(Model model, User user, StringBuilder textFile, Long fileId, StringBuilder resultFileText) {
        Iterable<FileDB> files = fileDBRepository.findAll();

        model.addAttribute("files", files);
        model.addAttribute("user", user);
        model.addAttribute("textFile", textFile);
        model.addAttribute("fileId", fileId);
        model.addAttribute("resultFileText", resultFileText);
    }

    @GetMapping("files")
    public String files(Model model, @AuthenticationPrincipal User user) {
        this.prepareView(model, user);
        return "files";
    }

    @PostMapping("addFile")
    public String addFile(
            @AuthenticationPrincipal User user,
            Model model,
            @RequestParam("file") MultipartFile file,
            @RequestParam String fileName
    ) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String fileExt = getFileExtension(file.getOriginalFilename());
            //String uuidFile = UUID.randomUUID().toString();
            String resultFilename = fileName + "." + fileExt;

            File newFile = new File(uploadPath + "/" + resultFilename);
            if (!newFile.exists() || newFile.isDirectory()) {
                FileDB fileDB = new FileDB(fileName, file.getContentType(), file.getBytes(), user.getId(), fileExt);
                fileDBRepository.save(fileDB);

                file.transferTo(newFile);
                model.addAttribute("okayMessage", "Файл успешно добавлен!");
            } else {
                model.addAttribute("errorMessage", "Файл с таким именем уже существует!");
            }
        }
        this.prepareView(model, user);
        return "files";
    }

    @PostMapping("deleteFile")
    public String deleteFile(
            @AuthenticationPrincipal User user,
            @RequestParam Long id,
            Model model) {
        FileDB fileDB = fileDBRepository.findById(id).get();
        File file = new File(uploadPath  + "/" +  fileDB.getName());
        if (!file.delete()) {
            model.addAttribute("errorMessage", "Произошли какие-то проблемы, не удалось удалить файл!");
        }
        fileDBRepository.delete(fileDB);

        this.prepareView(model, user);
        return "files";
    }

    @PostMapping("selectFile")
    public String selectFile(
            @AuthenticationPrincipal User user,
            @RequestParam Long id,
            Model model) throws IOException {
        FileDB fileDB = fileDBRepository.findById(id).get();

        StringBuilder stringBuilder = this.readFileText(fileDB.getFullName());

        this.prepareView(model, user, stringBuilder, fileDB.getId());
        return "files";
    }

    @PostMapping("clingoRun")
    public String clingoRun(
            @AuthenticationPrincipal User user,
            @RequestParam Long fileId,
            @RequestParam StringBuilder textFile,
            Model model) {
        FileDB fileDB = fileDBRepository.findById(fileId).get();
        StringBuilder resultFileText = null;

        Runtime runtime = Runtime.getRuntime();
        Process cmdStart = null;
        try {
            cmdStart = runtime.exec("cmd.exe /c cd files/ && cmd.exe /c clingo " + fileDB.getFullName() + " > " + fileDB.getName() + ".txt");

            if (cmdStart != null) {
                cmdStart.waitFor(30, TimeUnit.SECONDS);
                File resultFile = new File(uploadPath + "/" + fileDB.getName() + ".txt");
                if (resultFile.exists()) {
                    resultFileText = this.readFileText(resultFile.getName());

                    model.addAttribute("okayMessage", "it's Ok!");
                } else {
                    model.addAttribute("errorMessage", "it's NOT Ok!");
                }
            } else {
                model.addAttribute("errorMessage", "it's NOT Ok!");
            }
        } catch (IOException e) {
            cmdStart.destroy();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.prepareView(model, user, textFile, fileId, resultFileText);
        return "files";
    }

    @PostMapping("clingoRun1")
    public String clingoRun1(
            @AuthenticationPrincipal User user,
            @RequestParam StringBuilder textFile,
            Model model) {

        StringBuilder resultFileText = null;
        if (!textFile.isEmpty()) {

            Runtime runtime = Runtime.getRuntime();
            Process cmdStart = null;
            try {
                File execFile = this.createFileFromTextArea(textFile);
                if (execFile != null && execFile.exists()) {
                    cmdStart = runtime.exec("cmd.exe /c cd files/ && cmd.exe /c clingo " + execFile.getName() + " > " + execFile.getName() + ".txt");

                    if (cmdStart != null) {
                        cmdStart.waitFor(30, TimeUnit.SECONDS);
                        File resultFile = new File(uploadPath + "/" + execFile.getName() + ".txt");
                        if (resultFile.exists()) {
                            resultFileText = this.readFileText(resultFile.getName());

                            model.addAttribute("okayMessage", "it's Ok!");
                        } else {
                            model.addAttribute("errorMessage", "it's NOT Ok!");
                        }
                    } else {
                        model.addAttribute("errorMessage", "it's NOT Ok!");
                    }
                } else {
                    model.addAttribute("errorMessage", "it's NOT Ok!");
                }
            } catch (IOException e) {
                cmdStart.destroy();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            model.addAttribute("errorMessage", "Текст программы пустой!");
        }

        this.prepareView(model, user, textFile, null, resultFileText);
        return "files";
    }
}

