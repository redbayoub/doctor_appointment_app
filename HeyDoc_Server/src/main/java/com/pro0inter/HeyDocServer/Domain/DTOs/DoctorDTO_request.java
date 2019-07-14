package com.pro0inter.HeyDocServer.Domain.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class DoctorDTO_request {

    @NotNull
    private Long user_id;
    @NotNull
    private Double clinic_addrs_lat;
    @NotNull
    private Double clinic_addrs_lng;
    @Nullable
    private String picture;
    @NotEmpty
    private String address;
    @NotNull
    private Integer zipCode;

    @NotEmpty
    private List<SpecialityDTO> specialities;
    @NotEmpty
    private List<WorkingScheduleDTO> workingSchedule;
    @NotEmpty
    private List<DoctorServiceDTO> doctorServices;


}
