package com.tp.opencourse.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tp.opencourse.dto.FileDTO;
import com.tp.opencourse.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public FileDTO uploadFile(MultipartFile file) {
        try {

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "raw",  // This ensures Cloudinary doesn't treat it as an image
                    "public_id", file.getOriginalFilename(), // Custom path
                    "format", "pdf" // Enforce PDF format
            ));
            FileDTO fileDTO = FileDTO.builder()
                    .url(uploadResult.get("secure_url").toString())
                    .createdAt(LocalDateTime.now())
                    .build();
            return fileDTO;
        } catch (IOException e) {
            throw new RuntimeException("Upload file faild");
        }
    }
}
