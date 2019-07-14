package com.pro0inter.HeyDocServer.Domain.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class AppointmentDTO_ADD {
    @NotNull
    private Long patient_id;
    @NotNull
    private Long doctor_id;
    @NotNull
    private Long working_schedule_id;
    @NotNull
    private Long doctor_service_id;
    @NotEmpty
    private String patientProblem;
    @NotNull
    private Byte fellowUpNumber = 0;
}
