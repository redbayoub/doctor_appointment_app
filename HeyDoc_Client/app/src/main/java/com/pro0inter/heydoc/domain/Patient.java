package com.pro0inter.heydoc.domain;

/**
 * Created by root on 5/2/19.
 */

public class Patient {
    private Long patient_id;
    private String blood_type;
    private String contact_phone_number;
    private String emergency_contact_phone_number;

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
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
}
