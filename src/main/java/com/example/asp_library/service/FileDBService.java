package com.example.asp_library.service;

import com.example.asp_library.domain.FileDB;
import com.example.asp_library.repository.FileDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.example.asp_library.domain.User;

import java.io.IOException;
import java.util.stream.Stream;

@Service
public class FileDBService {
    @Autowired
    private FileDBRepository fileDBRepository;

    public FileDB store(MultipartFile file, User user) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FileDB FileDB = new FileDB(fileName, file.getContentType(), file.getBytes(), user.getId(), "");

        return fileDBRepository.save(FileDB);
    }

    public FileDB getFile(Long id) {
        return fileDBRepository.findById(id).get();
    }

    public Stream<FileDB> getAllFiles() {
        return fileDBRepository.findAll().stream();
    }
}
