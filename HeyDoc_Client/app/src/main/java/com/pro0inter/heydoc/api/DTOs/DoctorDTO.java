package com.pro0inter.heydoc.api.DTOs;

import java.util.Date;
import java.util.List;

/**
 * Created by redayoub on 5/19/19.
 */

public class DoctorDTO {
    private Long account_id;

    private Long user_id;


    private String firstName;

    private String lastName;


    private Character gender;


    private Date dateOfBirth;

    private Double clinic_addrs_lat;


    private Double clinic_addrs_lng;

    private String picture;

    private String address;

    private boolean approved;


    private Integer zipCode;

    private List<SpecialityDTO> specialities;

    private List<WorkingScheduleDTO> workingSchedule;

    public DoctorDTO() {
    }

    public DoctorDTO(Long user_id, String address, Integer zipCode, List<SpecialityDTO> specialities, List<WorkingScheduleDTO> workingSchedule) {
        this.user_id = user_id;
        this.address = address;
        this.zipCode = zipCode;
        this.specialities = specialities;
        this.workingSchedule = workingSchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoctorDTO dto = (DoctorDTO) o;

        if (approved != dto.approved) return false;
        if (account_id != null ? !account_id.equals(dto.account_id) : dto.account_id != null)
            return false;
        if (user_id != null ? !user_id.equals(dto.user_id) : dto.user_id != null) return false;
        if (firstName != null ? !firstName.equals(dto.firstName) : dto.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(dto.lastName) : dto.lastName != null) return false;
        if (gender != null ? !gender.equals(dto.gender) : dto.gender != null) return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(dto.dateOfBirth) : dto.dateOfBirth != null)
            return false;
        if (clinic_addrs_lat != null ? !clinic_addrs_lat.equals(dto.clinic_addrs_lat) : dto.clinic_addrs_lat != null)
            return false;
        if (clinic_addrs_lng != null ? !clinic_addrs_lng.equals(dto.clinic_addrs_lng) : dto.clinic_addrs_lng != null)
            return false;
        if (picture != null ? !picture.equals(dto.picture) : dto.picture != null) return false;
        if (address != null ? !address.equals(dto.address) : dto.address != null) return false;
        if (zipCode != null ? !zipCode.equals(dto.zipCode) : dto.zipCode != null) return false;
        if (specialities != null ? !specialities.equals(dto.specialities) : dto.specialities != null)
            return false;
        return workingSchedule != null ? workingSchedule.equals(dto.workingSchedule) : dto.workingSchedule == null;
    }

    @Override
    public int hashCode() {
        int result = account_id != null ? account_id.hashCode() : 0;
        result = 31 * result + (user_id != null ? user_id.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (clinic_addrs_lat != null ? clinic_addrs_lat.hashCode() : 0);
        result = 31 * result + (clinic_addrs_lng != null ? clinic_addrs_lng.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (approved ? 1 : 0);
        result = 31 * result + (zipCode != null ? zipCode.hashCode() : 0);
        result = 31 * result + (specialities != null ? specialities.hashCode() : 0);
        result = 31 * result + (workingSchedule != null ? workingSchedule.hashCode() : 0);
        return result;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
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


    @Override
    public String toString() {
        return "DoctorDTO{" +
                "account_id=" + account_id +
                ", user_id=" + user_id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", dateOfBirth=" + dateOfBirth +
                ", clinic_addrs_lat=" + clinic_addrs_lat +
                ", clinic_addrs_lng=" + clinic_addrs_lng +
                ", picture='" + picture + '\'' +
                ", address='" + address + '\'' +
                ", approved=" + approved +
                ", zipCode=" + zipCode +
                ", specialities=" + specialities +
                ", workingSchedule=" + workingSchedule +
                '}';
    }
}
