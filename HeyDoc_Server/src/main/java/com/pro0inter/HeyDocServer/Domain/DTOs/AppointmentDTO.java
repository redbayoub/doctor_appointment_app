package com.pro0inter.HeyDocServer.Domain.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
public class AppointmentDTO {

    private Long id;

    private PatientDTO_Out patient;

    private DoctorDTO_Out doctor;


    private Date startTime;

    private Date endTime;


    private String patientProblem;

    private Byte fellowUpNumber = 0;


    private Boolean finished = false;

    private Boolean canceled = false;

    private Boolean rescheduled = false;

    private String cancelOrRescheduleReason;

}
