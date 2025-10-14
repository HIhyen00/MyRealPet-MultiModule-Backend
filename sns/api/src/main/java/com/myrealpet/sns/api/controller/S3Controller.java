package com.myrealpet.sns.api.controller;

import com.myrealpet.sns.core.s3.S3PresignedUrlDto;
import com.myrealpet.sns.core.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) {
        
        try {
            log.info("이미지 업로드 요청 - 파일명: {}, 크기: {}, 폴더: {}", 
                file.getOriginalFilename(), file.getSize(), folder);
            
            if (!folder.equals("post") && !folder.equals("image_gallery")) {
                folder = "post";
            }
            
            String dirName = "sns/" + folder;
            String imageUrl = s3Service.uploadFile(file, dirName);
            
            log.info("이미지 업로드 성공 - URL: {}", imageUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("이미지 업로드 오류: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/s3/presigned-url")
    public ResponseEntity<S3PresignedUrlDto> getPresignedUrl(@RequestParam String key) {
        if (!key.startsWith("sns/post/") && !key.startsWith("sns/image_gallery/")) {
            key = "sns/post/" + key;
        }
        
        S3PresignedUrlDto presignedUrlDto = s3Service.generatePresignedUrl(key);
        return ResponseEntity.ok(presignedUrlDto);
    }

    @GetMapping("/auth/s3-credentials")
    public ResponseEntity<Map<String, String>> getS3Credentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("accessKeyId", "임시_액세스_키");
        credentials.put("secretAccessKey", "임시_시크릿_키");
        credentials.put("sessionToken", "임시_세션_토큰");
        
        return ResponseEntity.ok(credentials);
    }
}
