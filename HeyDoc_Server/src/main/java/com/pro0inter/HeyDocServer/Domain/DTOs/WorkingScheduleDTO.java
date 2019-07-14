package com.pro0inter.HeyDocServer.Domain.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
public class WorkingScheduleDTO {

    private Long id;

    @NotNull
    private Byte dayOfWeek;

    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss", timezone="CET")
    private Date startTime;

    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss", timezone="CET")
    private Date endTime;

    private boolean holiday=false;
}
