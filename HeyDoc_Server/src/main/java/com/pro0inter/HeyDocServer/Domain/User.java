package com.pro0inter.HeyDocServer.Domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank
    @Column(unique = true, nullable = false)
    private String uid;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

   /* @NotBlank
    @Column(unique = true,nullable = false)
    private String email;

    @Column(name = "is_email_verified",nullable = false)
    @ColumnDefault("false")
    private boolean emailVerified=false;*/

    @NotNull
    private Character gender;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;


/*


    //@JsonIgnoreCasca
    @OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.REMOVE},orphanRemoval = true,mappedBy = "user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)

    @NotFound(action = NotFoundAction.IGNORE)
    private Patient patient;

    //@JsonIgnore
 @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)

    @OneToOne(fetch = FetchType.LAZY,cascade ={CascadeType.REMOVE},orphanRemoval = true,mappedBy = "user")
    @NotFound(action = NotFoundAction.IGNORE)
    private Doctor doctor;

    //@JsonIgnore
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)

    @OneToOne(fetch = FetchType.LAZY,cascade ={CascadeType.REMOVE},orphanRemoval = true,mappedBy = "user")
    @NotFound(action = NotFoundAction.IGNORE)
    private Admin admin;

*/


}
