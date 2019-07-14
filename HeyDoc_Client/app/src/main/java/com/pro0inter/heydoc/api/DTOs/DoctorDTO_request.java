package com.pro0inter.heydoc.api.DTOs;

import java.util.List;

/**
 * Created by redayoub on 6/1/19.
 */

public class DoctorDTO_request {
    private Long user_id;

    private Double clinic_addrs_lat;

    private Double clinic_addrs_lng;

    private String picture;

    private String address;

    private Integer zipCode;

    private List<SpecialityDTO> specialities;

    private List<WorkingScheduleDTO> workingSchedule;

    private List<DoctorServiceDTO> doctorServices;


    public DoctorDTO_request() {
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Double getClinic_addrs_lat() {
        return clinic_addrs_lat;
    }

    public void setClinic_addrs_lat(Double clinic_addrs_lat) {
        this.clinic_addrs_lat = clinic_addrs_lat;
    }

    public Double getClinic_addrs_lng() {
        return clinic_addrs_lng;
    }

    public void setClinic_addrs_lng(Double clinic_addrs_lng) {
        this.clinic_addrs_lng = clinic_addrs_lng;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public List<SpecialityDTO> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(List<SpecialityDTO> specialities) {
        this.specialities = specialities;
    }

    public List<WorkingScheduleDTO> getWorkingSchedule() {
        return workingSchedule;
    }

    public void setWorkingSchedule(List<WorkingScheduleDTO> workingSchedule) {
        this.workingSchedule = workingSchedule;
    }

    public List<DoctorServiceDTO> getDoctorServices() {
        return doctorServices;
    }

    public void setDoctorServices(List<DoctorServiceDTO> doctorServices) {
        this.doctorServices = doctorServices;
    }
}
