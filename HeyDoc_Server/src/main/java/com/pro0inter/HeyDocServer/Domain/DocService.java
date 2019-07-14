package com.pro0inter.HeyDocServer.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "services")
@NoArgsConstructor
@Data
public class DocService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotEmpty
    private String title;


    @Column(length = 500)
    private String description;


}
