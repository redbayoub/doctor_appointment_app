package com.pro0inter.heydoc.api.DTOs;

import java.math.BigDecimal;

/**
 * Created by redayoub on 5/28/19.
 */

public class DoctorServiceDTO {

    private Long doctor_id;
    private DocServiceDTO service;
    private Byte estimatedDuration;

    //@Digits(integer = 3, fraction = 2)
    private BigDecimal fee;

    public DoctorServiceDTO() {
    }

    @Override
    public String toString() {
        return "DoctorServiceDTO{" +
                "doctor_id=" + doctor_id +
                ", service=" + service +
                ", estimatedDuration=" + estimatedDuration +
                ", fee=" + fee +
                '}';
    }

    public Long getDoctor_id() {

        return doctor_id;
    }

    public void setDoctor_id(Long doctor_id) {
        this.doctor_id = doctor_id;
    }

    public DocServiceDTO getService() {
        return service;
    }

    public void setService(DocServiceDTO service) {
        this.service = service;
    }

    public Byte getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Byte estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoctorServiceDTO dto = (DoctorServiceDTO) o;

        if (doctor_id != null ? !doctor_id.equals(dto.doctor_id) : dto.doctor_id != null)
            return false;
        if (service != null ? !service.equals(dto.service) : dto.service != null) return false;
        if (estimatedDuration != null ? !estimatedDuration.equals(dto.estimatedDuration) : dto.estimatedDuration != null)
            return false;
        return fee != null ? fee.equals(dto.fee) : dto.fee == null;
    }

    @Override
    public int hashCode() {
        int result = doctor_id != null ? doctor_id.hashCode() : 0;
        result = 31 * result + (service != null ? service.hashCode() : 0);
        result = 31 * result + (estimatedDuration != null ? estimatedDuration.hashCode() : 0);
        result = 31 * result + (fee != null ? fee.hashCode() : 0);
        return result;
    }
}
