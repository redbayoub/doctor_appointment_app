package com.pro0inter.HeyDocServer.Controllers;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pro0inter.HeyDocServer.Constants;
import com.pro0inter.HeyDocServer.Domain.Admin;
import com.pro0inter.HeyDocServer.Domain.DTOs.*;
import com.pro0inter.HeyDocServer.Domain.Doctor;
import com.pro0inter.HeyDocServer.Domain.Patient;
import com.pro0inter.HeyDocServer.Domain.User;
import com.pro0inter.HeyDocServer.Security.SecurityUtils;
import com.pro0inter.HeyDocServer.Services.AdminService;
import com.pro0inter.HeyDocServer.Services.DoctorService;
import com.pro0inter.HeyDocServer.Services.PatientService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {
    PropertyMap<User, UserDTO_Out> custom_mapping = new PropertyMap<User, UserDTO_Out>() {
        protected void configure() {
            map().setUser_id(source.getId());
            /*map().setEmailVerified(source.isEmailVerified());*/
            //map(source.getPatient().getId(), destination.getId());
        }
    };
    PropertyMap<Patient, PatientDTO_Out> mapping_patient_dtoOUT = new PropertyMap<Patient, PatientDTO_Out>() {
        protected void configure() {
            map().setAccount_id(source.getId());
            map().setUser_id(source.getUser().getId());
            map().setFirstName(source.getUser().getFirstName());
            map().setLastName(source.getUser().getLastName());
            map().setGender(source.getUser().getGender());
            map().setDateOfBirth(source.getUser().getDateOfBirth());
        }
    };
    PropertyMap<Doctor, DoctorDTO_Out> mapping_doctor_dtoOUT = new PropertyMap<Doctor, DoctorDTO_Out>() {
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
    private UserService userService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private AdminService adminService;

    @PostMapping("/")
    ResponseEntity sign_up(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @RequestBody UserDTO_SignUp user_dto) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            ModelMapper modelMapper = new ModelMapper();
            User user = modelMapper.map(user_dto, User.class);

            user.setUid(firebaseToken.getUid());


            User saved_user = userService.add_user(user);

            modelMapper.addMappings(custom_mapping);

            return new ResponseEntity(
                    modelMapper.map(saved_user, UserDTO_Out.class),
                    HttpStatus.CREATED);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{idORuid}")
    ResponseEntity get_user_by_id(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @PathVariable("idORuid") String idORuid) {
        Long id = -1L;
        String uid = null;
        try {
            id = Long.parseLong(idORuid);
        } catch (NumberFormatException e) {
            uid = idORuid;
        }


        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            User foundedUser = null;
            if (id != -1) foundedUser = userService.get_user(id);
            else if (uid != null)
                foundedUser = (User) userService.get_user(uid);

            if (foundedUser == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "User not found"), HttpStatus.NOT_FOUND);
            if (firebaseToken.getUid().equals(foundedUser.getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.APPROVED_DOCTOR.name(), false)) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {
                ModelMapper modelMapper = new ModelMapper();

                modelMapper.addMappings(custom_mapping);

                return new ResponseEntity(
                        modelMapper.map(foundedUser, UserDTO_Out.class),
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

    @GetMapping("/")
    ResponseEntity get_all_users(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            if (Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

            ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils(custom_mapping);


            return new ResponseEntity(objectMapperUtils.mapAll(userService.get_all_users(), UserDTO_Out.class), HttpStatus.OK);


        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/{id}")
    ResponseEntity update_user_by_id(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @PathVariable("id") long id,
            @RequestBody UserDTO_Out user_dto) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            User foundedUser = userService.get_user(id);
            if (foundedUser == null)
                return new ResponseEntity(HttpStatus.NOT_FOUND);

            if (firebaseToken.getUid().equals(foundedUser.getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {

                if (user_dto.getFirstName() != null)
                    foundedUser.setFirstName(user_dto.getFirstName());
                if (user_dto.getLastName() != null)
                    foundedUser.setLastName(user_dto.getLastName());
                if (user_dto.getDateOfBirth() != null)
                    foundedUser.setDateOfBirth(user_dto.getDateOfBirth());
                if (user_dto.getGender() != null)
                    foundedUser.setGender(user_dto.getGender());

                User savedUser = userService.update_user(foundedUser);
                ModelMapper modelMapper = new ModelMapper();

                modelMapper.addMappings(custom_mapping);

                return new ResponseEntity(
                        modelMapper.map(savedUser, UserDTO_Out.class),
                        HttpStatus.OK);
            }
            return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                    "You're not authorized"),
                    HttpStatus.UNAUTHORIZED);


        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity delete_user_by_id(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @PathVariable("id") long id) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            User foundedUser = userService.get_user(id);

            if (foundedUser == null)
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            if (firebaseToken.getUid().equals(foundedUser.getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {


                userService.delete_user(id);





                FirebaseAuth.getInstance().deleteUser(firebaseToken.getUid());

                return new ResponseEntity(HttpStatus.OK);

            } else
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{user_id}/patient")
    ResponseEntity get_patient(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                               @PathVariable("user_id") long user_id) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Patient foundedPatient = patientService.findByUserId(user_id);
            if (foundedPatient == null)
                return new ResponseEntity(HttpStatus.NOT_FOUND);

            if (firebaseToken.getUid().equals(foundedPatient.getUser().getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {

                ModelMapper modelMapper = new ModelMapper();
                modelMapper.addMappings(mapping_patient_dtoOUT);

                return new ResponseEntity(modelMapper.map(foundedPatient, PatientDTO_Out.class), HttpStatus.OK);
            }
            return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                    "You're not authorized"),
                    HttpStatus.UNAUTHORIZED);


        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }

    }
/*

    private PatientDTO_Out convertUserAndPatientToPatientDTO(User foundedUser, Patient foundedPatient) {
        PatientDTO_Out out=new PatientDTO_Out();

        out.setAccount_id(foundedPatient.getId());
        out.setUser_id(foundedUser.getId());
        out.setFirstName(foundedUser.getFirstName());
        out.setLastName(foundedUser.getLastName());
        out.setGender(foundedUser.getGender());
        out.setDateOfBirth(foundedUser.getDateOfBirth());

        out.setBlood_type(foundedPatient.getBlood_type());
        out.setContact_phone_number(foundedPatient.getContact_phone_number());
        out.setEmergency_contact_phone_number(foundedPatient.getEmergency_contact_phone_number());

        return out;

    }
*/

    @GetMapping("/{user_id}/doctor")
    ResponseEntity get_doctor(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                              @PathVariable("user_id") long user_id) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            Doctor foundedDoctor = doctorService.findByUserId(user_id);
            if (foundedDoctor == null)
                return new ResponseEntity(HttpStatus.NOT_FOUND);

            if (firebaseToken.getUid().equals(foundedDoctor.getUser().getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {


                ModelMapper modelMapper = new ModelMapper();
                modelMapper.addMappings(mapping_doctor_dtoOUT);

                return new ResponseEntity(modelMapper.map(foundedDoctor, DoctorDTO_Out.class), HttpStatus.OK);
            }
            return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                    "You're not authorized"),
                    HttpStatus.UNAUTHORIZED);


        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }

    }


    @GetMapping("/{user_id}/admin")
    ResponseEntity get_admin(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                             @PathVariable("user_id") long user_id) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            Admin foundedAdmin = adminService.findByUserId(user_id);
            if (foundedAdmin == null)
                return new ResponseEntity(HttpStatus.NOT_FOUND);

            if (firebaseToken.getUid().equals(foundedAdmin.getUser().getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.DIRECTOR.name(), false))
            ) {


                ModelMapper modelMapper = new ModelMapper();
                modelMapper.addMappings(mapping_doctor_dtoOUT);

                return new ResponseEntity(modelMapper.map(foundedAdmin, AdminDTO_Out.class), HttpStatus.OK);
            }
            return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                    "You're not authorized"),
                    HttpStatus.UNAUTHORIZED);


        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }

    }

    /*private DoctorDTO_Out convertUserAndDoctorToDoctorDTO(User foundedUser, Doctor foundedDoctor) {
        ObjectMapperUtils mapperUtils=new ObjectMapperUtils();
        DoctorDTO_Out out=new DoctorDTO_Out();

        out.setAccount_id(foundedDoctor.getId());
        out.setUser_id(foundedUser.getId());
        out.setFirstName(foundedUser.getFirstName());
        out.setLastName(foundedUser.getLastName());
        out.setGender(foundedUser.getGender());
        out.setDateOfBirth(foundedUser.getDateOfBirth());

        out.setAddress(foundedDoctor.getAddress());
        out.setApproved(foundedDoctor.isApproved());
        out.setClinic_addrs_lat(foundedDoctor.getClinic_addrs_lat());
        out.setClinic_addrs_lat(foundedDoctor.getClinic_addrs_lng());
        out.setPicture(foundedDoctor.getPicture());
        out.setZipCode(foundedDoctor.getZipCode());
        out.setSpecialities(mapperUtils.mapAll(foundedDoctor.getSpecialities(), SpecialityDTO.class));
        out.setWorkingSchedule(mapperUtils.mapAll(foundedDoctor.getWorkingSchedule(),WorkingScheduleDTO.class));

        return out;
    }*/


