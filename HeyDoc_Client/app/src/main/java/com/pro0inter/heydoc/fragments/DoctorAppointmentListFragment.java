package com.pro0inter.heydoc.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.adapters.DoctorAppointmentAdapter;
import com.pro0inter.heydoc.api.DTOs.AppointmentDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO;
import com.pro0inter.heydoc.api.DTOs.PatientDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.AppointmentService;
import com.pro0inter.heydoc.api.Services.UserService;
import com.pro0inter.heydoc.utils.UserInfoHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorAppointmentListFragment extends Fragment {


    private static final String TAG = "DoctorAppointListFrag";
    private static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BIRTH_DATE_FORMAT);
    ListView mListView;
    ProgressBar mProgressBar;
    DoctorAppointmentAdapter mDoctorAppointmentAdapter;
    DoctorDTO curr_doctor;


    public DoctorAppointmentListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_doctor_appointment_list, container, false);


        mProgressBar = layout.findViewById(R.id.loading_doctor_appointment_list_progress);
        mListView = layout.findViewById(R.id.doctor_appointment_listview);


        mDoctorAppointmentAdapter = new DoctorAppointmentAdapter(getActivity(), R.layout.doctor_appointment_row_layout);
        mListView.setAdapter(mDoctorAppointmentAdapter);
        // view -> add fellowUp appointment; reschedule;cancel with reason;
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AppointmentDTO selected_appointment = mDoctorAppointmentAdapter.getItem(position);
                // show context menu
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.appointment_manage_menu);

                popupMenu.getMenu().findItem(R.id.appointment_get_dir).setVisible(false);
                popupMenu.getMenu().findItem(R.id.appointment_showDoctorDetails).setVisible(false);
                if (!selected_appointment.getEndTime().before(new Date())) { // not already passed
                    popupMenu.getMenu().findItem(R.id.appointment_setAsFinished).setVisible(false);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.appointment_showPatientDetails: {
                                showPatientDetails(position);
                                return true;
                            }
                            case R.id.appointment_setAsFinished: {
                                setAppointmentAsFinished(position);
                                return true;
                            }
                            case R.id.appointment_cancel: {
                                showCancelDialog(position);
                                return true;
                            }

                        }

                        return false;
                    }
                });
                popupMenu.show();
                return true;
            }
        });


        if (curr_doctor == null)
            load_doctor(UserInfoHolder.getInstance(getContext()).getUser().getUser_id());
        else
            load_appointments();

        setHasOptionsMenu(true);
        return layout;
    }

    private void setAppointmentAsFinished(int position) { // already cheacked
        AppointmentDTO selected_appointment = mDoctorAppointmentAdapter.getItem(position);
        if (selected_appointment.getEndTime().before(new Date())) { // already passed
            selected_appointment.setFinished(true);
            AppointmentService appointmentService = ServiceGenerator.createServiceSecured(AppointmentService.class);
            Call<AppointmentDTO> request = appointmentService.update_appointment(selected_appointment.getId(), selected_appointment);
            request.enqueue(new Callback<AppointmentDTO>() {
                @Override
                public void onResponse(Call<AppointmentDTO> call, Response<AppointmentDTO> response) {
                    switch (response.code()) {
                        case 200: { // OK
                            AppointmentDTO res = response.body();
                            mDoctorAppointmentAdapter.remove(mDoctorAppointmentAdapter.getItem(position));
                            mDoctorAppointmentAdapter.insert(res, position);
                            mDoctorAppointmentAdapter.notifyDataSetChanged();
                            break;
                        }
                        default: {
                            RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                            // hendel errorResponse
                            Toast.makeText(getContext(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorResponse.toString());
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<AppointmentDTO> call, Throwable t) {
                    Log.d(TAG, t.toString());
                }
            });

        }

    }

    private void showPatientDetails(int position) {
        PatientDTO selectedPatient = mDoctorAppointmentAdapter.getItem(position).getPatient();

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.patient_view_dialog, null);

        ((TextView) view.findViewById(R.id.patient_firstName_tv)).setText(selectedPatient.getFirstName());
        ((TextView) view.findViewById(R.id.patient_lastName_tv)).setText(selectedPatient.getLastName());
        ((TextView) view.findViewById(R.id.patient_birthDate_tv)).setText(simpleDateFormat.format(selectedPatient.getDateOfBirth()));
        ((TextView) view.findViewById(R.id.patient_bloodType_tv)).setText(selectedPatient.getBlood_type());
        ((TextView) view.findViewById(R.id.patient_phoneNumber_tv)).setText(selectedPatient.getContact_phone_number());
        ((TextView) view.findViewById(R.id.patient_emengencyPhoneNumber_tv)).setText(selectedPatient.getEmergency_contact_phone_number());
        setGender(view.findViewById(R.id.patient_gender_tv), selectedPatient.getGender());

        builder.setView(view);

        builder.setNegativeButton(R.string.action_close, (dialog, which) -> dialog.cancel());
        builder.create().show();
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

    private void showCancelDialog(int position) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.appointment_cancel_dialog, null);
        EditText cancel_reason_et = view.findViewById(R.id.appointmentCancelDialog_cancel_et);
        builder.setView(view);

        builder.setPositiveButton(R.string.action_cancel, null);
        builder.setNegativeButton(R.string.action_close, (dialog, which) -> dialog.dismiss());
        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((android.support.v7.app.AlertDialog) dialog).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String reason = cancel_reason_et.getText().toString();
                        AppointmentDTO selected_appointment = mDoctorAppointmentAdapter.getItem(position);
                        if (TextUtils.isEmpty(reason)) {
                            cancel_reason_et.setError(getString(R.string.error_field_required));
                            cancel_reason_et.requestFocus();
                            return;
                        }
                        selected_appointment.setCanceled(true);
                        selected_appointment.setCancelOrRescheduleReason(reason);
                        AppointmentService appointmentService = ServiceGenerator.createServiceSecured(AppointmentService.class);
                        Call<AppointmentDTO> request = appointmentService.update_appointment(selected_appointment.getId(), selected_appointment);
                        request.enqueue(new Callback<AppointmentDTO>() {
                            @Override
                            public void onResponse(Call<AppointmentDTO> call, Response<AppointmentDTO> response) {
                                switch (response.code()) {
                                    case 200: { // OK
                                        AppointmentDTO res = response.body();
                                        mDoctorAppointmentAdapter.remove(mDoctorAppointmentAdapter.getItem(position));
                                        mDoctorAppointmentAdapter.insert(res, position);
                                        mDoctorAppointmentAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                    default: {
                                        RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                                        // hendel errorResponse
                                        Toast.makeText(getContext(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, errorResponse.toString());
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<AppointmentDTO> call, Throwable t) {
                                Log.d(TAG, t.toString());
                            }
                        });

                        //Dismiss once everything is OK.
                        dialog.dismiss();

                    }
                });
            }
        });
        dialog.show();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.management_fregments_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.management_frags_refresh: {
                load_appointments();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void load_doctor(Long user_id) {
        UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
        Call<DoctorDTO> request = userService.getDoctorByUserId(user_id);
        showProgress(true);
        request.enqueue(new Callback<DoctorDTO>() {
            @Override
            public void onResponse(Call<DoctorDTO> call, Response<DoctorDTO> response) {
                switch (response.code()) {
                    case 200: {
                        curr_doctor = response.body();
                        load_appointments();
                        break;
                    }
                    default: {
                        RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                        // hendel errorResponse
                        Toast.makeText(getContext(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, errorResponse.toString());
                        break;
                    }
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<DoctorDTO> call, Throwable t) {
                Log.d(TAG, t.toString());
                showProgress(false);
            }
        });
    }


    public void load_appointments() {
        AppointmentService appointmentService = ServiceGenerator.createServiceSecured(AppointmentService.class);
        Call<List<AppointmentDTO>> request = appointmentService.get_appointments_by_doctor_id(curr_doctor.getAccount_id());
        showProgress(true);
        request.enqueue(new Callback<List<AppointmentDTO>>() {
            @Override
            public void onResponse(Call<List<AppointmentDTO>> call, Response<List<AppointmentDTO>> response) {
                if (response.body() != null) {
                    mDoctorAppointmentAdapter.clear();
                    for (AppointmentDTO dto : response.body()) {

                        mDoctorAppointmentAdapter.add(dto);
                    }
                    mDoctorAppointmentAdapter.notifyDataSetChanged();
                }

                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<AppointmentDTO>> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
                showProgress(false);
            }
        });

    }

    private void showProgress(boolean b) {
        if (b) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

    }


}
