package com.pro0inter.HeyDocServer.Controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.pro0inter.HeyDocServer.Constants;
import com.pro0inter.HeyDocServer.Domain.DTOs.UserDTO_Out;
import com.pro0inter.HeyDocServer.Security.SecurityUtils;
import com.pro0inter.HeyDocServer.utils.RestErrorResponse;
import com.pro0inter.HeyDocServer.utils.Roles;
import com.pro0inter.HeyDocServer.utils.upload.SimpleStroageServiceImpl;
import com.pro0inter.HeyDocServer.utils.upload.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class FileUploadContoller {

    @Autowired
    private StorageService storageService;


    @GetMapping("/list/")
    public ResponseEntity listUploadedFiles(@RequestHeader(Constants.FIREBASE_HEADER) String id_token) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            if (
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            )
                return ResponseEntity.ok().body(storageService.list());
            else {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/files/{filename}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        Resource res = storageService.load(filename);
        if (res != null && res.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            res.getFilename()).body(res);

        } else
            return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity handleFileUpload(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                           @RequestParam("uploaded_file") MultipartFile file) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            String newName=firebaseToken.getUid()+"_picture"+getFileExtension(file.getOriginalFilename());
            String newFilename = storageService.store(newName,file);

            if (newFilename != null && !newFilename.isEmpty()) {
                /* Todo enable after having a static domain ( deployed )
                if(firebaseToken.getPicture()==null||firebaseToken.getPicture().trim().isEmpty()){
                    UserRecord.UpdateRequest updateRequest=new UserRecord.UpdateRequest(firebaseToken.getUid());
                    updateRequest.setPhotoUrl(newFilename);
                    FirebaseAuth.getInstance().updateUserAsync(updateRequest);
                }*/
                return ResponseEntity.ok().body(newFilename);
            } else
                return ResponseEntity.badRequest().build();
        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private static String getFileExtension(String filename){
        return filename.substring(filename.lastIndexOf('.')+1);
    }

}
