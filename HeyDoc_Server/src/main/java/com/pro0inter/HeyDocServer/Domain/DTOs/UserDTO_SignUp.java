package com.pro0inter.HeyDocServer.Domain.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserDTO_SignUp {
    private String firstName;
    private String lastName;
    private Character gender;
    private Date dateOfBirth;
}
