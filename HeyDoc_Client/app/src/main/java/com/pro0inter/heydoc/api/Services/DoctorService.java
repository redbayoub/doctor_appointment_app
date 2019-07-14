package com.pro0inter.heydoc.api.Services;

import com.pro0inter.heydoc.api.DTOs.DoctorDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO_request;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO_update;
import com.pro0inter.heydoc.api.DTOs.DoctorServiceDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by redayoub on 5/26/19.
 */

public interface DoctorService {
    @GET("/doctors/")
    Call<List<DoctorDTO>> get_all();

    @GET("/doctors/{account_id}")
    Call<List<DoctorDTO>> getDoctorById(@Path("account_id") Long account_id);

    @GET("/doctors/{account_id}/services")
    Call<List<DoctorServiceDTO>> getDoctorServicesByDocId(@Path("account_id") Long account_id);

    @POST("/doctors/")
    Call<DoctorDTO> requestDoctorAccount(
            @Body DoctorDTO_request doctorDTO_request);


    @DELETE("/doctors/{account_id}")
    Call<Void> delDoctor(@Path("account_id") Long account_id);

    @PUT("/doctors/{account_id}")
    Call<DoctorDTO> updateDoctor(
            @Path("account_id") Long account_id,
            @Body DoctorDTO_update selectedDoctor);
}
