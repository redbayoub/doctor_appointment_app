package com.pro0inter.HeyDocServer.Domain.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class DocServiceDTO {

    @Nullable
    private Long id;

    @NotEmpty
    private String title;

    @Nullable
    private String description;
}
