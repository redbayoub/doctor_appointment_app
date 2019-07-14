package com.pro0inter.HeyDocServer.Domain.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class SpecialityDTO {

    private Long id;
    @NotBlank
    private String title;
    private String description;

}
