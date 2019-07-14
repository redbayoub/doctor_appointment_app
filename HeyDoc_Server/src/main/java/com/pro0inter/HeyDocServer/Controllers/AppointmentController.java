package com.pro0inter.HeyDocServer.Controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pro0inter.HeyDocServer.Constants;
import com.pro0inter.HeyDocServer.Domain.*;
import com.pro0inter.HeyDocServer.Domain.DTOs.AppointmentDTO;
import com.pro0inter.HeyDocServer.Domain.DTOs.AppointmentDTO_ADD;
import com.pro0inter.HeyDocServer.Domain.DTOs.DoctorDTO_Out;
import com.pro0inter.HeyDocServer.Domain.DTOs.PatientDTO_Out;
import com.pro0inter.HeyDocServer.Security.SecurityUtils;
import com.pro0inter.HeyDocServer.Services.*;
import com.pro0inter.HeyDocServer.Services.DoctorService;
import com.pro0inter.HeyDocServer.utils.RestErrorResponse;
import com.pro0inter.HeyDocServer.utils.Roles;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

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
    private AppointmentService appointmentService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorService doctorService;

    /*
        PropertyMap<Appointment, AppointmentDTO> custom_mapping_appoint_to_appointDTO = new PropertyMap<Appointment, AppointmentDTO>() {
            protected void configure() {
                ModelMapper modelMapper=new ModelMapper();
                modelMapper.addMappings(custom_mapping_doctor_to_doctorDtoMini);
                DoctorDTO_Mini doctorDTO_mini=modelMapper.map(source.getDoctor(),DoctorDTO_Mini.class);
                map().setDoctor(doctorDTO_mini);


                *//*map().setEmailVerified(source.isEmailVerified());*//*
            //map(source.getPatient().getId(), destination.getId());
        }
    };*/
    @Autowired
    private DoctorService_Service doctorService_service;
    @Autowired
    private WorkingScheduleService workingScheduleService;

    @GetMapping("/{appointment_id}")
    ResponseEntity get_appointment(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @PathVariable("appointment_id") Long appointment_id) {
        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            Appointment foundedAppointment = appointmentService.get_appointment(appointment_id);
            if (foundedAppointment == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected appointment not found"),
                        HttpStatus.NOT_FOUND);


            if (foundedAppointment.getPatient().getUser().getUid().equals(firebaseToken.getUid()) ||
                    foundedAppointment.getDoctor().getUser().getUid().equals(firebaseToken.getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {


                ModelMapper modelMapper = new ModelMapper();
                modelMapper.addMappings(mapping_d_dtoOUT);
                AppointmentDTO dto = modelMapper.map(foundedAppointment, AppointmentDTO.class);

                return new ResponseEntity(dto,
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


    @PostMapping("/")
    @Transactional
    ResponseEntity add_appointment(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @Valid @RequestBody AppointmentDTO_ADD appointmentDTO_add) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            Doctor doctor = doctorService.get_doctor(appointmentDTO_add.getDoctor_id());
            Patient patient = patientService.get_patient(appointmentDTO_add.getPatient_id());
            com.pro0inter.HeyDocServer.Domain.DoctorService doctorService = doctorService_service.get_doctorService(new DoctorServicePK(doctor.getId(), appointmentDTO_add.getDoctor_service_id()));
            WorkingSchedule workingSchedule = workingScheduleService.get_workingSchedule(appointmentDTO_add.getWorking_schedule_id());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");

            Appointment insertedAppointment = null;

            Calendar selDate = Calendar.getInstance();

            Calendar workingScheduleEndTime = Calendar.getInstance();
            workingScheduleEndTime.setTime(workingSchedule.getEndTime());

            selDate.set(Calendar.HOUR_OF_DAY, workingScheduleEndTime.get(Calendar.HOUR_OF_DAY));
            selDate.set(Calendar.MINUTE, workingScheduleEndTime.get(Calendar.MINUTE));


            while (insertedAppointment == null) {
                if (selDate.get(Calendar.DAY_OF_WEEK) == workingSchedule.getDayOfWeek()) {
                    Date maxDbEndTime = (Date) appointmentService.raw_query(
                            "SELECT MAX(`end_to`) FROM appointments WHERE DATE(`end_to`)='" + simpleDateFormat.format(selDate.getTime())
                                    + "' AND `doctor_id`=" + doctor.getId()
                    ).getSingleResult();

                    if (maxDbEndTime != null) {
                        Calendar maxDbEndTimePlusRequestedTime = Calendar.getInstance();
                        maxDbEndTimePlusRequestedTime.setTime(maxDbEndTime);
                        maxDbEndTimePlusRequestedTime.add(Calendar.MINUTE, doctorService.getEstimatedDuration());


                        if (selDate.after(maxDbEndTimePlusRequestedTime)) { // insert Appointment
                            Appointment appointment = new Appointment();
                            appointment.setDoctor(doctor);
                            appointment.setPatient(patient);
                            appointment.setStartTime(maxDbEndTime);
                            appointment.setEndTime(maxDbEndTimePlusRequestedTime.getTime());
                            appointment.setCanceled(false);
                            appointment.setRescheduled(false);
                            appointment.setFellowUpNumber(appointmentDTO_add.getFellowUpNumber());
                            appointment.setPatientProblem(appointmentDTO_add.getPatientProblem());

                            insertedAppointment = appointmentService.add_appointment(appointment);
                        }
                    } else {
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTime(selDate.getTime());

                        Calendar workingScheduleStartTime = Calendar.getInstance();
                        workingScheduleStartTime.setTime(workingSchedule.getStartTime());

                        startTime.set(Calendar.HOUR_OF_DAY, workingScheduleStartTime.get(Calendar.HOUR_OF_DAY));
                        startTime.set(Calendar.MINUTE, workingScheduleStartTime.get(Calendar.MINUTE));

                        Calendar endTime = Calendar.getInstance();
                        endTime.setTime(startTime.getTime());
                        endTime.add(Calendar.MINUTE, doctorService.getEstimatedDuration());

                        Appointment appointment = new Appointment();

                        appointment.setDoctor(doctor);
                        appointment.setPatient(patient);
                        appointment.setStartTime(startTime.getTime());
                        appointment.setEndTime(endTime.getTime());
                        appointment.setCanceled(false);
                        appointment.setRescheduled(false);
                        appointment.setFellowUpNumber(appointmentDTO_add.getFellowUpNumber());
                        appointment.setPatientProblem(appointmentDTO_add.getPatientProblem());

                        insertedAppointment = appointmentService.add_appointment(appointment);
                    }


                }
                selDate.add(Calendar.DAY_OF_YEAR, 1); // 7 to go directly to same week day
            }

            ModelMapper modelMapper = new ModelMapper();
            /*modelMapper.addConverter(new Converter<Appointment, AppointmentDTO>() {
                @Override
                public AppointmentDTO convert(MappingContext<Appointment, AppointmentDTO> mappingContext) {
                    ModelMapper modelMapper=new ModelMapper();
                    modelMapper.addMappings(custom_mapping_doctor_to_doctorDtoMini);
                    DoctorDTO_Mini doctorDTO_mini=modelMapper.map(mappingContext.getSource().getDoctor(),DoctorDTO_Mini.class);
                    map().setDoctor(doctorDTO_mini);
                    return null;
                }
            },Appointment.class,AppointmentDTO.class);*/
            modelMapper.addMappings(mapping_d_dtoOUT);
            modelMapper.addMappings(mapping_p_dtoOUT);
            AppointmentDTO dto = modelMapper.map(insertedAppointment, AppointmentDTO.class);

            return new ResponseEntity(dto, HttpStatus.OK);


        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{appointment_id}")
    @Transactional
    ResponseEntity update_appointment(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @PathVariable("appointment_id") Long appointment_id,
            @Valid @RequestBody AppointmentDTO new_info) {

        SecurityUtils securityUtils = new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            Appointment foundedAppointment = appointmentService.get_appointment(appointment_id);
            if (foundedAppointment == null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected appointment not found"),
                        HttpStatus.NOT_FOUND);


            if (foundedAppointment.getPatient().getUser().getUid().equals(firebaseToken.getUid()) ||
                    foundedAppointment.getDoctor().getUser().getUid().equals(firebaseToken.getUid()) ||
                    Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(), false))) {

                if (Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.PATIENT.name(), false))) {
                    if (new_info.getCanceled() != null && !foundedAppointment.isCanceled()) { // set as cnaceled with/without reason
                        foundedAppointment.setCanceled(true);
                        foundedAppointment.setCancelOrRescheduleReason(new_info.getCancelOrRescheduleReason());
                    }
                } else if (Boolean.TRUE.equals(((Map<String, Object>) firebaseToken.getClaims()).getOrDefault(Roles.APPROVED_DOCTOR.name(), false))) {
                    if (new_info.getFinished() != null &&
                            !foundedAppointment.isFinished() &&
                            foundedAppointment.getEndTime().before(new Date())
                    ) { // set as finished
                        foundedAppointment.setFinished(true);
                    }
                    // set as cnaceled with reason
                    if (new_info.getCanceled() != null && new_info.getCancelOrRescheduleReason() != null && !foundedAppointment.isCanceled()) {
                        foundedAppointment.setCanceled(true);
                        foundedAppointment.setCancelOrRescheduleReason(new_info.getCancelOrRescheduleReason());
                    }
                }
                Appointment up_appointment = appointmentService.update_appointment(foundedAppointment);
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.addMappings(mapping_d_dtoOUT);
                modelMapper.addMappings(mapping_p_dtoOUT);
                AppointmentDTO dto = modelMapper.map(up_appointment, AppointmentDTO.class);

                return new ResponseEntity(dto, HttpStatus.OK);

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





/*
    @PostMapping("/")
    ResponseEntity add_appointment(
            @RequestHeader(Constants.FIREBASE_HEADER) String id_token,
            @Valid @RequestBody AppointmentDTO doctorDTO_out){
        SecurityUtils securityUtils=new SecurityUtils();
        try {
            FirebaseToken firebaseToken = securityUtils.getFirebaseToken(id_token);
            User foundedUser=userService.get_user(doctorDTO_out.getUser_id());
            if(foundedUser==null)
                return new ResponseEntity(new RestErrorResponse(HttpStatus.NOT_FOUND,
                        "Selected user not found" ),
                        HttpStatus.NOT_FOUND);


            if(  Boolean.FALSE.equals(((Map<String,Object>)firebaseToken.getClaims()).getOrDefault(Roles.ADMIN.name(),false))){
                return new ResponseEntity(new RestErrorResponse(HttpStatus.UNAUTHORIZED,
                        "You're not authorized" ),
                        HttpStatus.UNAUTHORIZED);
            }

            if(doctorService.existByUserId(foundedUser.getId()))
                return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST,
                        "Doctor account already exist"),HttpStatus.BAD_REQUEST);



            ModelMapper modelMapper=new ModelMapper();
            Doctor new_doctor=modelMapper.map(doctorDTO_out,Doctor.class);
            new_doctor.setUser(foundedUser);


            Doctor saved_doctor=doctorService.add_doctor(new_doctor);
            modelMapper.addMappings(mapping_d_dtoOUT);

            return new ResponseEntity(
                    modelMapper.map(saved_doctor, DoctorDTO_Out.class),
                    HttpStatus.CREATED);

        } catch (FirebaseAuthException e) {
            return new ResponseEntity(new RestErrorResponse(HttpStatus.BAD_REQUEST,e.getLocalizedMessage() ),
                    HttpStatus.BAD_REQUEST);
        }

    }*/

}
