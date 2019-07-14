package com.pro0inter.HeyDocServer.Services;

import com.pro0inter.HeyDocServer.Domain.Doctor;
import com.pro0inter.HeyDocServer.Repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService_Service doctorService_service;


    public Doctor get_doctor(Long doctor_id) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctor_id);
        if (optionalDoctor.isPresent())
            return optionalDoctor.get();
        return null;
    }

    public Doctor add_doctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Doctor update_doctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void delete_doctor(Long doctor_id) {
        doctorService_service.deleteAllByDoctorId(doctor_id);
        appointmentService.deleteAllByDoctorId(doctor_id);
        doctorRepository.deleteById(doctor_id);
    }


    public boolean existByUserId(Long user_id) {
        return doctorRepository.existByUserId(user_id) >= 1;
    }

    public Doctor findByUserId(Long user_id) {
        Optional<Doctor> optionalDoctor = doctorRepository.findByUserId(user_id);
        if (optionalDoctor.isPresent())
            return optionalDoctor.get();
        return null;
    }


    public List<Doctor> get_all() {
        return doctorRepository.findAll();
    }
}