/*
    @PostMapping("/{user_id}/patient")
    ResponseEntity add_patient(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                               @PathVariable("user_id") long user_id,
                               @Valid @RequestBody PatientDTO_Out newPatientDTO_in ){

        SecurityUtils securityUtils=new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            User foundedUser=userService.get_user(firebaseToken.getUid());
            if(foundedUser==null)
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            if(foundedUser.getPatient()!=null)
                return new ResponseEntity("Patient account already exist",HttpStatus.BAD_REQUEST);

            ObjectMapperUtils objectMapperUtils=new ObjectMapperUtils();

            Patient newPatient=objectMapperUtils.map(newPatientDTO_in, Patient.class);
            newPatient.setUser(foundedUser);
            foundedUser.setPatient(newPatient);
            User updated_user=userService.update_user(foundedUser);

            Patient saved_patient=updated_user.getPatient();
            *//*
            claims.put(Roles.PATIENT.name(), true);
            if(!claims.isEmpty()){
              securityUtils.setClaims(firebaseToken.getUid(),claims);
            }*//*
            return new ResponseEntity(
                    objectMapperUtils.map(saved_patient, PatientDTO_Out.class ),
                    HttpStatus.CREATED);

        }catch (FirebaseAuthException e) {
            return new ResponseEntity(e.getLocalizedMessage(),HttpStatus.BAD_REQUEST);
        }*/
}



/*
set claims
 User currUser=userService.get_user(firebaseToken.getUid());
                Map<String, Object> claims = new HashMap<>();

                if(currUser.getPatient()!=null && ! currUser.getPatient().isDeleted()){
                    claims.put(Roles.PATIENT.name(), true);
                }

                if(currUser.getDoctor()!=null && ! currUser.getDoctor().isDeleted()){
                    claims.put(Roles.APPROVED_DOCTOR.name(), true);
                }

                if(currUser.getAdmin()!=null && ! currUser.getAdmin().isDeleted()){
                    claims.put(Roles.ADMIN.name(), true);
                    if(currUser.getAdmin().isDirector())
                        claims.put(Roles.DIRECTOR.name(), true);
                }

                if(!claims.isEmpty()){
                  securityUtils.setClaims(firebaseToken.getUid(),claims);
                }
 */
