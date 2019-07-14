package com.pro0inter.heydoc.api.Services;

import com.pro0inter.heydoc.api.DTOs.AdminDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by redayoub on 5/19/19.
 */

public interface AdminService {
    @POST("/admins/")
    Call<AdminDTO> add_admin(@Body AdminDTO adminDTO);

    @PUT("/admins/{account_id}")
    Call<AdminDTO> update_admin(@Path("account_id") Long account_id,
                                @Body AdminDTO adminDTO);

    @GET("/admins/")
    Call<List<AdminDTO>> get_all_admins();

    @GET("/admins/{account_id}")
    Call<AdminDTO> get_by_account_id(
            @Path("account_id") Long account_id);

    @DELETE("/admins/{id}")
    Call<Void> del_admin(@Path("id") long id);
}
