package com.pro0inter.HeyDocServer.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "appointments")
@Check(constraints = "end_to > start_from")
@NoArgsConstructor
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Doctor doctor;

    @Column(name = "start_from", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date startTime;

    @Column(name = "end_to", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date endTime;

    @Column(length = 500)
    private String patientProblem;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Byte fellowUpNumber = 0;


    @Column(name = "is_finished", nullable = false)
    @ColumnDefault("false")
    private boolean finished = false;

    @Column(name = "is_canceled", nullable = false)
    @ColumnDefault("false")
    private boolean canceled = false;

    @Column(name = "is_rescheduled", nullable = false)
    @ColumnDefault("false")
    private boolean rescheduled = false;

    private String cancelOrRescheduleReason;


}
