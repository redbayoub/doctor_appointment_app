package com.pro0inter.HeyDocServer.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "WorkingSchedule")
@Check(constraints = "end_time > start_time")
@Data
@NoArgsConstructor
public class WorkingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Doctor doctor;

    @Column(nullable = false)
    private Byte dayOfWeek;

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date endTime;


    @Column(name = "is_holiday", nullable = false)
    @ColumnDefault("false")
    private boolean holiday = false;


}
