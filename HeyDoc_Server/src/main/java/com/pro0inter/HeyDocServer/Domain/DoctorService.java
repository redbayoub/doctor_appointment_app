package com.pro0inter.HeyDocServer.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class DoctorService {
    @EmbeddedId
    private DoctorServicePK id = new DoctorServicePK();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("doctorId")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serviceId")
    private DocService service;

    @Column(nullable = false)
    private Byte estimatedDuration;

    /*  @Digits(integer = 3  total number of digits of precision
  precision
  , fraction = 2  number of digits after the decimal point
  scale
  )*/
    @Column(precision = 5, scale = 2)
    private BigDecimal fee;


}
