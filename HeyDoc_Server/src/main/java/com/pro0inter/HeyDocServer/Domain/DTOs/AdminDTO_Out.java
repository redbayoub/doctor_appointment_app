package com.pro0inter.HeyDocServer.Domain.DTOs;

import com.google.firebase.database.annotations.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
public class AdminDTO_Out {
    private Long account_id;

    @NotNull
    private Long user_id;

    private boolean director = false;


    @Nullable
    private String firstName;
    @Nullable
    private String lastName;
    @Nullable
    private Character gender;
    @Nullable
    private Date dateOfBirth;
}
