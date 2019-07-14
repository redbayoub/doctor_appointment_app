package com.pro0inter.HeyDocServer.Domain;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String picture;

    @NotBlank
    private String address;
    @NotNull
    private Integer zipCode;
    @NotNull
    private Double clinic_addrs_lat;
    @NotNull
    private Double clinic_addrs_lng;

    @Column(name = "is_approved", nullable = false)
    @ColumnDefault("false")
    private boolean approved = false;


    //@JsonIgnore
    /*@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)*/
    @ManyToMany(targetEntity = Speciality.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "Doctor_Speciality",
            joinColumns = {@JoinColumn(name = "doctor_id")},
            inverseJoinColumns = {@JoinColumn(name = "speciality_id")}
    )
    @NotEmpty
    private List<Speciality> specialities = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "doctor")
    /*@JoinTable(
            name = "Doctor_WorkingSchedule",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "WorkingSchedule_id"))*/
    @NotEmpty
    private List<WorkingSchedule> workingSchedule = new ArrayList<>();






/*

    @JsonIgnore
    @OneToMany(mappedBy = "doctor",cascade = CascadeType.REMOVE)
    private List<Appointment> appointments=new ArrayList<>();

*/

}
