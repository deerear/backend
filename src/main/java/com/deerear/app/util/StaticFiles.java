package com.deerear.app.util;

import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StaticFiles {
    public static String saveImage(MultipartFile file, String route, String id) {

        String name = file.getOriginalFilename();
        assert name != null;
        String extension = name.substring(name.lastIndexOf(".") + 1);

        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String dbPath = "/images/" + route + "/" + id + "/" + fileName;

        // Path 객체 생성
        Path path = Paths.get("./app" + dbPath);

        try{
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new BizException("이미지 저장에 실패했습니다.", ErrorCode.INVALID_INPUT, "");
        }

        return dbPath;
    }
}
