package com.pro0inter.heydoc.api.DTOs;

import java.util.Date;

/**
 * Created by root on 4/29/19.
 */

public class PatientDTO {
    private Long account_id;
    private Long user_id;
    private String firstName;
    private String lastName;
    private Character gender;
    private Date dateOfBirth;

    private String blood_type;
    private String contact_phone_number;
    private String emergency_contact_phone_number;

    public PatientDTO() {
    }

    public PatientDTO(Long account_id, Long user_id, String blood_type, String contact_phone_number, String emergency_contact_phone_number) {
        this.account_id = account_id;
        this.user_id = user_id;
        this.blood_type = blood_type;
        this.contact_phone_number = contact_phone_number;
        this.emergency_contact_phone_number = emergency_contact_phone_number;
    }

    public PatientDTO(Long user_id, String blood_type, String contact_phone_number, String emergency_contact_phone_number) {
        this.user_id = user_id;
        this.blood_type = blood_type;
        this.contact_phone_number = contact_phone_number;
        this.emergency_contact_phone_number = emergency_contact_phone_number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientDTO that = (PatientDTO) o;

        if (account_id != null ? !account_id.equals(that.account_id) : that.account_id != null)
            return false;
        if (user_id != null ? !user_id.equals(that.user_id) : that.user_id != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
            return false;
        if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(that.dateOfBirth) : that.dateOfBirth != null)
            return false;
        if (blood_type != null ? !blood_type.equals(that.blood_type) : that.blood_type != null)
            return false;
        if (contact_phone_number != null ? !contact_phone_number.equals(that.contact_phone_number) : that.contact_phone_number != null)
            return false;
        return emergency_contact_phone_number != null ? emergency_contact_phone_number.equals(that.emergency_contact_phone_number) : that.emergency_contact_phone_number == null;
    }

    @Override
    public int hashCode() {
        int result = account_id != null ? account_id.hashCode() : 0;
        result = 31 * result + (user_id != null ? user_id.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (blood_type != null ? blood_type.hashCode() : 0);
        result = 31 * result + (contact_phone_number != null ? contact_phone_number.hashCode() : 0);
        result = 31 * result + (emergency_contact_phone_number != null ? emergency_contact_phone_number.hashCode() : 0);
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

    public String getBlood_type() {
        return blood_type;
    }

    public void setBlood_type(String blood_type) {
        this.blood_type = blood_type;
    }

    public String getContact_phone_number() {
        return contact_phone_number;
    }

    public void setContact_phone_number(String contact_phone_number) {
        this.contact_phone_number = contact_phone_number;
    }

    public String getEmergency_contact_phone_number() {
        return emergency_contact_phone_number;
    }

    public void setEmergency_contact_phone_number(String emergency_contact_phone_number) {
        this.emergency_contact_phone_number = emergency_contact_phone_number;
    }

    @Override
    public String toString() {
        return "PatientDTO{" +
                "account_id=" + account_id +
                ", user_id=" + user_id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", dateOfBirth=" + dateOfBirth +
                ", blood_type='" + blood_type + '\'' +
                ", contact_phone_number='" + contact_phone_number + '\'' +
                ", emergency_contact_phone_number='" + emergency_contact_phone_number + '\'' +
                '}';
    }
}
