package com.tp.opencourse.service;

import com.tp.opencourse.dto.FileDTO;
import com.tp.opencourse.dto.VideoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    FileDTO uploadFile(MultipartFile file) throws IOException;
    VideoDTO uploadVideo(MultipartFile file)  throws IOException;

}
