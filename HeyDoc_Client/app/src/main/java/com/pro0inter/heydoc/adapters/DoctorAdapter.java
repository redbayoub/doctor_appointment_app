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
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO;
import com.pro0inter.heydoc.api.DTOs.SpecialityDTO;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by redayoub on 5/26/19.
 */

public class DoctorAdapter extends ArrayAdapter<DoctorDTO> {
    private LayoutInflater mLayoutInflater;
    private Set<DoctorDTO> notFiltredSet;

    public DoctorAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        DataHandler handler;
        if (convertView == null) {

            row = mLayoutInflater.inflate(R.layout.doctor_row_layout, parent, false);
            handler = new DataHandler();
            handler.fullName = row.findViewById(R.id.doctor_row_fullName);
            handler.age = row.findViewById(R.id.doctor_row_age);
            handler.specialities = row.findViewById(R.id.doctor_row_specialities);
            handler.gender = row.findViewById(R.id.doctor_row_gender);
            handler.picture = row.findViewById(R.id.doctor_row_image);


            row.setTag(handler);
        } else {
            handler = (DataHandler) row.getTag();
        }

        DoctorDTO dataProvider = getItem(pos);
        // empty handler
        handler.clear();

        handler.fullName.setText(dataProvider.getFirstName() + " " + dataProvider.getLastName());

        Calendar calendar = Calendar.getInstance();
        int current_year = calendar.get(Calendar.YEAR);
        calendar.setTime(dataProvider.getDateOfBirth());
        int birthDate_year = calendar.get(Calendar.YEAR);
        int age = current_year - birthDate_year;
        handler.age.setText(age + " ans");

        StringBuilder specialties = new StringBuilder();
        for (SpecialityDTO specialityDTO : dataProvider.getSpecialities()) {
            specialties.append(specialityDTO.getTitle() + " , ");
        }
        handler.specialities.setText(specialties.toString());

        setGender(handler.gender, dataProvider.getGender());

        if(!TextUtils.isEmpty(dataProvider.getPicture())){
            Picasso.get()
                    .load(Uri.parse(ServiceGenerator.getFileUrl(dataProvider.getPicture())))
                    .placeholder(R.drawable.ic_doctor_avatar_placeholder_100dp)
                    .error(R.drawable.ic_error_black_100dp)
                    .into(handler.picture);
        }

        return row;
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

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                initNotFilterdSet();
                FilterResults results = new FilterResults();
                ArrayList<DoctorDTO> filtredResult = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {

                    results.values = notFiltredSet;
                    results.count = notFiltredSet.size();
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < getCount(); i++) {
                        DoctorDTO dto = getItem(i);
                        if (dto.getFirstName() != null && dto.getFirstName().toLowerCase().startsWith(constraint.toString()))
                            filtredResult.add(dto);
                        if (dto.getLastName() != null && dto.getLastName().toLowerCase().startsWith(constraint.toString()))
                            filtredResult.add(dto);

                    }

                    results.count = filtredResult.size();
                    results.values = filtredResult;

                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll((Collection<? extends DoctorDTO>) results.values);

                notifyDataSetChanged();
            }
        };
    }

    private void initNotFilterdSet() {
        if (notFiltredSet == null)
            notFiltredSet = new HashSet<>();
        for (int i = 0; i < getCount(); i++) {
            DoctorDTO dto = getItem(i);
            notFiltredSet.add(dto);
        }
    }

    static class DataHandler {
        TextView fullName;
        TextView age;
        TextView specialities;
        TextView gender;
        ImageView picture;


        public void clear() {
            fullName.setText("");
            age.setText("");
            specialities.setText("");
            gender.setText("");
        }
    }
}
