package com.pro0inter.HeyDocServer.Controllers;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pro0inter.HeyDocServer.Constants;
import com.pro0inter.HeyDocServer.Domain.Appointment;
import com.pro0inter.HeyDocServer.Domain.DTOs.AppointmentDTO;
import com.pro0inter.HeyDocServer.Domain.DTOs.DoctorDTO_Out;
import com.pro0inter.HeyDocServer.Domain.DTOs.PatientDTO_Out;
import com.pro0inter.HeyDocServer.Domain.Doctor;
import com.pro0inter.HeyDocServer.Domain.Patient;
import com.pro0inter.HeyDocServer.Domain.User;
import com.pro0inter.HeyDocServer.Security.SecurityUtils;
import com.pro0inter.HeyDocServer.Services.AppointmentService;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/patients")
public class PatientController {
    PropertyMap<Patient, PatientDTO_Out> mapping_p_dtoOUT = new PropertyMap<Patient, PatientDTO_Out>() {
        protected void configure() {
            map().setAccount_id(source.getId());
            map().setUser_id(source.getUser().getId());
            map().setFirstName(source.getUser().getFirstName());
            map().setLastName(source.getUser().getLastName());
            map().setGender(source.getUser().getGender());
            map().setDateOfBirth(source.getUser().getDateOfBirth());
        }
    };
    PropertyMap<PatientDTO_Out, Patient> mapping_dtoOUT_p = new PropertyMap<PatientDTO_Out, Patient>() {
        protected void configure() {
            //map().setUser(userService.get_user(source.getUser_id()));
            map().setId(source.getAccount_id());
        }
    };
    PropertyMap<Doctor, DoctorDTO_Out> mapping_d_dtoOUT = new PropertyMap<Doctor, DoctorDTO_Out>() {
        protected void configure() {
            map().setAccount_id(source.getId());
            map().setUser_id(source.getUser().getId());
            map().setFirstName(source.getUser().getFirstName());
            map().setLastName(source.getUser().getLastName());
            map().setGender(source.getUser().getGender());
            map().setDateOfBirth(source.getUser().getDateOfBirth());

           /* ObjectMapperUtils mapperUtils = new ObjectMapperUtils();

            List<WorkingScheduleDTO> workingScheduleDTOs=mapperUtils.mapAll(source.getWorkingSchedule(), WorkingScheduleDTO.class);
            map().setWorkingSchedule(workingScheduleDTOs);

            List<SpecialityDTO> specialityDTOs=mapperUtils.mapAll(source.getSpecialities(), SpecialityDTO.class);

            map().setSpecialities(specialityDTOs);*/

        }
    };
    @Autowired
    private PatientService patientService;
    @Autowired
    private UserService userService;
    @Autowired
    private AppointmentService appointmentService;

/*

    PropertyMap<Appointment, AppointmentDTO> custom_mapping_appoint_to_appointDTO = new PropertyMap<Appointment, AppointmentDTO>() {
        protected void configure() {
            ModelMapper modelMapper=new ModelMapper();
            modelMapper.addMappings(custom_mapping_doctor_to_doctorDtoMini);
            map().setDoctor(modelMapper.map(source.getDoctor(),DoctorDTO_Mini.class));


            */
    /*map().setEmailVerified(source.isEmailVerified());*//*

            //map(source.getPatient().getId(), destination.getId());
        }
    };
*/

