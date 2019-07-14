package com.pro0inter.heydoc.api.Services;

import com.pro0inter.heydoc.api.DTOs.DocServiceDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by redayoub on 5/28/19.
 */

public interface DocService_Service {

    @GET("/doc_services/")
    Call<ArrayList<DocServiceDTO>> get_docservices();

    @GET("/doc_services/{doc_service_idOrTitle}")
    Call<DocServiceDTO> get_doc_service_by_idOrTitle(
            @Path("doc_service_idOrTitle") String doc_service_idOrTitle);

    @POST("/doc_services/")
    Call<DocServiceDTO> add_Doc_service(@Body DocServiceDTO doc_serviceDTO);

    @PUT("/doc_services/{doc_service_id}")
    Call<DocServiceDTO> update_Doc_service(
            @Path("doc_service_id") Long doc_service_id,
            @Body DocServiceDTO doc_serviceDTO);

    @DELETE("/doc_services/{doc_service_id}")
    Call<Void> delete_Doc_service(
            @Path("doc_service_id") Long doc_service_id);

}
