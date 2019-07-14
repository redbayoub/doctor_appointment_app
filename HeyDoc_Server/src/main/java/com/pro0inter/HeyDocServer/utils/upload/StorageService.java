package com.pro0inter.HeyDocServer.utils.upload;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface StorageService {
    void init();

    String store(String newFileName,MultipartFile file);

    String[] list();

    Resource load(String filename);

    int delete(String filename);

    int deleteAll();

}
