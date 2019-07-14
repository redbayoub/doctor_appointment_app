package com.pro0inter.HeyDocServer.Controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pro0inter.HeyDocServer.Constants;
import com.pro0inter.HeyDocServer.Domain.DTOs.DoctorServiceDTO;
import com.pro0inter.HeyDocServer.Domain.DocService;
import com.pro0inter.HeyDocServer.Domain.Doctor;
import com.pro0inter.HeyDocServer.Domain.DoctorServicePK;
import com.pro0inter.HeyDocServer.Security.SecurityUtils;
import com.pro0inter.HeyDocServer.Services.DocService_Service;
import com.pro0inter.HeyDocServer.Services.DoctorService;
import com.pro0inter.HeyDocServer.Services.DoctorService_Service;
import com.pro0inter.HeyDocServer.utils.RestErrorResponse;
import com.pro0inter.HeyDocServer.utils.Roles;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/doctors_services")
public class DoctorServiceController {
    PropertyMap<com.pro0inter.HeyDocServer.Domain.DoctorService, DoctorServiceDTO> mapping_doctorService_dto = new PropertyMap<com.pro0inter.HeyDocServer.Domain.DoctorService, DoctorServiceDTO>() {
        protected void configure() {
            map().setDoctor_id(source.getId().getDoctorId());

            map().setEstimatedDuration(source.getEstimatedDuration());
            map().setFee(source.getFee());
        }
    };
    @Autowired
    private DoctorService_Service DoctorService_Service;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DocService_Service docServiceService;

    @PostMapping("/")
    ResponseEntity add_doctor_service(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @Valid @RequestBody DoctorServiceDTO doctorServiceDTO) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);


            if (DoctorService_Service.existByDocIdAndDocServiceId(doctorServiceDTO.getDoctor_id(), doctorServiceDTO.getService().getId()))
                return new ResponseEntity(new RestErrorResponse(HttpStatus.FOUND,
                        "Selected DoctorService exists"),
                        HttpStatus.FOUND);

            Doctor doctor = doctorService.get_doctor(doctorServiceDTO.getDoctor_id());

            if (doctor == null) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected Doctor dosn't exists"),
                        HttpStatus.NOT_FOUND);
            }

            DocService docService = docServiceService.get_docService(doctorServiceDTO.getService().getId());
            if (docService == null) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected Doc Service dosn't exists"),
                        HttpStatus.NOT_FOUND);
            }


            if (!doctor.getUser().getUid().equals(firebaseToken.getUid())) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

            com.pro0inter.HeyDocServer.Domain.DoctorService doctorService = new com.pro0inter.HeyDocServer.Domain.DoctorService();

            doctorService.setId(new DoctorServicePK(doctorServiceDTO.getDoctor_id(), doctorServiceDTO.getService().getId()));
            doctorService.setEstimatedDuration(doctorServiceDTO.getEstimatedDuration());
            doctorService.setFee(doctorServiceDTO.getFee());

            com.pro0inter.HeyDocServer.Domain.DoctorService sv_DoctorService = DoctorService_Service.add_doctorService(doctorService);


            ModelMapper modelMapper = new ModelMapper();
            modelMapper.addMappings(mapping_doctorService_dto);


            return new ResponseEntity(
                    modelMapper.map(sv_DoctorService, DoctorServiceDTO.class),
                    HttpStatus.CREATED);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/")
    ResponseEntity update_docservice(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                     @Valid @RequestBody DoctorServiceDTO doctorServiceDTO) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            DoctorServicePK docservice_id = new DoctorServicePK(doctorServiceDTO.getDoctor_id(), doctorServiceDTO.getService().getId());

            com.pro0inter.HeyDocServer.Domain.DoctorService doctorservice = DoctorService_Service.get_doctorService(docservice_id);
            if (doctorservice == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected Doctor Service dosn't exist"), HttpStatus.NOT_FOUND);


            Doctor doctor = doctorService.get_doctor(doctorServiceDTO.getDoctor_id());

            if (doctor == null) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected Doctor dosn't exists"),
                        HttpStatus.NOT_FOUND);
            }


            if (!doctor.getUser().getUid().equals(firebaseToken.getUid())) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

            doctorservice.setFee(doctorServiceDTO.getFee());
            doctorservice.setEstimatedDuration(doctorServiceDTO.getEstimatedDuration());


            com.pro0inter.HeyDocServer.Domain.DoctorService sv_doctorservice = DoctorService_Service.update_doctorService(doctorservice);

            ModelMapper modelMapper = new ModelMapper();
            modelMapper.addMappings(mapping_doctorService_dto);

            return new ResponseEntity(
                    modelMapper.map(sv_doctorservice, DoctorServiceDTO.class),
                    HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/")
    ResponseEntity delete_docservice(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                     @RequestBody DoctorServiceDTO doctorServiceDTO) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            DoctorServicePK docservice_id = new DoctorServicePK(doctorServiceDTO.getDoctor_id(), doctorServiceDTO.getService().getId());

            com.pro0inter.HeyDocServer.Domain.DoctorService doctorservice = DoctorService_Service.get_doctorService(docservice_id);
            if (doctorservice == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected Doctor Service dosn't exist"), HttpStatus.NOT_FOUND);


            Doctor doctor = doctorService.get_doctor(doctorServiceDTO.getDoctor_id());

            if (doctor == null) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected Doctor dosn't exists"),
                        HttpStatus.NOT_FOUND);
            }


            if (doctor.getUser().getUid().equals(firebaseToken.getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {

                DoctorService_Service.delete_doctorService(docservice_id);

                return new ResponseEntity(
                        HttpStatus.OK);


            } else {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }


        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


}
