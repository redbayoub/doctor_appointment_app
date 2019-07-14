package com.pro0inter.heydoc.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.adapters.DoctorAdapter;
import com.pro0inter.heydoc.adapters.DoctorServiceAdapter;
import com.pro0inter.heydoc.adapters.WorkingScheduleAdapter;
import com.pro0inter.heydoc.api.DTOs.AppointmentDTO;
import com.pro0inter.heydoc.api.DTOs.AppointmentDTO_ADD;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorServiceDTO;
import com.pro0inter.heydoc.api.DTOs.SpecialityDTO;
import com.pro0inter.heydoc.api.DTOs.WorkingScheduleDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.AppointmentService;
import com.pro0inter.heydoc.api.Services.DoctorService;
import com.pro0inter.heydoc.utils.UserInfoHolder;
import com.pro0inter.heydoc.utils.WeekDays;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAppointmentActivity extends AppCompatActivity {

    private final static String TIME_PATTREN = "HH:mm";
    private final static SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTREN);


    private static final String TAG = "AddAppointmentActivity";
    private boolean doctorSelected, periodSelected, serviceSelected;
    private DoctorDTO currentSelectedDoctor;
    private WorkingScheduleDTO currentWorkingSchedule;
    private DoctorServiceDTO currentDoctorService;

    private FrameLayout selectDoctorFrame, selectPeriodFrame, selectServiceFrame;

    private View mAdd_appointment_form, mProgressView, doctor_info_row_layout, workingSchedule_row_layout, doctor_service_row_layout;

    private EditText patientProblem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        selectDoctorFrame = findViewById(R.id.selectDoctorFrame);
        selectPeriodFrame = findViewById(R.id.selectPeriodFrame);
        selectServiceFrame = findViewById(R.id.selectServiceFrame);
        patientProblem = findViewById(R.id.AddApp_patientProblem);

        mProgressView = findViewById(R.id.AddApp_progress);
        mAdd_appointment_form = findViewById(R.id.AddApp_form);

        doctor_info_row_layout = getLayoutInflater().inflate(R.layout.doctor_row_layout, null);
        workingSchedule_row_layout = getLayoutInflater().inflate(R.layout.working_schedule_row_layout, null);
        doctor_service_row_layout = getLayoutInflater().inflate(R.layout.doctor_service_row_layout, null);


    }

    public void selectDoctor(View view) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.select_doctor_dialog, null);

        ListView doctorsListView = layout.findViewById(R.id.selectDoctor_list);

        DoctorAdapter doctorAdapter = new DoctorAdapter(this, R.layout.doctor_row_layout);
        doctorsListView.setAdapter(doctorAdapter);

        doctorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                doctorSelected = true;
                currentSelectedDoctor = doctorAdapter.getItem(position);
            }
        });

        loadDoctors(doctorAdapter);


        SearchView searchView = layout.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doctorAdapter.getFilter().filter(newText);
                return true;
            }
        });


        builder.setView(layout);

        DialogInterface.OnClickListener selectClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (currentSelectedDoctor == null) {
                    Toast.makeText(AddAppointmentActivity.this, "Nothing selected_darwable", Toast.LENGTH_SHORT).show();
                } else {
                    initSelectedDoctorFrame();
                }
            }
        };


        builder.setPositiveButton(R.string.action_select, selectClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void loadDoctors(DoctorAdapter doctorAdapter) {
        DoctorService doctorService = ServiceGenerator.createService(DoctorService.class);
        Call<List<DoctorDTO>> request = doctorService.get_all();
        request.enqueue(new Callback<List<DoctorDTO>>() {
            @Override
            public void onResponse(Call<List<DoctorDTO>> call, Response<List<DoctorDTO>> response) {
                if (response.body() != null) {
                    for (DoctorDTO dto : response.body()) {
                        if (dto.isApproved())
                            doctorAdapter.add(dto);
                    }
                    doctorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<DoctorDTO>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

    private void initSelectedDoctorFrame() {
        selectDoctorFrame.setBackgroundColor(Color.WHITE);
        selectDoctorFrame.removeAllViews();
        selectDoctorFrame.addView(doctor_info_row_layout);
        //selectDoctorFrame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        initInfoDoctorRowLayout();


        selectPeriodFrame.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        selectServiceFrame.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    private void initInfoDoctorRowLayout() {
        ((TextView) doctor_info_row_layout.findViewById(R.id.doctor_row_fullName)).setText(currentSelectedDoctor.getFirstName() + " " + currentSelectedDoctor.getLastName());
        ((TextView) doctor_info_row_layout.findViewById(R.id.doctor_row_gender)).setText((currentSelectedDoctor.getGender() == 'M') ? getResources().getString(R.string.gender_male) : getResources().getString(R.string.gender_female));

        Calendar calendar = Calendar.getInstance();
        int current_year = calendar.get(Calendar.YEAR);
        calendar.setTime(currentSelectedDoctor.getDateOfBirth());
        int birthDate_year = calendar.get(Calendar.YEAR);
        int age = current_year - birthDate_year;
        ((TextView) doctor_info_row_layout.findViewById(R.id.doctor_row_age)).setText(age + " ans");


        StringBuilder specialties = new StringBuilder();
        for (SpecialityDTO specialityDTO : currentSelectedDoctor.getSpecialities()) {
            specialties.append(specialityDTO.getTitle() + " , ");
        }
        ((TextView) doctor_info_row_layout.findViewById(R.id.doctor_row_specialities)).setText(specialties);

    }

    public void selectPeriod(View view) {
        if (doctorSelected) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.select_working_schedule_dialog, null);


            ListView workingSchedule = layout.findViewById(R.id.selectWorkingSchedule_list);

            WorkingScheduleAdapter workingScheduleAdapter = new WorkingScheduleAdapter(this, R.layout.working_schedule_row_layout);
            workingSchedule.setAdapter(workingScheduleAdapter);

            workingSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setSelected(true);
                    periodSelected = true;
                    currentWorkingSchedule = workingScheduleAdapter.getItem(position);

                }
            });
            for (WorkingScheduleDTO dto : currentSelectedDoctor.getWorkingSchedule()) {
                if (!dto.isHoliday()) workingScheduleAdapter.add(dto);
            }


            builder.setView(layout);

            DialogInterface.OnClickListener selectClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (currentWorkingSchedule == null) {
                        Toast.makeText(AddAppointmentActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show();
                    } else {
                        initPeriodFrame();
                    }
                }
            };


            builder.setPositiveButton(R.string.action_select, selectClickListener);
            builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
            builder.create().show();

        }
    }

    private void initPeriodFrame() {
        selectPeriodFrame.setBackgroundColor(Color.WHITE);
        selectPeriodFrame.removeAllViews();
        selectPeriodFrame.addView(workingSchedule_row_layout);
        //selectPeriodFrame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        initInfoPeriodRowLayout(workingSchedule_row_layout, currentWorkingSchedule);

    }

    private void initInfoPeriodRowLayout(View workingSchedule_row_layout, WorkingScheduleDTO currentWorkingSchedule) {


        ((TextView) workingSchedule_row_layout.findViewById(R.id.workingSchedule_day)).setText(WeekDays.of(currentWorkingSchedule.getDayOfWeek()).getDisplayName());


        ((TextView) workingSchedule_row_layout.findViewById(R.id.workingSchedule_fromTime)).setText(sdf.format(currentWorkingSchedule.getStartTime()));
        ((TextView) workingSchedule_row_layout.findViewById(R.id.workingSchedule_toTime)).setText(sdf.format(currentWorkingSchedule.getEndTime()));
        if (currentWorkingSchedule.isHoliday()) {

            ((TextView) workingSchedule_row_layout.findViewById(R.id.workingSchedule_isHoliday)).setText(getString(R.string.not_working));
            ((TextView) workingSchedule_row_layout.findViewById(R.id.workingSchedule_isHoliday)).setTextColor(getResources().getColor(R.color.color_red));

        } else {

            ((TextView) workingSchedule_row_layout.findViewById(R.id.workingSchedule_isHoliday)).setText(getString(R.string.working));
            ((TextView) workingSchedule_row_layout.findViewById(R.id.workingSchedule_isHoliday)).setTextColor(getResources().getColor(R.color.color_green));
        }


    }


    public void selectService(View view) {
        if (doctorSelected) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.select_doctor_service_dialog, null);


            ListView doctorServiceListView = layout.findViewById(R.id.selectDoctorService_list);

            DoctorServiceAdapter doctorServiceAdapter = new DoctorServiceAdapter(this, R.layout.doctor_service_row_layout);
            doctorServiceListView.setAdapter(doctorServiceAdapter);

            doctorServiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setSelected(true);
                    serviceSelected = true;
                    currentDoctorService = doctorServiceAdapter.getItem(position);

                }
            });

            loadDoctorServices(currentSelectedDoctor.getAccount_id(), doctorServiceAdapter);


            builder.setView(layout);

            DialogInterface.OnClickListener selectClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (currentDoctorService == null) {
                        Toast.makeText(AddAppointmentActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show();
                    } else {
                        initDoctorServiceFrame();
                    }
                }
            };


            builder.setPositiveButton(R.string.action_select, selectClickListener);
            builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
            builder.create().show();


        }

    }

    private void initDoctorServiceFrame() {
        selectServiceFrame.setBackgroundColor(Color.WHITE);
        selectServiceFrame.removeAllViews();
        selectServiceFrame.addView(doctor_service_row_layout);

        initInfoServiceRowLayout(doctor_service_row_layout, currentDoctorService);

    }

    private void initInfoServiceRowLayout(View doctor_service_row_layout, DoctorServiceDTO currentDoctorService) {
        ((TextView) doctor_service_row_layout.findViewById(R.id.DocService_title)).setText(currentDoctorService.getService().getTitle());
        ((TextView) doctor_service_row_layout.findViewById(R.id.DoctorService_estDur)).setText(currentDoctorService.getEstimatedDuration() + " min");
        ((TextView) doctor_service_row_layout.findViewById(R.id.DoctorService_fee)).setText(currentDoctorService.getFee() + " $");
    }

    private void loadDoctorServices(Long account_id, DoctorServiceAdapter doctorServiceAdapter) {
        DoctorService doctorService = ServiceGenerator.createService(DoctorService.class);
        Call<List<DoctorServiceDTO>> request = doctorService.getDoctorServicesByDocId(account_id);
        request.enqueue(new Callback<List<DoctorServiceDTO>>() {
            @Override
            public void onResponse(Call<List<DoctorServiceDTO>> call, Response<List<DoctorServiceDTO>> response) {
                if (response.body() != null) {
                    for (DoctorServiceDTO dto : response.body()) {
                        doctorServiceAdapter.add(dto);
                    }
                    doctorServiceAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<DoctorServiceDTO>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });

    }


    public void cancelAppointment(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void bookAppointment(View view) {
        if (patientProblem.getText().toString().trim().isEmpty()) {
            patientProblem.setError(getString(R.string.error_field_required));
            patientProblem.requestFocus();
            return;
        }

        if (doctorSelected && periodSelected && serviceSelected) {
            AppointmentDTO_ADD apointment_req = new AppointmentDTO_ADD();

            apointment_req.setDoctor_id(currentSelectedDoctor.getAccount_id());
            apointment_req.setPatient_id(UserInfoHolder.getInstance(this).getPatient().getPatient_id());
            apointment_req.setDoctor_service_id(currentDoctorService.getService().getId());
            apointment_req.setWorking_schedule_id(currentWorkingSchedule.getId());
            apointment_req.setFellowUpNumber((byte) 0);
            apointment_req.setPatientProblem(patientProblem.getText().toString());

            AppointmentService appointmentService = ServiceGenerator.createServiceSecured(AppointmentService.class);
            Call<AppointmentDTO> request = appointmentService.book_appointment(apointment_req);
            showProgress(true);
            request.enqueue(new Callback<AppointmentDTO>() {
                @Override
                public void onResponse(Call<AppointmentDTO> call, Response<AppointmentDTO> response) {
                    switch (response.code()) {
                        case 200: {
                            AppointmentDTO result = response.body();
                            Toast.makeText(AddAppointmentActivity.this, "You have booked an appointment at " + result.getStartTime().toString(), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddAppointmentActivity.this, MainActivity.class));

                            showProgress(false);
                            break;
                        }
                        default: {
                            RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                            // hendel errorResponse
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorResponse.toString());
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<AppointmentDTO> call, Throwable t) {
                    Log.d(TAG, t.toString());
                    showProgress(false);
                }
            });


        } else {
            Toast.makeText(this, "Please make sure that you selected the doctor and the period and the service", Toast.LENGTH_SHORT).show();
        }
    }


    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAdd_appointment_form.setVisibility(show ? View.GONE : View.VISIBLE);
            mAdd_appointment_form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAdd_appointment_form.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAdd_appointment_form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
