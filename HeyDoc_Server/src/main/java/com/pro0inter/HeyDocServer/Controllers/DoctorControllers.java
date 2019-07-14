package com.pro0inter.HeyDocServer.Controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pro0inter.HeyDocServer.Constants;
import com.pro0inter.HeyDocServer.Domain.*;
import com.pro0inter.HeyDocServer.Domain.DTOs.*;
import com.pro0inter.HeyDocServer.Security.SecurityUtils;
import com.pro0inter.HeyDocServer.Services.*;
import com.pro0inter.HeyDocServer.Services.DoctorService;
import com.pro0inter.HeyDocServer.utils.ObjectMapperUtils;
import com.pro0inter.HeyDocServer.utils.RestErrorResponse;
import com.pro0inter.HeyDocServer.utils.Roles;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctors")
public class DoctorControllers {

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
    PropertyMap<com.pro0inter.HeyDocServer.Domain.DoctorService, DoctorServiceDTO> mapping_doctorService_dto = new PropertyMap<com.pro0inter.HeyDocServer.Domain.DoctorService, DoctorServiceDTO>() {
        protected void configure() {
            map().setDoctor_id(source.getId().getDoctorId());
            /*
            map().setEstimatedDuration(source.getEstimatedDuration());
            map().setFee(source.getFee());*/
        }
    };
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
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DoctorService_Service doctorService_Service;
    @Autowired
    private DocService_Service docService_service;
    @Autowired
    private WorkingScheduleService workingScheduleService;
    @Autowired
    private UserService userService;
    @Autowired
    private SpecialityService specialityService;
    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/")
    ResponseEntity add_doctor(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @Valid @RequestBody DoctorDTO_request doctorDTO_request) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);

            User foundedUser = userService.get_user(doctorDTO_request.getUser_id());
            if (foundedUser == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected user not found"),
                        HttpStatus.NOT_FOUND);


            if (doctorService.existByUserId(foundedUser.getId()))
                return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST,
                        "Doctor account already exist"), HttpStatus.BAD_REQUEST);

            ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils();

            Doctor newDoctor = new Doctor();

            newDoctor.setPicture(doctorDTO_request.getPicture());
            newDoctor.setClinic_addrs_lng(doctorDTO_request.getClinic_addrs_lng());
            newDoctor.setClinic_addrs_lat(doctorDTO_request.getClinic_addrs_lat());
            newDoctor.setAddress(doctorDTO_request.getAddress());
            newDoctor.setZipCode(doctorDTO_request.getZipCode());

            newDoctor.setUser(foundedUser);
            newDoctor.setSpecialities(
                    specialityService.findAllById(doctorDTO_request.getSpecialities().stream().map(specialityDTO -> specialityDTO.getId()).collect(Collectors.toList()))
            );

            //newDoctor.setSpecialities(objectMapperUtils.mapAll(doctorDTO_request.getSpecialities(), Speciality.class));
            List<WorkingSchedule> schedules = objectMapperUtils.mapAll(doctorDTO_request.getWorkingSchedule(), WorkingSchedule.class);
            schedules.forEach(workingSchedule -> workingSchedule.setDoctor(newDoctor));

            newDoctor.setWorkingSchedule(schedules);


            Doctor saved_doctor = doctorService.add_doctor(newDoctor);
            //List<com.pro0inter.HeyDocServer.Domain.DoctorService> services = new ArrayList<>();
            for (DoctorServiceDTO dto : doctorDTO_request.getDoctorServices()) {
                com.pro0inter.HeyDocServer.Domain.DoctorService service = new com.pro0inter.HeyDocServer.Domain.DoctorService();

                service.setId(new DoctorServicePK(saved_doctor.getId(), dto.getService().getId()));
                service.setEstimatedDuration(dto.getEstimatedDuration());
                service.setFee(dto.getFee());
                service.setDoctor(saved_doctor);
                service.setService(docService_service.get_docService(dto.getService().getId()));

                //services.add(service);
                doctorService_Service.add_doctorService(service);

            }
            //doctorService_Service.saveAll(services);

            objectMapperUtils.getModelMapper().addMappings(mapping_d_dtoOUT);

            if (saved_doctor != null) {
                Map<String, Object> claims = securityUtils.getClaims(firebaseToken);
                if (claims == null) claims = new HashMap<>();
                claims.put(Roles.NOT_APPROVED_DOCTOR.name(), true);

              securityUtils.setClaims(firebaseToken.getUid(),claims);
            }

            return new ResponseEntity(
                    objectMapperUtils.map(saved_doctor, DoctorDTO_Out.class),
                    HttpStatus.CREATED);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/{doctor_id}")
    ResponseEntity get_doctor_id(
            @PathVariable("doctor_id") Long doctor_id) {

        Doctor doctor = doctorService.get_doctor(doctor_id);

        if (doctor == null)
            return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                    "Selected doctor not found"),
                    HttpStatus.NOT_FOUND);


        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        modelMapper.addMappings(mapping_d_dtoOUT);

        return new ResponseEntity(
                modelMapper.map(doctor, DoctorDTO_Out.class),
                HttpStatus.OK);
    }

    @GetMapping("/{doctor_id}/services")
    ResponseEntity get_doctor_services(
            @PathVariable("doctor_id") Long doctor_id) {

        Doctor doctor = doctorService.get_doctor(doctor_id);

        if (doctor == null)
            return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                    "Selected doctor not found"),
                    HttpStatus.NOT_FOUND);

        List<com.pro0inter.HeyDocServer.Domain.DoctorService> doctorServices = doctorService_Service.get_by_doctor_id(doctor_id);
        ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils();
        objectMapperUtils.getModelMapper().getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);


        return new ResponseEntity(
                objectMapperUtils.mapAll(doctorServices, DoctorServiceDTO.class),
                HttpStatus.OK);
    }

    @GetMapping("/")
    ResponseEntity get_all_doctors() {
        ObjectMapperUtils mapperUtils = new ObjectMapperUtils(mapping_d_dtoOUT);
        mapperUtils.getModelMapper().getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        return new ResponseEntity(mapperUtils.mapAll(doctorService.get_all(), DoctorDTO_Out.class), HttpStatus.OK);
    }

    @PutMapping("/{doctor_id}")
    ResponseEntity update_doctor(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @PathVariable("doctor_id") Long doctor_id,
            @Valid @RequestBody DoctorDTO_update doctorDTO_update) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            Doctor doctor = doctorService.get_doctor(doctor_id);

            if (doctor == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected doctor not found"),
                        HttpStatus.NOT_FOUND);

            if (doctor.getUser().getUid().equals(firebaseToken.getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {
                if (Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {
                    if (doctorDTO_update.getApproved() != null)
                        doctor.setApproved(doctorDTO_update.getApproved());
                }

                if (doctorDTO_update.getAddress() != null)
                    doctor.setAddress(doctorDTO_update.getAddress());

                if (doctorDTO_update.getClinic_addrs_lat() != null)
                    doctorDTO_update.setClinic_addrs_lat(doctorDTO_update.getClinic_addrs_lat());

                if (doctorDTO_update.getClinic_addrs_lng() != null)
                    doctor.setClinic_addrs_lng(doctorDTO_update.getClinic_addrs_lng());

                if (doctorDTO_update.getPicture() != null)
                    doctor.setPicture(doctorDTO_update.getPicture());
                // todo specialities , working hour
                ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils();

                if (doctorDTO_update.getSpecialities() != null && !doctorDTO_update.getSpecialities().isEmpty())

                    doctor.setSpecialities(
                            specialityService.findAllById(doctorDTO_update.getSpecialities().stream().map(specialityDTO -> specialityDTO.getId()).collect(Collectors.toList()))
                    );

                if (doctorDTO_update.getWorkingSchedule() != null && !doctorDTO_update.getWorkingSchedule().isEmpty()) {

                    //doctor.getWorkingSchedule().forEach(workingSchedule -> workingScheduleService.delete_workingSchedule(workingSchedule.getId()));
                    workingScheduleService.deleteInBatch(doctor.getWorkingSchedule());
                    List<WorkingSchedule> saved_schedules = new ArrayList<>();

                    List<WorkingSchedule> schedulesToSave = objectMapperUtils.mapAll(doctorDTO_update.getWorkingSchedule(), WorkingSchedule.class);
                    schedulesToSave.forEach(workingSchedule -> {
                        workingSchedule.setDoctor(doctor);
                        saved_schedules.add(workingScheduleService.add_workingSchedule(workingSchedule));
                    });


                    doctor.setWorkingSchedule(saved_schedules);

                }

                if (doctorDTO_update.getDoctorServices() != null && !doctorDTO_update.getDoctorServices().isEmpty()) {
                    doctorService_Service.deleteAllByDoctorId(doctor.getId());


                    for (DoctorServiceDTO dto : doctorDTO_update.getDoctorServices()) {
                        com.pro0inter.HeyDocServer.Domain.DoctorService service = new com.pro0inter.HeyDocServer.Domain.DoctorService();

                        service.setId(new DoctorServicePK(doctor.getId(), dto.getService().getId()));
                        service.setEstimatedDuration(dto.getEstimatedDuration());
                        service.setFee(dto.getFee());
                        service.setDoctor(doctor);
                        service.setService(docService_service.get_docService(dto.getService().getId()));

                        doctorService_Service.add_doctorService(service);

                    }
                }


                Doctor sv_doctor = doctorService.update_doctor(doctor);
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.addMappings(mapping_d_dtoOUT);


                if (sv_doctor != null) {
                    Map<String, Object> claims = securityUtils.getClaims(firebaseToken);
                    if (claims == null) claims = new HashMap<>();
                    if (sv_doctor.isApproved()) {
                        claims.remove(Roles.NOT_APPROVED_DOCTOR);
                        claims.put(Roles.APPROVED_DOCTOR.name(), true);
                    } else {
                        claims.remove(Roles.APPROVED_DOCTOR);
                        claims.put(Roles.NOT_APPROVED_DOCTOR.name(), true);
                    }

                  securityUtils.setClaims(firebaseToken.getUid(),claims);
                }
                return new ResponseEntity(
                        modelMapper.map(sv_doctor, DoctorDTO_Out.class),
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

    @DeleteMapping("/{doctor_id}")
    ResponseEntity delete_doctor(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @PathVariable("doctor_id") Long doctor_id) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            Doctor doctor = doctorService.get_doctor(doctor_id);

            if (doctor == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected doctor not found"),
                        HttpStatus.NOT_FOUND);

            if ( //doctor.getUser().getUid().equals(firebaseToken.getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {

                doctorService.delete_doctor(doctor_id);


                Map<String, Object> claims = securityUtils.getClaims(firebaseToken);
                if (claims == null) claims = new HashMap<>();
                claims.remove(Roles.NOT_APPROVED_DOCTOR);
                claims.remove(Roles.APPROVED_DOCTOR);


              securityUtils.setClaims(firebaseToken.getUid(),claims);

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

    @GetMapping("/{doctor_id}/appointments")
    ResponseEntity get_doctor_appointments(@RequestHeader(Constants.FIREBASE_HEADER) String id_token,
                                           @PathVariable("doctor_id") long doctor_id) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            Doctor doctor = doctorService.get_doctor(doctor_id);
            if (doctor == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Doctor account dosn't exist"), HttpStatus.NOT_FOUND);

            if (firebaseToken.getUid().equals(doctor.getUser().getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))
            ) {


                ObjectMapperUtils objectMapperUtils = new ObjectMapperUtils(mapping_d_dtoOUT, mapping_p_dtoOUT);

                List<Appointment> appointments = appointmentService.findByDoctorId(doctor_id);

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
