package com.pro0inter.HeyDocServer.Controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pro0inter.HeyDocServer.Constants;
import com.pro0inter.HeyDocServer.Domain.DTOs.DocServiceDTO;
import com.pro0inter.HeyDocServer.Domain.DocService;
import com.pro0inter.HeyDocServer.Security.SecurityUtils;
import com.pro0inter.HeyDocServer.Services.DocService_Service;
import com.pro0inter.HeyDocServer.utils.ObjectMapperUtils;
import com.pro0inter.HeyDocServer.utils.RestErrorResponse;
import com.pro0inter.HeyDocServer.utils.Roles;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/doc_services")
public class DocServiceController {

    @Autowired
    private DocService_Service docServiceService;

    @GetMapping("/")
    ResponseEntity get_list() {

        ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils();
        List<DocService> docServices = docServiceService.getAll();
        return new ResponseEntity(
                objectMapperUtils.mapAll(docServices, DocServiceDTO.class),
                HttpStatus.OK);
    }

    @GetMapping("/{doc_service_idOrTitle}")
    ResponseEntity get_doc_service_by_id(@PathVariable("doc_service_idOrTitle") String doc_service_idOrTitle) {
        DocService docService = null;

        try {
            docService = docServiceService.get_docService(Long.parseLong(doc_service_idOrTitle));
        } catch (NumberFormatException e) {
            docService = docServiceService.get_docService(doc_service_idOrTitle);
        }

        ModelMapper modelMapper = new ModelMapper();

        return new ResponseEntity(
                modelMapper.map(docService, DocServiceDTO.class),
                HttpStatus.OK);
    }

    @PostMapping("/")
    ResponseEntity add_doc_service(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @Valid @RequestBody DocServiceDTO docServiceDTO) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            DocService foundedDocService = docServiceService.get_docService(docServiceDTO.getTitle());

            if (foundedDocService != null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.FOUND,
                        "Selected DocService exists"),
                        HttpStatus.FOUND);
            if ( //Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.APPROVED_DOCTOR.name(), false)) &&
                    Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

            ModelMapper modelMapper = new ModelMapper();
            DocService docService = modelMapper.map(docServiceDTO, DocService.class);

            DocService sv_docservice = docServiceService.add_docService(docService);

            return new ResponseEntity(
                    modelMapper.map(sv_docservice, DocServiceDTO.class),
                    HttpStatus.CREATED);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/{docservice_id}")
    ResponseEntity update_docservice(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                     @PathVariable("docservice_id") long docservice_id,
                                     @RequestBody DocServiceDTO docserviceDTO) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            DocService docservice = docServiceService.get_docService(docservice_id);
            if (docservice == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected DocService dosn't exist"), HttpStatus.NOT_FOUND);


            if (Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

            docservice.setTitle(docserviceDTO.getTitle());
            docservice.setDescription(docserviceDTO.getDescription());

            DocService sv_docservice = docServiceService.update_docService(docservice);

            ModelMapper modelMapper = new ModelMapper();

            return new ResponseEntity(
                    modelMapper.map(sv_docservice, DocServiceDTO.class),
                    HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/{docservice_id}")
    ResponseEntity delete_docservice(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                     @PathVariable("docservice_id") long docservice_id) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            DocService docservice = docServiceService.get_docService(docservice_id);
            if (docservice == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected DocService dosn't exist"), HttpStatus.NOT_FOUND);


            if (Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }


            docServiceService.delete_docService(docservice_id);

            return new ResponseEntity(
                    HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


}
