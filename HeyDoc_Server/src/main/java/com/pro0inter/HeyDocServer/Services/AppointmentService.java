package com.pro0inter.HeyDocServer.Services;

import com.pro0inter.HeyDocServer.Domain.Appointment;
import com.pro0inter.HeyDocServer.Repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EntityManager entityManager;


    public Appointment get_appointment(Long appointment_id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointment_id);
        if (optionalAppointment.isPresent())
            return optionalAppointment.get();
        return null;
    }

    public Appointment add_appointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Appointment update_appointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void delete_appointment(Long appointment_id) {
        appointmentRepository.deleteById(appointment_id);
    }

    public List<Appointment> findByDoctorId(Long doctor_id) {
        return appointmentRepository.findByDoctorId(doctor_id);
    }

    public List<Appointment> findByPatientId(Long patient_id) {
        return appointmentRepository.findByPatientId(patient_id);
    }

    public Query raw_query(String sqlQuery) {
        return entityManager.createNativeQuery(sqlQuery);
    }


    public void deleteAllByPatientId(Long id) {
        appointmentRepository.deleteInBatch(appointmentRepository.findByPatientId(id));
        appointmentRepository.flush();
    }

    public void deleteAllByDoctorId(Long id) {
        appointmentRepository.deleteInBatch(appointmentRepository.findByDoctorId(id));
        appointmentRepository.flush();
    }

}
