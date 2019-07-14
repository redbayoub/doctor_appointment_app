package com.pro0inter.HeyDocServer.Domain.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DoctorServiceDTO {

    @NotNull
    private Long doctor_id;

    @NotNull
    private DocServiceDTO service;

    @NotNull
    private Byte estimatedDuration;

    /*  @Digits(integer = 3
  precision
  , fraction = 2
  scale
  )*/
    @NotNull
    @Digits(integer = 3, fraction = 2)
    private BigDecimal fee;

}
