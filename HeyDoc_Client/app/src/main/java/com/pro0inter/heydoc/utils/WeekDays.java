package com.pro0inter.heydoc.utils;

import java.util.Calendar;


/**
 * Created by redayoub on 5/28/19.
 */

public enum WeekDays {
    /**
     * The singleton instance for the day-of-week of Sunday.
     * This has the numeric value of {@code 7}.
     */
    SUNDAY(Calendar.SUNDAY),
    /**
     * The singleton instance for the day-of-week of Monday.
     * This has the numeric value of {@code 1}.
     */
    MONDAY(Calendar.MONDAY),
    /**
     * The singleton instance for the day-of-week of Tuesday.
     * This has the numeric value of {@code 2}.
     */
    TUESDAY(Calendar.TUESDAY),
    /**
     * The singleton instance for the day-of-week of Wednesday.
     * This has the numeric value of {@code 3}.
     */
    WEDNESDAY(Calendar.WEDNESDAY),
    /**
     * The singleton instance for the day-of-week of Thursday.
     * This has the numeric value of {@code 4}.
     */
    THURSDAY(Calendar.THURSDAY),
    /**
     * The singleton instance for the day-of-week of Friday.
     * This has the numeric value of {@code 5}.
     */
    FRIDAY(Calendar.FRIDAY),
    /**
     * The singleton instance for the day-of-week of Saturday.
     * This has the numeric value of {@code 6}.
     */
    SATURDAY(Calendar.SATURDAY);

    private static final WeekDays[] ENUMS = WeekDays.values();
    private int value;

    WeekDays(int value) {
        this.value = value;
    }

    public static WeekDays of(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new RuntimeException("Invalid value for DayOfWeek: " + dayOfWeek);
        }
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return SUNDAY;
            case Calendar.MONDAY:
                return MONDAY;
            case Calendar.TUESDAY:
                return TUESDAY;
            case Calendar.WEDNESDAY:
                return WEDNESDAY;
            case Calendar.THURSDAY:
                return THURSDAY;
            case Calendar.FRIDAY:
                return FRIDAY;
            case Calendar.SATURDAY:
                return SATURDAY;

        }

        return null;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        switch (getValue()) {
            case Calendar.SUNDAY:
                return "SUNDAY";
            case Calendar.MONDAY:
                return "MONDAY";
            case Calendar.TUESDAY:
                return "TUESDAY";
            case Calendar.WEDNESDAY:
                return "WEDNESDAY";
            case Calendar.THURSDAY:
                return "THURSDAY";
            case Calendar.FRIDAY:
                return "FRIDAY";
            case Calendar.SATURDAY:
                return "SATURDAY";

        }

        return null;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
