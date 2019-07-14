package com.pro0inter.HeyDocServer.Domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "patients")
@NoArgsConstructor
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    @NotBlank
    @Column(nullable = false, length = 3)
    private String blood_type;

    @Column(length = 15)
    private String contact_phone_number;

    @Column(length = 15)
    private String emergency_contact_phone_number;



   /* @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "patient",cascade = CascadeType.REMOVE)
    private List<Appointment> appointments=new ArrayList<>();*/
}
