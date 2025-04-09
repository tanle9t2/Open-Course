package com.tp.opencourse.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tp.opencourse.dto.FileDTO;
import com.tp.opencourse.dto.VideoDTO;
import com.tp.opencourse.entity.enums.Type;
import com.tp.opencourse.mapper.ResourceMapper;
import com.tp.opencourse.service.CloudinaryService;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ResourceMapper resourceMapper;
    private static final String FFMPEG_PROBE_PATH = "D:\\ffmpeg-7.1.1-essentials_build\\bin\\ffprobe.exe"; // Change path based on your OS

    private String extractPublicId(String url) {
        try {
            // Get the part after /upload/
            String[] parts = url.split("/upload/");
            String path = parts[1]; // e.g., "v1234567890/phân công.docx.pdf"

            // Remove version (v123...) and get the file path
            String[] pathParts = path.split("/", 2);
            String filePath = pathParts[1];

            // Decode URL-encoded characters
            String decoded = java.net.URLDecoder.decode(filePath, "UTF-8");

            // Remove only the LAST extension (.pdf), keep .docx

            return decoded;

        } catch (Exception e) {
            throw new RuntimeException("Invalid Cloudinary URL");
        }
    }

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
                    .name(file.getOriginalFilename())
                    .createdAt(LocalDateTime.now())
                    .build();
            return fileDTO;
        } catch (IOException e) {
            throw new RuntimeException("Upload file failed");
        }
    }

    @Override
    public VideoDTO uploadVideo(MultipartFile file) throws IOException {
        try {

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "video",  // This ensures Cloudinary doesn't treat it as an image
                    "public_id", file.getOriginalFilename().split("\\.")[0] // Custom path
            ));
            VideoDTO videoDTO = VideoDTO.builder()
                    .type("VIDEO")
                    .name(file.getOriginalFilename())
                    .url(uploadResult.get("secure_url").toString())
                    .createdAt(LocalDateTime.now())
                    .duration(getVideoDuration(file))
                    .build();
            return videoDTO;
        } catch (IOException e) {
            throw new RuntimeException("Upload file faild");
        }
    }

    @Override
    public void removeResource(String url, String type) throws IOException {
        String publicId = extractPublicId(url);
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                "type", "upload",
                "resource_type", type // e.g., "image" or "video"
        ));
    }

    private double getVideoDuration(MultipartFile file) throws IOException {
        // Save the file temporarily
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);

        // Use FFprobe to analyze the file
        FFprobe ffprobe = new FFprobe(FFMPEG_PROBE_PATH);
        FFmpegProbeResult probeResult = ffprobe.probe(tempFile.getAbsolutePath());

        // Get duration (in seconds)
        double duration = probeResult.getFormat().duration;

        // Delete the temporary file
        tempFile.delete();

        return duration;
    }
}
