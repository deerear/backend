package com.deerear.app.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StaticFiles {
    public static String saveImage(MultipartFile file, String route, String id) throws IOException {

        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
        String dbPath = "/images/" + route + "/" + id + "/" + fileName;

        // Path 객체 생성
        Path path = Paths.get("." + dbPath);

        // 디렉토리 생성
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        Files.write(path, file.getBytes());

        return dbPath;
    }
}
