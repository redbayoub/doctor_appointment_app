package com.pro0inter.heydoc.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.pro0inter.heydoc.activities.AddAppointmentActivity;
import com.pro0inter.heydoc.adapters.DoctorServiceAdapter;
import com.pro0inter.heydoc.adapters.PatientAppointmentAdapter;
import com.pro0inter.heydoc.adapters.SpecialityAdapter;
import com.pro0inter.heydoc.adapters.WorkingScheduleAdapter;
import com.pro0inter.heydoc.api.DTOs.AppointmentDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorServiceDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.AppointmentService;
import com.pro0inter.heydoc.api.Services.DoctorService;
import com.pro0inter.heydoc.domain.Patient;
import com.pro0inter.heydoc.utils.UserInfoHolder;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientAppointmentListFragment extends Fragment {

    private final static String TAG = "PatientAppointListFrag";
    private static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BIRTH_DATE_FORMAT);


    ListView mListView;
    ProgressBar mProgressBar;
    PatientAppointmentAdapter mPatientAppointmentAdapter;
    Patient curr_patient;


    public PatientAppointmentListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_appointment_list, container, false);

        FloatingActionButton button = view.findViewById(R.id.add_appointment);
        button.setOnClickListener(v -> {
            // Add appointment
            startActivity(new Intent(getActivity(), AddAppointmentActivity.class));

        });

        mProgressBar = view.findViewById(R.id.loading_patient_appointment_list_progress);
        mListView = view.findViewById(R.id.patient_appointment_listview);


        mPatientAppointmentAdapter = new PatientAppointmentAdapter(getActivity(), R.layout.patient_appointment_row_layout);
        mListView.setAdapter(mPatientAppointmentAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // show context menu
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.appointment_manage_menu);
                popupMenu.getMenu().findItem(R.id.appointment_setAsFinished).setVisible(false);
                popupMenu.getMenu().findItem(R.id.appointment_showPatientDetails).setVisible(false);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.appointment_get_dir: {
                                getDirections(position);
                                return true;
                            }
                            case R.id.appointment_showDoctorDetails: {
                                showDoctorDetails(position);
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


        curr_patient = UserInfoHolder.getInstance(getActivity()).getPatient();

        load_appointments();

        setHasOptionsMenu(true);

        return view;
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

    public void load_appointments() {
        AppointmentService appointmentService = ServiceGenerator.createServiceSecured(AppointmentService.class);
        Call<List<AppointmentDTO>> request = appointmentService.get_appointments_by_patient_id(curr_patient.getPatient_id());
        request.enqueue(new Callback<List<AppointmentDTO>>() {
            @Override
            public void onResponse(Call<List<AppointmentDTO>> call, Response<List<AppointmentDTO>> response) {
                if (response.body() != null) {
                    mPatientAppointmentAdapter.clear();
                    for (AppointmentDTO dto : response.body()) {

                        mPatientAppointmentAdapter.add(dto);
                    }
                    mPatientAppointmentAdapter.notifyDataSetChanged();
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
                        AppointmentDTO selected_appointment = mPatientAppointmentAdapter.getItem(position);
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
                                        mPatientAppointmentAdapter.remove(mPatientAppointmentAdapter.getItem(position));
                                        mPatientAppointmentAdapter.insert(res, position);
                                        mPatientAppointmentAdapter.notifyDataSetChanged();
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


    private void getDirections(int position) {
        // TODO implement getDirections
    }

    private void showDoctorDetails(int position) {
        DoctorDTO selectedDoctor = mPatientAppointmentAdapter.getItem(position).getDoctor();

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.doctor_view_dialog, null);
        // init

        ((TextView) view.findViewById(R.id.doctor_firstName_tv)).setText(selectedDoctor.getFirstName());
        ((TextView) view.findViewById(R.id.doctor_lastName_tv)).setText(selectedDoctor.getLastName());
        ((TextView) view.findViewById(R.id.doctor_birthDate_tv)).setText(simpleDateFormat.format(selectedDoctor.getDateOfBirth()));

        if (selectedDoctor.isApproved()) {
            ((TextView) view.findViewById(R.id.doctor_approved_tv)).setText(Boolean.toString(selectedDoctor.isApproved()));
            ((TextView) view.findViewById(R.id.doctor_approved_tv)).setTextColor(getContext().getResources().getColor(R.color.color_green));

        } else {
            ((TextView) view.findViewById(R.id.doctor_approved_tv)).setText(Boolean.toString(selectedDoctor.isApproved()));
            ((TextView) view.findViewById(R.id.doctor_approved_tv)).setTextColor(getContext().getResources().getColor(R.color.color_red));
        }


        setGender(view.findViewById(R.id.doctor_gender_tv), selectedDoctor.getGender());

        ListView specialtiesListView = view.findViewById(R.id.doctor_specialities);
        SpecialityAdapter specialityAdapter = new SpecialityAdapter(getContext(), R.layout.speciality_row_layout);
        specialtiesListView.setAdapter(specialityAdapter);
        specialityAdapter.addAll(selectedDoctor.getSpecialities());

        ListView servicesListView = view.findViewById(R.id.doctor_services);
        DoctorServiceAdapter doctorServiceAdapter = new DoctorServiceAdapter(getContext(), R.layout.doctor_service_row_layout);
        servicesListView.setAdapter(doctorServiceAdapter);
        load_doctor_services(selectedDoctor.getAccount_id(), doctorServiceAdapter);


        ListView workingScheduleListView = view.findViewById(R.id.doctor_workingSchedule);
        WorkingScheduleAdapter workingScheduleAdapter = new WorkingScheduleAdapter(getContext(), R.layout.working_schedule_row_layout);
        workingScheduleListView.setAdapter(workingScheduleAdapter);
        workingScheduleAdapter.addAll(selectedDoctor.getWorkingSchedule());

        //end init
        builder.setView(view);

        builder.setNegativeButton(R.string.action_close, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }


    private void load_doctor_services(Long account_id, DoctorServiceAdapter doctorServiceAdapter) {
        DoctorService doctorService = ServiceGenerator.createService(DoctorService.class);
        Call<List<DoctorServiceDTO>> request = doctorService.getDoctorServicesByDocId(account_id);
        request.enqueue(new Callback<List<DoctorServiceDTO>>() {
            @Override
            public void onResponse(Call<List<DoctorServiceDTO>> call, Response<List<DoctorServiceDTO>> response) {
                if (response != null) {
                    doctorServiceAdapter.addAll(response.body());
                    doctorServiceAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<DoctorServiceDTO>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });

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


}
