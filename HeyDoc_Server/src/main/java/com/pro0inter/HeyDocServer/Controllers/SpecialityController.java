package com.pro0inter.HeyDocServer.Controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pro0inter.HeyDocServer.Constants;
import com.pro0inter.HeyDocServer.Domain.DTOs.SpecialityDTO;
import com.pro0inter.HeyDocServer.Domain.Speciality;
import com.pro0inter.HeyDocServer.Security.SecurityUtils;
import com.pro0inter.HeyDocServer.Services.SpecialityService;
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
@RequestMapping("/specialities")
public class SpecialityController {

    @Autowired
    private SpecialityService specialityService;

    @GetMapping("/")
    ResponseEntity get_list() {

        ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils();
        List<Speciality> specialities = specialityService.getAll();
        return new ResponseEntity(
                objectMapperUtils.mapAll(specialities, SpecialityDTO.class),
                HttpStatus.OK);
    }

    @GetMapping("/{speciality_idOrTitle}")
    ResponseEntity get_speciality_by_id(@PathVariable("speciality_idOrTitle") String speciality_idOrTitle) {
        Speciality speciality = null;
        try {
            speciality = specialityService.get_speciality(Long.parseLong(speciality_idOrTitle));
        } catch (NumberFormatException e) {
            speciality = specialityService.get_speciality(speciality_idOrTitle);
        }

        ModelMapper modelMapper = new ModelMapper();

        return new ResponseEntity(
                modelMapper.map(speciality, SpecialityDTO.class),
                HttpStatus.OK);
    }

    @PostMapping("/")
    ResponseEntity add_spiciality(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @Valid @RequestBody SpecialityDTO specialityDTO) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            Speciality foundedSpeciality = specialityService.get_speciality(specialityDTO.getTitle());

            if (foundedSpeciality != null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.FOUND,
                        "Selected speciality exists"),
                        HttpStatus.FOUND);
            if (Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

            ModelMapper modelMapper = new ModelMapper();
            Speciality speciality = modelMapper.map(specialityDTO, Speciality.class);

            Speciality sv_speciality = specialityService.add_speciality(speciality);

            return new ResponseEntity(
                    modelMapper.map(sv_speciality, SpecialityDTO.class),
                    HttpStatus.CREATED);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/{speciality_id}")
    ResponseEntity update_speciality(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                     @PathVariable("speciality_id") long speciality_id,
                                     @RequestBody SpecialityDTO specialityDTO) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Speciality speciality = specialityService.get_speciality(speciality_id);
            if (speciality == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Speciality dosn't exist"), HttpStatus.NOT_FOUND);


            if (Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

            speciality.setTitle(specialityDTO.getTitle());
            speciality.setDescription(specialityDTO.getDescription());

            Speciality sv_speciality = specialityService.update_speciality(speciality);

            ModelMapper modelMapper = new ModelMapper();

            return new ResponseEntity(
                    modelMapper.map(sv_speciality, SpecialityDTO.class),
                    HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/{speciality_id}")
    ResponseEntity delete_speciality(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                     @PathVariable("speciality_id") long speciality_id) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Speciality speciality = specialityService.get_speciality(speciality_id);
            if (speciality == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Speciality dosn't exist"), HttpStatus.NOT_FOUND);


            if (Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }


            specialityService.delete_speciality(speciality_id);

            return new ResponseEntity(
                    HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


}
