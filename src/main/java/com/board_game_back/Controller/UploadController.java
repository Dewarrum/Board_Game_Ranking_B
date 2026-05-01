package com.board_game_back.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    @Value("${supabase.storage.profile-bucket}")
    private String profileBucket;

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + ".jpg";
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + filename;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.setContentType(MediaType.IMAGE_JPEG);

            restTemplate.exchange(
                uploadUrl,
                HttpMethod.PUT,
                new HttpEntity<>(file.getBytes(), headers),
                String.class
            );

            String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + filename;
            return ResponseEntity.ok(Map.of("url", publicUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + ".jpg";
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + profileBucket + "/" + filename;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.setContentType(MediaType.IMAGE_JPEG);

            restTemplate.exchange(
                uploadUrl,
                HttpMethod.PUT,
                new HttpEntity<>(file.getBytes(), headers),
                String.class
            );

            String publicUrl = supabaseUrl + "/storage/v1/object/public/" + profileBucket + "/" + filename;
            return ResponseEntity.ok(Map.of("url", publicUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
