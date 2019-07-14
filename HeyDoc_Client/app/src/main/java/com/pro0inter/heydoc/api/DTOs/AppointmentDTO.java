package com.pro0inter.heydoc.api.DTOs;

import java.util.Date;

/**
 * Created by redayoub on 5/14/19.
 */

public class AppointmentDTO {

    private Long id;

    private PatientDTO patient;

    private DoctorDTO doctor;


    private Date startTime;

    private Date endTime;


    private String patientProblem;

    private Byte fellowUpNumber = 0;


    private Boolean finished = false;

    private Boolean canceled = false;

    private Boolean rescheduled = false;

    private String cancelOrRescheduleReason;


    public AppointmentDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppointmentDTO that = (AppointmentDTO) o;

        if (finished != that.finished) return false;
        if (canceled != that.canceled) return false;
        if (rescheduled != that.rescheduled) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (patient != null ? !patient.equals(that.patient) : that.patient != null) return false;
        if (doctor != null ? !doctor.equals(that.doctor) : that.doctor != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null)
            return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (patientProblem != null ? !patientProblem.equals(that.patientProblem) : that.patientProblem != null)
            return false;
        if (fellowUpNumber != null ? !fellowUpNumber.equals(that.fellowUpNumber) : that.fellowUpNumber != null)
            return false;
        return cancelOrRescheduleReason != null ? cancelOrRescheduleReason.equals(that.cancelOrRescheduleReason) : that.cancelOrRescheduleReason == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (patient != null ? patient.hashCode() : 0);
        result = 31 * result + (doctor != null ? doctor.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (patientProblem != null ? patientProblem.hashCode() : 0);
        result = 31 * result + (fellowUpNumber != null ? fellowUpNumber.hashCode() : 0);
        result = 31 * result + (finished ? 1 : 0);
        result = 31 * result + (canceled ? 1 : 0);
        result = 31 * result + (rescheduled ? 1 : 0);
        result = 31 * result + (cancelOrRescheduleReason != null ? cancelOrRescheduleReason.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AppointmentDTO{" +
                "id=" + id +
                ", patient=" + patient +
                ", doctor=" + doctor +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", patientProblem='" + patientProblem + '\'' +
                ", fellowUpNumber=" + fellowUpNumber +
                ", finished=" + finished +
                ", canceled=" + canceled +
                ", rescheduled=" + rescheduled +
                ", cancelOrRescheduleReason='" + cancelOrRescheduleReason + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Boolean getRescheduled() {
        return rescheduled;
    }

    public void setRescheduled(Boolean rescheduled) {
        this.rescheduled = rescheduled;
    }

    public String getCancelOrRescheduleReason() {
        return cancelOrRescheduleReason;
    }

    public void setCancelOrRescheduleReason(String cancelOrRescheduleReason) {
        this.cancelOrRescheduleReason = cancelOrRescheduleReason;
    }
}
