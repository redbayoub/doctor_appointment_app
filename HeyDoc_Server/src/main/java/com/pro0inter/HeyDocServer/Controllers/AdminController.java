package com.pro0inter.HeyDocServer.Controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pro0inter.HeyDocServer.Constants;
import com.pro0inter.HeyDocServer.Domain.Admin;
import com.pro0inter.HeyDocServer.Domain.DTOs.AdminDTO_Out;
import com.pro0inter.HeyDocServer.Domain.User;
import com.pro0inter.HeyDocServer.Security.SecurityUtils;
import com.pro0inter.HeyDocServer.Services.AdminService;
import com.pro0inter.HeyDocServer.Services.UserService;
import com.pro0inter.HeyDocServer.utils.ObjectMapperUtils;
import com.pro0inter.HeyDocServer.utils.RestErrorResponse;
import com.pro0inter.HeyDocServer.utils.Roles;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/admins")
public class AdminController {

    PropertyMap<Admin, AdminDTO_Out> mapping_a_dtoOUT = new PropertyMap<Admin, AdminDTO_Out>() {
        protected void configure() {
            map().setAccount_id(source.getId());
            map().setUser_id(source.getUser().getId());
            map().setFirstName(source.getUser().getFirstName());
            map().setLastName(source.getUser().getLastName());
            map().setGender(source.getUser().getGender());
            map().setDateOfBirth(source.getUser().getDateOfBirth());
        }
    };
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    @PostMapping("/")
    ResponseEntity add_admin(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @Valid @RequestBody AdminDTO_Out adminDTO_out) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            User foundedUser = userService.get_user(adminDTO_out.getUser_id());
            if (foundedUser == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected user not found"),
                        HttpStatus.NOT_FOUND);
            if (Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.DIRECTOR.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

            if (adminService.existByUserId(foundedUser.getId())) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST,
                        "Admin account already exist"), HttpStatus.BAD_REQUEST);
            }


            Admin newAdmin = new Admin();
            newAdmin.setDirector(adminDTO_out.isDirector());
            newAdmin.setUser(foundedUser);
            //foundedUser.setAdmin(newAdmin);
            Admin sv_Admin = adminService.add_admin(newAdmin);

            if (sv_Admin != null) {
                Map<String, Object> claims = securityUtils.getClaims(firebaseToken);
                if (claims == null) claims = new HashMap<>();
                claims.put(Roles.ADMIN.name(), true);
                if (sv_Admin.isDirector())
                    claims.put(Roles.DIRECTOR.name(), true);
                else
                    claims.put(Roles.DIRECTOR.name(), false);
              securityUtils.setClaims(firebaseToken.getUid(),claims);
            }

            ModelMapper modelMapper = new ModelMapper();
            modelMapper.addMappings(mapping_a_dtoOUT);
            return new ResponseEntity(
                    modelMapper.map(sv_Admin, AdminDTO_Out.class),
                    HttpStatus.CREATED);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }

    }


    @GetMapping("/{account_id}")
    ResponseEntity find_by_id(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @PathVariable("account_id") long account_id) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Admin founded_admin = adminService.get_admin(account_id);
            if (founded_admin == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Admin account dosn't exist"), HttpStatus.NOT_FOUND);

            if (firebaseToken.getUid().equals(founded_admin.getUser().getUid()) ||
                    Boolean.TRUE.equals((((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.DIRECTOR.name(), false)))
            ) {
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.addMappings(mapping_a_dtoOUT);

                return new ResponseEntity(
                        modelMapper.map(founded_admin, AdminDTO_Out.class),
                        HttpStatus.FOUND);

            } else
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/")
    ResponseEntity get_admin_list(@RequestHeader(Constants.FIREBASE_HEADER) String id_token) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            if (Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.DIRECTOR.name(), false))) {
                ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils(mapping_a_dtoOUT);
                List<Admin> admins = adminService.getAll();
                return new ResponseEntity(
                        objectMapperUtils.mapAll(admins, AdminDTO_Out.class),
                        HttpStatus.OK);

            } else
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);


        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }

    }


    @PutMapping("/{admin_id}")
    ResponseEntity update_admin(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                @PathVariable("admin_id") long admin_id,
                                @RequestBody AdminDTO_Out adminDTO_out) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Admin admin = adminService.get_admin(admin_id);
            if (admin == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Admin account dosn't exist"), HttpStatus.NOT_FOUND);


            if (Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.DIRECTOR.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }


            admin.setDirector(adminDTO_out.isDirector());


            Admin saved_admin = adminService.update_admin(admin);
            if (saved_admin != null) {
                Map<String, Object> claims = securityUtils.getClaims(firebaseToken);
                if (claims == null) claims = new HashMap<>();
                claims.put(Roles.ADMIN.name(),true);
                if (saved_admin.isDirector())
                    claims.put(Roles.DIRECTOR.name(), true);
                else
                    claims.put(Roles.DIRECTOR.name(), false);
              securityUtils.setClaims(firebaseToken.getUid(),claims);
            }
           /* User updated_user=userService.update_user(foundedUser);
            Patient saved_patient=updated_user.getPatient();*/

            /*foundedUser.setPatient(newPatient);
            User updated_user=userService.update_user(foundedUser);

            Patient saved_patient=updated_user.getPatient();*/
            //Patient saved_patient=patientService.add_patient(newPatient);
            /*
            TODO
            claims.put(Roles.PATIENT.name(), true);
            if(!claims.isEmpty()){
              securityUtils.setClaims(firebaseToken.getUid(),claims);
            }*/
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.addMappings(mapping_a_dtoOUT);
            return new ResponseEntity(
                    modelMapper.map(saved_admin, AdminDTO_Out.class),
                    HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{admin_id}")
    ResponseEntity delete_admin(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                @PathVariable("admin_id") long admin_id) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Admin admin = adminService.get_admin(admin_id);
            if (admin == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST,
                        "Admin account dosn't exist"), HttpStatus.BAD_REQUEST);


            if (Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.DIRECTOR.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }


            adminService.delete_admin(admin_id);


            Map<String, Object> claims = securityUtils.getClaims(firebaseToken);
            if (claims == null) claims = new HashMap<>();
            claims.put(Roles.ADMIN.name(), false);
            claims.put(Roles.DIRECTOR.name(), false);
          securityUtils.setClaims(firebaseToken.getUid(),claims);

           /* User updated_user=userService.update_user(foundedUser);
            Patient saved_patient=updated_user.getPatient();*/

            /*foundedUser.setPatient(newPatient);
            User updated_user=userService.update_user(foundedUser);

            Patient saved_patient=updated_user.getPatient();*/
            //Patient saved_patient=patientService.add_patient(newPatient);
            /*
            TODO
            claims.put(Roles.PATIENT.name(), true);
            if(!claims.isEmpty()){
              securityUtils.setClaims(firebaseToken.getUid(),claims);
            }*/
            return new ResponseEntity(
                    HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


}