    @GetMapping("/{account_id}")
    ResponseEntity find_by_id(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @PathVariable("account_id") long account_id) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Patient patient = patientService.get_patient(account_id);
            if (patient == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Patient account dosn't exist"), HttpStatus.NOT_FOUND);

            if (firebaseToken.getUid().equals(patient.getUser().getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.APPROVED_DOCTOR.name(), false)) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {
                ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils(mapping_p_dtoOUT);

                return new ResponseEntity(
                        objectMapperUtils.map(patient, PatientDTO_Out.class),
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
    ResponseEntity get_patient_list(@RequestHeader(Constants.FIREBASE_HEADER) String id_token) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            if (Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils(mapping_p_dtoOUT);
                List<Patient> patients = patientService.getAll();
                return new ResponseEntity(
                        objectMapperUtils.mapAll(patients, PatientDTO_Out.class),
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


    @PostMapping("/")
    ResponseEntity add_patient(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                               @Valid @RequestBody PatientDTO_Out newPatientDTO_in) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            User foundedUser = userService.get_user(newPatientDTO_in.getUser_id());
            if (foundedUser == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected user not found"),
                        HttpStatus.NOT_FOUND);
            if (!firebaseToken.getUid().equals(foundedUser.getUid()) &&
                    Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }

            if (patientService.existByUserId(foundedUser.getId()))
                return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST,
                        "Patient account already exist"), HttpStatus.BAD_REQUEST);

            ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils();


            Patient newPatient = objectMapperUtils.map(newPatientDTO_in, Patient.class);
            newPatient.setUser(foundedUser);

            Patient saved_patient = patientService.add_patient(newPatient);


            if (saved_patient != null) {
                Map<String, Object> claims = securityUtils.getClaims(firebaseToken);
                if (claims == null) claims = new HashMap<>();
                claims.put(Roles.PATIENT.name(), true);

              securityUtils.setClaims(firebaseToken.getUid(),claims);
            }

            return new ResponseEntity(
                    objectMapperUtils.map(saved_patient, PatientDTO_Out.class),
                    HttpStatus.CREATED);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{patient_id}")
    ResponseEntity update_patient(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                  @PathVariable("patient_id") long patient_id,
                                  @RequestBody PatientDTO_Out patientDTO_out) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Patient patient = patientService.get_patient(patient_id);
            if (patient == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST,
                        "Patient account dosn't exist"), HttpStatus.BAD_REQUEST);


            if (!firebaseToken.getUid().equals(patient.getUser().getUid()) &&
                    Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }


            //ObjectMapperUtils objectMapperUtils=new ObjectMapperUtils(mapping_dtoOUT_p,mapping_p_dtoOUT);
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.addMappings(mapping_dtoOUT_p);
            modelMapper.addMappings(mapping_p_dtoOUT);


            Patient newPatient = modelMapper.map(patientDTO_out, Patient.class);
            newPatient.setUser(patient.getUser());

            Patient saved_patient = patientService.update_patient(newPatient);


            return new ResponseEntity(
                    modelMapper.map(saved_patient, PatientDTO_Out.class),
                    HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{patient_id}")
    ResponseEntity delete_patient(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                  @PathVariable("patient_id") long patient_id) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Patient patient = patientService.get_patient(patient_id);
            if (patient == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST,
                        "Patient account dosn't exist"), HttpStatus.BAD_REQUEST);


            if (!firebaseToken.getUid().equals(patient.getUser().getUid()) &&
                    Boolean.FALSE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized"),
                        HttpStatus.UNAUTHORIZED);
            }


            patientService.delete_patient(patient_id);


            Map<String, Object> claims = securityUtils.getClaims(firebaseToken);
            if (claims == null) claims = new HashMap<>();
            claims.remove(Roles.PATIENT.name());
          securityUtils.setClaims(firebaseToken.getUid(),claims);

            return new ResponseEntity(
                    HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{patient_id}/appointments")
    ResponseEntity get_patient_appointments(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                            @PathVariable("patient_id") long patient_id) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            Patient patient = patientService.get_patient(patient_id);
            if (patient == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Patient account dosn't exist"), HttpStatus.NOT_FOUND);

            if (firebaseToken.getUid().equals(patient.getUser().getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {


                ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils(mapping_d_dtoOUT, mapping_p_dtoOUT);

                List<Appointment> appointments = appointmentService.findByPatientId(patient_id);

                return new ResponseEntity(
                        objectMapperUtils.mapAll(appointments, AppointmentDTO.class),
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


}
