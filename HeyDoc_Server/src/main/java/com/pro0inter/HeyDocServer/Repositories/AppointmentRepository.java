package com.pro0inter.HeyDocServer.Repositories;

import com.pro0inter.HeyDocServer.Domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("select ap from Appointment ap where ap.patient.id=?1")
    List<Appointment> findByPatientId(Long patient_id);

    @Query("select ap from Appointment ap where ap.doctor.id=?1")
    List<Appointment> findByDoctorId(Long doctor_id);
}
