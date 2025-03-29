package com.tp.opencourse.service;

import com.tp.opencourse.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    FileDTO uploadFile(MultipartFile file) throws IOException;

}
