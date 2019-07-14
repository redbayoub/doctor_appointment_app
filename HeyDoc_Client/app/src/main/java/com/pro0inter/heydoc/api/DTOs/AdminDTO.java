package com.pro0inter.heydoc.api.DTOs;

import java.util.Date;

/**
 * Created by redayoub on 5/19/19.
 */

public class AdminDTO {
    private Long account_id;
    private Long user_id;

    private boolean director = false;


    private String firstName;

    private String lastName;

    private Character gender;

    private Date dateOfBirth;


    public AdminDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdminDTO adminDTO = (AdminDTO) o;

        if (director != adminDTO.director) return false;
        if (account_id != null ? !account_id.equals(adminDTO.account_id) : adminDTO.account_id != null)
            return false;
        if (user_id != null ? !user_id.equals(adminDTO.user_id) : adminDTO.user_id != null)
            return false;
        if (firstName != null ? !firstName.equals(adminDTO.firstName) : adminDTO.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(adminDTO.lastName) : adminDTO.lastName != null)
            return false;
        if (gender != null ? !gender.equals(adminDTO.gender) : adminDTO.gender != null)
            return false;
        return dateOfBirth != null ? dateOfBirth.equals(adminDTO.dateOfBirth) : adminDTO.dateOfBirth == null;
    }

    @Override
    public int hashCode() {
        int result = account_id != null ? account_id.hashCode() : 0;
        result = 31 * result + (user_id != null ? user_id.hashCode() : 0);
        result = 31 * result + (director ? 1 : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AdminDTO{" +
                "account_id=" + account_id +
                ", user_id=" + user_id +
                ", director=" + director +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", dateOfBirth=" + dateOfBirth +
                '}';
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

    public boolean isDirector() {
        return director;
    }

    public void setDirector(boolean director) {
        this.director = director;
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
}
