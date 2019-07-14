package com.pro0inter.heydoc.domain;

import com.pro0inter.heydoc.api.DTOs.SpecialityDTO;
import com.pro0inter.heydoc.api.DTOs.WorkingScheduleDTO;

import java.util.List;

/**
 * Created by redayoub on 6/2/19.
 */

public class Doctor {


    private Double clinic_addrs_lat;


    private Double clinic_addrs_lng;

    private String picture;

    private String address;

    private boolean approved;


    private Integer zipCode;

    private List<SpecialityDTO> specialities;

    private List<WorkingScheduleDTO> workingSchedule;

}
