package com.pro0inter.heydoc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.api.DTOs.WorkingScheduleDTO;
import com.pro0inter.heydoc.utils.WeekDays;

import java.text.SimpleDateFormat;

/**
 * Created by redayoub on 5/27/19.
 */

public class WorkingScheduleAdapter extends ArrayAdapter<WorkingScheduleDTO> {
    private final static String TIME_PATTREN = "HH:mm";
    private final static SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTREN);

    private LayoutInflater mLayoutInflater;

    public WorkingScheduleAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        DataHandler handler;
        if (convertView == null) {

            row = mLayoutInflater.inflate(R.layout.working_schedule_row_layout, parent, false);
            handler = new DataHandler();
            handler.day = row.findViewById(R.id.workingSchedule_day);
            handler.fromTime = row.findViewById(R.id.workingSchedule_fromTime);
            handler.toTime = row.findViewById(R.id.workingSchedule_toTime);
            handler.isHoliday = row.findViewById(R.id.workingSchedule_isHoliday);
            row.setTag(handler);
        } else {
            handler = (DataHandler) row.getTag();
        }
        WorkingScheduleDTO dataProvider = getItem(pos);
        // empty handler
        handler.clear();

        handler.day.setText(WeekDays.of(dataProvider.getDayOfWeek()).getDisplayName());
        handler.fromTime.setText(sdf.format(dataProvider.getStartTime()));
        handler.toTime.setText(sdf.format(dataProvider.getEndTime()));
        if (dataProvider.isHoliday()) {

            handler.isHoliday.setText(getContext().getString(R.string.not_working));
            handler.isHoliday.setTextColor(getContext().getResources().getColor(R.color.color_red));

        } else {

            handler.isHoliday.setText(getContext().getString(R.string.working));
            handler.isHoliday.setTextColor(getContext().getResources().getColor(R.color.color_green));
        }


        return row;
    }

    static class DataHandler {
        TextView day;
        TextView fromTime;
        TextView toTime;
        TextView isHoliday;

        public void clear() {
            day.setText("");
            fromTime.setText("");
            toTime.setText("");
            isHoliday.setText("");
        }
    }
}

