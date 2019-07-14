package com.pro0inter.HeyDocServer.Services;

import com.pro0inter.HeyDocServer.Domain.Patient;
import com.pro0inter.HeyDocServer.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentService appointmentService;


    public Patient get_patient(Long patient_id) {
        Optional<Patient> optionalPatient = patientRepository.findById(patient_id);
        if (optionalPatient.isPresent())
            return optionalPatient.get();
        return null;
    }

    public Patient add_patient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient update_patient(Patient patient) {

        return patientRepository.save(patient);
    }

    public void delete_patient(Long patient_id) {
        appointmentService.deleteAllByPatientId(patient_id);
        patientRepository.deleteById(patient_id);
    }

    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    public boolean existByUserId(Long user_id) {
        return patientRepository.existByUserId(user_id) >= 1;
    }

    public Patient findByUserId(Long user_id) {
        Optional<Patient> optionalPatient = patientRepository.findByUserId(user_id);
        if (optionalPatient.isPresent())
            return optionalPatient.get();
        return null;
    }
}
