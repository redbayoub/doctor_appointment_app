package com.pro0inter.heydoc.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.pro0inter.heydoc.api.DTOs.PatientDTO;
import com.pro0inter.heydoc.api.DTOs.UserDTO_In;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.UserService;
import com.pro0inter.heydoc.domain.Doctor;
import com.pro0inter.heydoc.domain.Patient;
import com.pro0inter.heydoc.domain.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 4/30/19.
 */

public class UserInfoHolder {
    private static final String TAG = "UserInfoHolder";

    private static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(BIRTH_DATE_FORMAT);

    private static final String PREF_FILE = "user_info_pref";

    private static final String USER_ID_KEY = "user_id";
    private static final String USER_FIRST_NAME_KEY = "user_first_name";
    private static final String USER_LAST_NAME_KEY = "user_last_name_key";
    private static final String USER_BIRTH_DATE_KEY = "user_birth_date_key";
    private static final String USER_GENDER_KEY = "user_gender_key";

    private static final String PATIENT_ACCOUNT_ID_KEY = "patient_account_id_key";
    private static final String PATIENT_BLOOD_TYPE_KEY = "patient_blood_type_key";
    private static final String PATIENT_PH_NUMBER_KEY = "patient_ph_number_key";
    private static final String PATIENT_EMRG_PH_NUMBER_KEY = "patient_emrg_ph_number_key";
    private static UserInfoHolder instance;
    private FirebaseUser firebaseUser;
    /*private Context context;*/
    private User user;
    private Patient patient;
    private Doctor doctor;

    private UserInfoHolder(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
       /* this.context = context;*/
    }

    public static void init(FirebaseUser firebaseUser, Context context, Runnable onFinished) {
        instance = new UserInfoHolder(firebaseUser);

        instance.load_user_info(context, () -> { // after loading user info
            //finished
            if (onFinished != null)
                onFinished.run();

            instance.saveUserToPreferences(context);
            instance.load_patient_info(context, () -> {// after loading patient info
                instance.savePatientToPreferences(context);
            }, null);
        }, null);
    }


    public static UserInfoHolder getInstance(Context context) {
        if (instance == null) {
            User user = getUserFromPreferences(context);
            if (user != null) {
                instance = new UserInfoHolder(null);
                instance.user = user;
                Patient patient = getPatientFromPrefrences(context);
                if (patient != null) {
                    instance.patient = patient;
                } else {
                    instance.load_patient_info(context, () -> {// after loading patient info
                        instance.savePatientToPreferences(context);
                    }, null);
                }
            } else
                new RuntimeException("Instance not init");
        }

        return instance;
    }

    private static User getUserFromPreferences(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        if (!sharedPreferences.contains(USER_ID_KEY)) {
            return null;
        }
        User user = new User();
        user.setUser_id(sharedPreferences.getLong(USER_ID_KEY, 0L));
        user.setFirstName(sharedPreferences.getString(USER_FIRST_NAME_KEY, null));
        user.setLastName(sharedPreferences.getString(USER_LAST_NAME_KEY, null));
        try {
            user.setDateOfBirth(sdf.parse(sharedPreferences.getString(USER_BIRTH_DATE_KEY, null)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setGender(sharedPreferences.getString(USER_GENDER_KEY, null).charAt(0));


        return user;
    }

    private static Patient getPatientFromPrefrences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        if (!sharedPreferences.contains(PATIENT_ACCOUNT_ID_KEY)) {
            return null;
        }
        Patient patient = new Patient();
        patient.setPatient_id(sharedPreferences.getLong(PATIENT_ACCOUNT_ID_KEY, 0L));
        patient.setBlood_type(sharedPreferences.getString(PATIENT_BLOOD_TYPE_KEY, null));
        patient.setContact_phone_number(sharedPreferences.getString(PATIENT_PH_NUMBER_KEY, null));
        patient.setEmergency_contact_phone_number(sharedPreferences.getString(PATIENT_EMRG_PH_NUMBER_KEY, null));

        return patient;
    }

    public static void clearPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    private static User userDTO_in_TO_User(UserDTO_In userDTO_in) {
        if (userDTO_in == null) return null;
        User user = new User();
        user.setUser_id(userDTO_in.getUser_id());
        user.setFirstName(userDTO_in.getFirstName());
        user.setLastName(userDTO_in.getLastName());
        user.setDateOfBirth(userDTO_in.getDateOfBirth());
        user.setGender(userDTO_in.getGender());

        return user;
    }

    public Patient getPatient() {
        return patient;
    }

    public User getUser() {
        return instance.user;
    }

    private void saveUserToPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(USER_ID_KEY, user.getUser_id());
        editor.putString(USER_FIRST_NAME_KEY, user.getFirstName());
        editor.putString(USER_LAST_NAME_KEY, user.getLastName());
        editor.putString(USER_BIRTH_DATE_KEY, sdf.format(user.getDateOfBirth()));
        editor.putString(USER_GENDER_KEY, user.getGender().toString());

        editor.commit();


    }

    private void savePatientToPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(PATIENT_ACCOUNT_ID_KEY, patient.getPatient_id());
        editor.putString(PATIENT_BLOOD_TYPE_KEY, patient.getBlood_type());
        editor.putString(PATIENT_PH_NUMBER_KEY, patient.getContact_phone_number());
        editor.putString(PATIENT_EMRG_PH_NUMBER_KEY, patient.getEmergency_contact_phone_number());

        editor.commit();


    }

