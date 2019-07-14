package com.pro0inter.heydoc.api.Services;

import com.pro0inter.heydoc.api.DTOs.PatientDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by root on 4/27/19.
 */

public interface PatientService {
    @POST("/patients/")
    Call<PatientDTO> add_patient(@Body PatientDTO patientDTO);

    @GET("/patients/")
    Call<List<PatientDTO>> get_all_patients();

    @GET("/patients/{account_id}")
    Call<PatientDTO> get_by_account_id(
            @Path("account_id") Long account_id);

    @DELETE("/patients/{account_id}")
    Call<Void> delPatient(@Path("account_id") long id);

    @PUT("/patients/{account_id}")
    Call<PatientDTO> update_patient(@Path("account_id") Long patient_id,
                                    @Body PatientDTO newPatientInfo);

    /*
    @POST("/users/")
    Call<UserDTO_In> signupUser(@Body UserDTO_SignUp userDTO_signUp);

    @GET("/users/{id}")
    Call<UserDTO_In> getUser(@Path("id") long id);

    @GET("/users/{uid}")
    Call<UserDTO_In> getUserByUID(@Path("uid") String uid);

    @PUT("/users/{id}")
    Call<UserDTO_In> updateUser(@Path("id") long id,
                                @Body UserDTO_In userDTO_in);


    @DELETE("/users/{id}")
    Call<UserDTO_In> delUser(@Path("id") long id);*/


}
