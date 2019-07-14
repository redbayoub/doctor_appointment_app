package com.pro0inter.HeyDocServer.utils.upload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Primary
public class SimpleStroageServiceImpl implements StorageService {

    @Value("${com.pro0inter.HeyDocServer.upload.folder.path}")
    private String upload_folder_path;
    private File uploaded_fld;


    @Override
    public void init() {
        uploaded_fld=new File(upload_folder_path);
        uploaded_fld.mkdir();

    }



    @Override
    public String store(String newFileName,MultipartFile file) {
        if(uploaded_fld==null)init();

        try {
            File newFile=new File(uploaded_fld, newFileName);
            Files.copy(file.getInputStream(),newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return newFile.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String[] list() {
        if(uploaded_fld==null)init();
        return  uploaded_fld.list();

    }

    @Override
    public Resource load(String filename) {
        if(uploaded_fld==null)init();
        File requested_file=new File(uploaded_fld, filename);
        return new FileSystemResource(requested_file);

    }



    @Override
    public int delete(String filename) {
        File requested_file=new File(uploaded_fld,filename);
        return (requested_file.delete())?1:0;
    }

    @Override
    public int deleteAll() {
        int files_count=uploaded_fld.listFiles().length;
        for (File f:uploaded_fld.listFiles() ) {
            f.delete();
        }
        return files_count;
    }
}
