package com.pro0inter.heydoc.api.Services;

import com.pro0inter.heydoc.api.DTOs.AppointmentDTO;
import com.pro0inter.heydoc.api.DTOs.AppointmentDTO_ADD;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by redayoub on 5/14/19.
 */

public interface AppointmentService {

    @GET("/patients/{patient_id}/appointments")
    Call<List<AppointmentDTO>> get_appointments_by_patient_id(
            @Path("patient_id") Long patient_id);

    @GET("/doctors/{doctor_id}/appointments")
    Call<List<AppointmentDTO>> get_appointments_by_doctor_id(
            @Path("doctor_id") Long doctor_id);

    @GET("/appointments/{appointment_id}")
    Call<AppointmentDTO> get_appointment(
            @Path("appointment_id") Long appointment_id);

    @POST("/appointments/")
    Call<AppointmentDTO> book_appointment(
            @Body AppointmentDTO_ADD appointmentDTO_add_req);

    @PUT("/appointments/{appointment_id}")
    Call<AppointmentDTO> update_appointment(
            @Path("appointment_id") Long appointment_id,
            @Body AppointmentDTO new_info);
}
