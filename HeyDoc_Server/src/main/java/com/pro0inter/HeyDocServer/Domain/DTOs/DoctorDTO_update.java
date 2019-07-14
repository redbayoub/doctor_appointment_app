package com.pro0inter.HeyDocServer.Domain.DTOs;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class DoctorDTO_update {
    @NotNull
    private Long account_id;

    @Nullable
    private Double clinic_addrs_lat;

    @Nullable
    private Double clinic_addrs_lng;

    @Nullable
    private String picture;

    @Nullable
    private Boolean approved;

    @Nullable
    private String address;

    @Nullable
    private Integer zipCode;

    @Nullable
    private List<SpecialityDTO> specialities;

    @Nullable
    private List<WorkingScheduleDTO> workingSchedule;

    @Nullable
    private List<DoctorServiceDTO> doctorServices;


}
