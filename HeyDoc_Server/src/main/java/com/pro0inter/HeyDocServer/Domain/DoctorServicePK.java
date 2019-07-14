package com.pro0inter.HeyDocServer.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorServicePK implements Serializable {
    private Long doctorId;
    private Long serviceId;


}
