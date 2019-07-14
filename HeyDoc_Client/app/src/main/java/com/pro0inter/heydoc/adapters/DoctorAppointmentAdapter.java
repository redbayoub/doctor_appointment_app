package com.pro0inter.heydoc.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.api.DTOs.AppointmentDTO;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by redayoub on 6/8/19.
 */

public class DoctorAppointmentAdapter extends ArrayAdapter<AppointmentDTO> {

    private static final String DATE_TIME_PATTREN = "HH:mm  E dd/MM/yyyy";
    private LayoutInflater mLayoutInflater;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTREN);

    public DoctorAppointmentAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public View getView(int pos, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        DataHandler handler;
        if (convertView == null) {

            row = mLayoutInflater.inflate(R.layout.doctor_appointment_row_layout, parent, false);
            handler = new DataHandler();
            handler.patient_fullname = row.findViewById(R.id.DoctorAppointment_patient_fullname);
            handler.patient_age = row.findViewById(R.id.DoctorAppointment_patient_age);
            handler.patient_gender = row.findViewById(R.id.DoctorAppointment_patient_gender);
            handler.patient_problem = row.findViewById(R.id.DoctorAppointment_patient_prblem);

            handler.starts_at = row.findViewById(R.id.DoctorAppointment_starts_at);
            handler.ends_at = row.findViewById(R.id.DoctorAppointment_ends_at);

            handler.status = row.findViewById(R.id.DoctorAppointment_status);
            handler.cancelReasonLabel = row.findViewById(R.id.DoctorAppointment_cancelReason_at_label);
            handler.cancelReason = row.findViewById(R.id.DoctorAppointment_cancelReason);

            row.setTag(handler);
        } else {
            handler = (DataHandler) row.getTag();
        }
        AppointmentDTO dataProvider = getItem(pos);
        // empty handler
        handler.clear();

        handler.patient_fullname.setText(dataProvider.getPatient().getFirstName() + " " + dataProvider.getPatient().getLastName());
        handler.patient_problem.setText(dataProvider.getPatientProblem());

        setGender(handler.patient_gender, dataProvider.getPatient().getGender());


        Calendar calendar = Calendar.getInstance();
        int current_year = calendar.get(Calendar.YEAR);
        calendar.setTime(dataProvider.getPatient().getDateOfBirth());
        int birthDate_year = calendar.get(Calendar.YEAR);
        int age = current_year - birthDate_year;
        handler.patient_age.setText(age + " ans");

        handler.starts_at.setText(simpleDateFormat.format(dataProvider.getStartTime()));
        handler.ends_at.setText(simpleDateFormat.format(dataProvider.getEndTime()));

        setStatus(handler.status, handler.cancelReasonLabel, handler.cancelReason, dataProvider);

        return row;
    }

    private void setStatus(TextView status, TextView cancelReasonLabel, TextView cancelReason, AppointmentDTO dataProvider) {
        if (dataProvider.getCanceled()) {
            status.setText(R.string.canceled);
            status.setTextColor(Color.RED);
            if (dataProvider.getCancelOrRescheduleReason() != null &&
                    !dataProvider.getCancelOrRescheduleReason().trim().isEmpty()) {
                cancelReasonLabel.setVisibility(View.VISIBLE);
                cancelReason.setVisibility(View.VISIBLE);
                cancelReason.setText(dataProvider.getCancelOrRescheduleReason());
            } else { // canceled withoud reason
                cancelReasonLabel.setVisibility(View.GONE);
                cancelReason.setVisibility(View.GONE);
            }
        } else if (dataProvider.getFinished()) {
            status.setText(R.string.finished);
            status.setTextColor(Color.GREEN);
            cancelReasonLabel.setVisibility(View.GONE);
            cancelReason.setVisibility(View.GONE);
        } else { // Pending
            status.setText(R.string.pending);
            status.setTextColor(Color.BLACK);
            cancelReasonLabel.setVisibility(View.GONE);
            cancelReason.setVisibility(View.GONE);
        }
    }

    private void setGender(TextView textView, Character gender) {
        switch (gender) {
            case 'M': {
                textView.setText(getContext().getString(R.string.Male));
                textView.setTextColor(Color.BLUE);
                break;
            }
            case 'F': {
                textView.setText(getContext().getString(R.string.Female));
                textView.setTextColor(Color.parseColor("#FF00F1"));
                break;
            }
        }
    }

    static class DataHandler {
        TextView patient_fullname;
        TextView patient_age;
        TextView patient_gender;
        TextView patient_problem;

        TextView starts_at;
        TextView ends_at;
        TextView status;
        TextView cancelReasonLabel;
        TextView cancelReason;

        public void clear() {
            patient_fullname.setText("");
            patient_age.setText("");
            patient_gender.setText("");
            patient_problem.setText("");
            starts_at.setText("");
            ends_at.setText("");
            status.setText("");
            cancelReason.setText("");

        }
    }

}
