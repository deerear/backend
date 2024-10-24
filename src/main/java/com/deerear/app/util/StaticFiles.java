package com.deerear.app.util;

import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StaticFiles {
    public static String saveImage(MultipartFile file, String route, String id, Boolean isThumbnail) {

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

            if (isThumbnail){
                resizeAndSave(file, String.valueOf(path), fileName, extension);
            } else {
                Files.write(path, file.getBytes());
            }

        } catch (IOException e) {
            throw new BizException("이미지 저장에 실패했습니다.", ErrorCode.INVALID_INPUT, "");
        }

        return dbPath;
    }

    private static void resizeAndSave(MultipartFile file, String path, String name, String extension) throws IOException {

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

        if (originalImage == null) {
            throw new IllegalArgumentException("Failed to read image file");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 최대 크기 상수 정의
        final int MAX_SIZE = 250;

        double ratio = 1.0;
        // Max Size : 250 x 250
        if (originalWidth > MAX_SIZE || originalHeight > MAX_SIZE) {
            // double로 캐스팅하여 나눗셈 수행
            ratio = Math.min((double)MAX_SIZE / originalWidth, (double)MAX_SIZE / originalHeight);
        }

        // 새로운 크기 계산 (최소 1픽셀 보장)
        int width = Math.max(1, (int)(originalWidth * ratio));
        int height = Math.max(1, (int)(originalHeight * ratio));


        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(originalImage, 0, 0, width, height, null);
        graphics2D.dispose();

        File saveFile = new File(path + name);

        ImageIO.write(resizedImage, extension, saveFile);

    }
}
