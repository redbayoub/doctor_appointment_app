package com.pro0inter.HeyDocServer.Domain.DTOs;

import com.google.firebase.database.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO_Out {
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

    @NotNull
    @NotBlank
    private String blood_type;
    @NotNull
    @NotBlank
    private String contact_phone_number;
    @NotNull
    @NotBlank
    private String emergency_contact_phone_number;
}