    public void updateUser(UserDTO_In new_info, Context context) {
        User newUser = userDTO_in_TO_User(new_info);
        instance.user = newUser;
        saveUserToPreferences(context);

    }

    public void updatePatient(PatientDTO new_info, Context context) {
        Patient newPatient = patientDTO_in_TO_Patient(new_info);
        instance.patient = newPatient;
        savePatientToPreferences(context);
    }

    public void load_user_info(Context context, Runnable onSuccess, Runnable onFailed) {
        UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
        Call<UserDTO_In> request = userService.getUserByIdOrUID(firebaseUser.getUid());
        request.enqueue(new Callback<UserDTO_In>() {
            @Override
            public void onResponse(Call<UserDTO_In> call, Response<UserDTO_In> response) {
                switch (response.code()) {
                    case 200: { // OK Successful

                        UserDTO_In userDTO_in = response.body();
                        instance.user = (userDTO_in_TO_User(userDTO_in));
                        Log.d(TAG, user.toString());
                        if (onSuccess != null)
                            onSuccess.run();

                        break;
                    }
                    default: {
                        RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                        // hendel errorResponse
                        Toast.makeText(context, errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, errorResponse.toString());
                        if (onFailed != null)
                            onFailed.run();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDTO_In> call, Throwable t) {
                Log.d(TAG, t.toString());
                if (onFailed != null)
                    onFailed.run();
            }
        });


    }

    public void load_patient_info(Context context, Runnable onSuccess, Runnable onFailed) {
        UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
        Call<PatientDTO> request = userService.getPatientByUserId(user.getUser_id());
        request.enqueue(new Callback<PatientDTO>() {
            @Override
            public void onResponse(Call<PatientDTO> call, Response<PatientDTO> response) {
                switch (response.code()) {
                    case 200: { // OK Successful

                        PatientDTO patientDTO = response.body();
                        instance.patient = patientDTO_in_TO_Patient(patientDTO);
                        Log.d(TAG, user.toString());
                        if (onSuccess != null)
                            onSuccess.run();

                        break;
                    }
                    default: {
                        RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                        // hendel errorResponse
                        Toast.makeText(context, errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, errorResponse.toString());
                        if (onFailed != null)
                            onFailed.run();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<PatientDTO> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
                onFailed.run();
            }
        });

    }

    private Patient patientDTO_in_TO_Patient(PatientDTO patientDTO) {
        if (patientDTO == null) return null;

        Patient patient = new Patient();
        patient.setPatient_id(patientDTO.getAccount_id());
        patient.setBlood_type(patientDTO.getBlood_type());
        patient.setContact_phone_number(patientDTO.getContact_phone_number());
        patient.setEmergency_contact_phone_number(patientDTO.getEmergency_contact_phone_number());
        return patient;
    }
}
