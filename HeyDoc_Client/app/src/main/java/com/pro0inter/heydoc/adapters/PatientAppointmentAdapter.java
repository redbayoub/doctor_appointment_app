package com.pro0inter.heydoc.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.api.DTOs.AppointmentDTO;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class PatientAppointmentAdapter extends ArrayAdapter<AppointmentDTO> {


    private static final String DATE_TIME_PATTREN = "HH:mm  E dd/MM/yyyy";
    private LayoutInflater mLayoutInflater;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTREN);

    public PatientAppointmentAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public View getView(int pos, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        DataHandler handler;
        if (convertView == null) {

            row = mLayoutInflater.inflate(R.layout.patient_appointment_row_layout, parent, false);
            handler = new DataHandler();
            handler.doctor_fullname = row.findViewById(R.id.PatientAppointment_doctor_fullname);
            handler.doctor_picture = row.findViewById(R.id.PatientAppointment_doctor_picture);
            handler.clinic_addrs = row.findViewById(R.id.PatientAppointment_clinic_address);
            handler.starts_at = row.findViewById(R.id.PatientAppointment_starts_at);
            handler.ends_at = row.findViewById(R.id.PatientAppointment_ends_at);


            handler.status = row.findViewById(R.id.PatientAppointment_status);
            handler.cancelReasonLabel = row.findViewById(R.id.PatientAppointment_cancelReason_at_label);
            handler.cancelReason = row.findViewById(R.id.PatientAppointment_cancelReason);


            row.setTag(handler);
        } else {
            handler = (DataHandler) row.getTag();
        }
        AppointmentDTO dataProvider = getItem(pos);
        // empty handler
        handler.clear();

        if(!TextUtils.isEmpty(dataProvider.getDoctor().getPicture())){
            Picasso.get()
                    .load(Uri.parse(ServiceGenerator.getFileUrl(dataProvider.getDoctor().getPicture())))
                    .placeholder(R.drawable.ic_doctor_avatar_placeholder_100dp)
                    .error(R.drawable.ic_error_black_100dp)
                    .into(handler.doctor_picture);
        }

        handler.doctor_fullname.setText(dataProvider.getDoctor().getFirstName() + " " + dataProvider.getDoctor().getLastName());
        handler.clinic_addrs.setText(dataProvider.getDoctor().getAddress());
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
            } else { // canceled without reason
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

    static class DataHandler {
        TextView doctor_fullname;
        ImageView doctor_picture;
        TextView clinic_addrs;
        TextView starts_at;
        TextView ends_at;
        TextView status;
        TextView cancelReasonLabel;
        TextView cancelReason;



        public void clear() {
            doctor_fullname.setText("");
            clinic_addrs.setText("");
            starts_at.setText("");
            ends_at.setText("");
            status.setText("");
            cancelReason.setText("");
        }
    }

}
