package com.pro0inter.heydoc.api.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by redayoub on 5/19/19.
 */

public class WorkingScheduleDTO {

    private Long id;

    private Byte dayOfWeek;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "CET")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "CET")
    private Date endTime;
    private boolean holiday = false;

    public WorkingScheduleDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkingScheduleDTO that = (WorkingScheduleDTO) o;

        if (holiday != that.holiday) return false;
        if (dayOfWeek != null ? !dayOfWeek.equals(that.dayOfWeek) : that.dayOfWeek != null)
            return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null)
            return false;
        return endTime != null ? endTime.equals(that.endTime) : that.endTime == null;
    }

    @Override
    public int hashCode() {
        int result = dayOfWeek != null ? dayOfWeek.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (holiday ? 1 : 0);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Byte dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    @Override
    public String toString() {
        return "WorkingScheduleDTO{" +
                "dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", holiday=" + holiday +
                '}';
    }
}
