package com.pro0inter.heydoc.api.DTOs;

/**
 * Created by redayoub on 6/3/19.
 */

public class AppointmentDTO_ADD {
    private Long patient_id;

    private Long doctor_id;

    private Long working_schedule_id;

    private Long doctor_service_id;

    private String patientProblem;

    private Byte fellowUpNumber = 0;

    public AppointmentDTO_ADD() {
    }

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public Long getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(Long doctor_id) {
        this.doctor_id = doctor_id;
    }

    public Long getWorking_schedule_id() {
        return working_schedule_id;
    }

    public void setWorking_schedule_id(Long working_schedule_id) {
        this.working_schedule_id = working_schedule_id;
    }

    public Long getDoctor_service_id() {
        return doctor_service_id;
    }

    public void setDoctor_service_id(Long doctor_service_id) {
        this.doctor_service_id = doctor_service_id;
    }

    public String getPatientProblem() {
        return patientProblem;
    }

    public void setPatientProblem(String patientProblem) {
        this.patientProblem = patientProblem;
    }

    public Byte getFellowUpNumber() {
        return fellowUpNumber;
    }

    public void setFellowUpNumber(Byte fellowUpNumber) {
        this.fellowUpNumber = fellowUpNumber;
    }
}
