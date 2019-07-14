package com.pro0inter.HeyDocServer.Domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "specialities")
@NoArgsConstructor
@Data
public class Speciality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String title;

    @Column(length = 500)
    private String description;


    @ManyToMany(mappedBy = "specialities", targetEntity = Doctor.class, fetch = FetchType.LAZY)
    private List<Doctor> doctors = new ArrayList<>();


}
