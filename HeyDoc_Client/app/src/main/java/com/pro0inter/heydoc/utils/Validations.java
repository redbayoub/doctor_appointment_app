package com.pro0inter.heydoc.utils;

/**
 * Created by redayoub on 5/21/19.
 */

public class Validations {
    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    public static boolean isPhoneNumberValid(String contact_ph_number) {
        //TODO: Replace this with your own logic
        return true;
    }


    public static boolean isBloodTypeValid(String blood_type) {
        return (blood_type.length() == 3 || blood_type.length() == 2);
    }
}
