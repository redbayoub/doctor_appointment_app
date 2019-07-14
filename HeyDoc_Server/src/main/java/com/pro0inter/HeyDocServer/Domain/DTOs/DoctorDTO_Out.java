package com.pro0inter.HeyDocServer.Domain.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class DoctorDTO_Out {
    @Nullable
    private Long account_id;

    @NotNull
    private Long user_id;

    @Nullable
    private String firstName;
    @Nullable
    private String lastName;
    @Nullable
    private Character gender;
    @Nullable
    private Date dateOfBirth;


    @Nullable
    private Double clinic_addrs_lat;

    @Nullable
    private Double clinic_addrs_lng;

    @Nullable
    private String picture;

    @Nullable
    private boolean approved = false;

    @NotNull
    @NotEmpty
    private String address;

    @NotNull
    private Integer zipCode;

    @NotEmpty
    private List<SpecialityDTO> specialities;

    @NotEmpty
    private List<WorkingScheduleDTO> workingSchedule;


}
