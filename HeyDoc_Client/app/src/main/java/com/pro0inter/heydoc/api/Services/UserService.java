package com.pro0inter.heydoc.api.Services;

import com.pro0inter.heydoc.api.DTOs.AdminDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO;
import com.pro0inter.heydoc.api.DTOs.PatientDTO;
import com.pro0inter.heydoc.api.DTOs.UserDTO_In;
import com.pro0inter.heydoc.api.DTOs.UserDTO_SignUp;

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

public interface UserService {

    @POST("/users/")
    Call<UserDTO_In> signupUser(@Body UserDTO_SignUp userDTO_signUp);


    @GET("/users/{idORuid}")
    Call<UserDTO_In> getUserByIdOrUID(@Path("idORuid") String uid);

    @GET("/users/{user_id}/patient")
    Call<PatientDTO> getPatientByUserId(@Path("user_id") Long user_id);

    @GET("/users/{user_id}/doctor")
    Call<DoctorDTO> getDoctorByUserId(@Path("user_id") Long user_id);

    @GET("/users/")
    Call<List<UserDTO_In>> get_users();

    @PUT("/users/{id}")
    Call<UserDTO_In> updateUser(@Path("id") long id,
                                @Body UserDTO_In userDTO_in);


    @DELETE("/users/{id}")
    Call<Void> delUser(@Path("id") long id);

    @GET("/users/{user_id}/admin")
    Call<AdminDTO> getAdminByUserId(@Path("user_id") Long user_id);
}
