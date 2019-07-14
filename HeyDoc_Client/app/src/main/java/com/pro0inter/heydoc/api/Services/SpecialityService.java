package com.pro0inter.heydoc.api.Services;

import com.pro0inter.heydoc.api.DTOs.SpecialityDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by redayoub on 5/14/19.
 */

public interface SpecialityService {
    @GET("/specialities/")
    Call<ArrayList<SpecialityDTO>> get_specialities();

    @GET("/specialities/{speciality_idOrTitle}")
    Call<SpecialityDTO> get_speciality_by_idOrTitle(
            @Path("speciality_idOrTitle") String speciality_idOrTitle);

    @POST("/specialities/")
    Call<SpecialityDTO> add_Speciality(@Body SpecialityDTO specialityDTO);

    @PUT("/specialities/{speciality_id}")
    Call<SpecialityDTO> update_Speciality(
            @Path("speciality_id") Long speciality_id,
            @Body SpecialityDTO specialityDTO);

    @DELETE("/specialities/{speciality_id}")
    Call<Void> delete_Speciality(
            @Path("speciality_id") Long speciality_id);

}
