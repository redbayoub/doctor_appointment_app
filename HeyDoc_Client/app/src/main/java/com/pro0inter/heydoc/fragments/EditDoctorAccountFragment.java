package com.pro0inter.heydoc.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.activities.MainActivity;
import com.pro0inter.heydoc.adapters.DocServiceAdapter;
import com.pro0inter.heydoc.adapters.DoctorServiceAdapter;
import com.pro0inter.heydoc.adapters.SpecialityAdapter;
import com.pro0inter.heydoc.adapters.WorkingScheduleAdapter;
import com.pro0inter.heydoc.api.DTOs.DocServiceDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO_update;
import com.pro0inter.heydoc.api.DTOs.DoctorServiceDTO;
import com.pro0inter.heydoc.api.DTOs.SpecialityDTO;
import com.pro0inter.heydoc.api.DTOs.WorkingScheduleDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.DocService_Service;
import com.pro0inter.heydoc.api.Services.DoctorService;
import com.pro0inter.heydoc.api.Services.SpecialityService;
import com.pro0inter.heydoc.api.Services.UserService;
import com.pro0inter.heydoc.fragments.interfaces.ViewPervFragment;
import com.pro0inter.heydoc.utils.UserInfoHolder;
import com.pro0inter.heydoc.utils.WeekDays;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditDoctorAccountFragment extends Fragment {


    private static final String TAG = "EditDoctorAccountFrag";

    private final static String TIME_PATTREN = "HH:mm";
    private final static SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTREN);


    private View layout;

    private View mProgressView;
    private View mDoctorEditFormView;

    private TextView DoctorEdit_clinic_gps_loc, DoctorEdit_specialities, DoctorEdit_services, DoctorEdit_working_schedule;
    private EditText DoctorEdit_clinic_addrs_et, DoctorEdit_clinic_zip_code_et;
    private GPSLocation clinic_location;


    private DocServiceDTO selectedDocServiceDTO; // used for select doc service dialog
    private ArrayList<DocServiceDTO> serverDocServices = new ArrayList<>();
    private ArrayList<SpecialityDTO> serverSpecialities = new ArrayList<>();

    private ArrayList<SpecialityDTO> mySpecialities = new ArrayList<>();
    private ArrayList<DoctorServiceDTO> myDoctorServices = new ArrayList<>();
    private ArrayList<DoctorServiceDTO> originalDoctorServices = new ArrayList<>();
    private ArrayList<WorkingScheduleDTO> myWorkingSchedule = new ArrayList<>();

    private SparseBooleanArray selectedSpecialtiesSBA = new SparseBooleanArray();

    private DoctorDTO currDoctor;

    public EditDoctorAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_edit_doctor_account, container, false);

        // init ui refrences
        DoctorEdit_clinic_addrs_et = layout.findViewById(R.id.DoctorEdit_clinic_addrs_et);
        DoctorEdit_clinic_zip_code_et = layout.findViewById(R.id.DoctorEdit_clinic_zip_code_et);

        DoctorEdit_clinic_gps_loc = layout.findViewById(R.id.DoctorEdit_clinic_gps_loc);
        DoctorEdit_specialities = layout.findViewById(R.id.DoctorEdit_specialities);
        DoctorEdit_services = layout.findViewById(R.id.DoctorEdit_services);
        DoctorEdit_working_schedule = layout.findViewById(R.id.DoctorEdit_working_schedule);

        mProgressView = layout.findViewById(R.id.DoctorEdit_progress);
        mDoctorEditFormView = layout.findViewById(R.id.DoctorEdit_form);

        // init btns actions
        layout.findViewById(R.id.DoctorEdit_SetClinicGpsLoc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClinicGpsLocPicker();
            }
        });

        layout.findViewById(R.id.DoctorEdit_SetSpecialties_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetSpecialties();
            }
        });

        layout.findViewById(R.id.DoctorEdit_SetServices_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetServices();

            }
        });

        layout.findViewById(R.id.DoctorEdit_SetWorkingSchedule_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetWorkingSchedule();
            }
        });

        layout.findViewById(R.id.DoctorEdit_save_modifications_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clinic_location = new GPSLocation();
                clinic_location.lat = 10;
                clinic_location.lng = 10;

                saveModifications(DoctorEdit_clinic_addrs_et.getText().toString(),
                        Integer.parseInt(DoctorEdit_clinic_zip_code_et.getText().toString())
                        , clinic_location, mySpecialities, myWorkingSchedule, myDoctorServices);
            }
        });

        load_doctor();
        setHasOptionsMenu(true);
        return layout;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.edit_account_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editAccountMenu_deleteAccount: {
                showDeleteDoctorDialog();

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDoctorDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_account_dialog_title);

        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                DoctorService doctorService = ServiceGenerator.createServiceSecured(DoctorService.class);
                Call<Void> request = doctorService.delDoctor(currDoctor.getAccount_id());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case 200: {
                                Toast.makeText(getActivity(), "Your Account has been DELETED", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), MainActivity.class));
                                break;
                            }
                            default: {
                                RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                                // hendel errorResponse
                                Toast.makeText(getActivity(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, errorResponse.toString());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d(TAG, t.toString());
                    }
                });

            }


        });
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }


    private void initUI(DoctorDTO doctorDTO) {
        mySpecialities.addAll(doctorDTO.getSpecialities());
        myWorkingSchedule.addAll(doctorDTO.getWorkingSchedule());
        initSelectedWorkingScuduleFrame();
        initSelectedSpecialtiesFrame();

        clinic_location = new GPSLocation();
        clinic_location.lat = doctorDTO.getClinic_addrs_lat();
        clinic_location.lng = doctorDTO.getClinic_addrs_lng();
        DoctorEdit_clinic_gps_loc.setText(clinic_location.toString());

        DoctorEdit_clinic_addrs_et.setText(doctorDTO.getAddress());
        DoctorEdit_clinic_zip_code_et.setText(Integer.toString(doctorDTO.getZipCode()));


    }

    private void load_doctor() {
        UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
        Call<DoctorDTO> request = userService.getDoctorByUserId(UserInfoHolder.getInstance(getContext()).getUser().getUser_id());
        showProgress(true);
        request.enqueue(new Callback<DoctorDTO>() {
            @Override
            public void onResponse(Call<DoctorDTO> call, Response<DoctorDTO> response) {
                switch (response.code()) {
                    case 200: {
                        currDoctor = response.body();
                        load_doctor_services(currDoctor);
                        initUI(currDoctor);
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

    private void load_doctor_services(DoctorDTO currDoctor) {
        DoctorService doctorService = ServiceGenerator.createService(DoctorService.class);
        Call<List<DoctorServiceDTO>> request = doctorService.getDoctorServicesByDocId(currDoctor.getAccount_id());
        request.enqueue(new Callback<List<DoctorServiceDTO>>() {
            @Override
            public void onResponse(Call<List<DoctorServiceDTO>> call, Response<List<DoctorServiceDTO>> response) {
                switch (response.code()) {
                    case 200: {
                        myDoctorServices.addAll(response.body());
                        originalDoctorServices.addAll(response.body());
                        initSelectedServicesFrame();
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
            public void onFailure(Call<List<DoctorServiceDTO>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

    private void saveModifications(String clinic_addrs, int clinic_zipCode,
                                   GPSLocation clinic_location,
                                   ArrayList<SpecialityDTO> mySpecialities,
                                   ArrayList<WorkingScheduleDTO> myWorkingSchedule,
                                   ArrayList<DoctorServiceDTO> myDoctorServices) {
        DoctorDTO_update new_info = new DoctorDTO_update(currDoctor.getAccount_id());

        if (!currDoctor.getAddress().equals(clinic_addrs))
            new_info.setAddress(clinic_addrs);

        if (!currDoctor.getZipCode().equals(clinic_zipCode))
            new_info.setZipCode(clinic_zipCode);

        if (!currDoctor.getClinic_addrs_lat().equals(clinic_location.lat))
            new_info.setClinic_addrs_lat(clinic_location.lat);

        if (!currDoctor.getClinic_addrs_lng().equals(clinic_location.lng))
            new_info.setClinic_addrs_lng(clinic_location.lng);

        if (!currDoctor.getSpecialities().equals(mySpecialities))
            new_info.setSpecialities(mySpecialities);

        if (!currDoctor.getWorkingSchedule().equals(myWorkingSchedule))
            new_info.setWorkingSchedule(myWorkingSchedule);

        if (!originalDoctorServices.equals(myDoctorServices)) {
            new_info.setDoctorServices(myDoctorServices);
        }


        DoctorService doctorService = ServiceGenerator.createServiceSecured(DoctorService.class);
        Call<DoctorDTO> request = doctorService.updateDoctor(currDoctor.getAccount_id(), new_info);
        showProgress(true);
        request.enqueue(new Callback<DoctorDTO>() {
            @Override
            public void onResponse(Call<DoctorDTO> call, Response<DoctorDTO> response) {
                switch (response.code()) {
                    case 200: {
                        Toast.makeText(getContext(), "Doctor Info has been Updated", Toast.LENGTH_SHORT).show();
                        ((ViewPervFragment) getActivity()).viewPervFragment();
                        showProgress(false);
                        break;
                    }
                    default: {
                        RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                        // hendel errorResponse
                        Toast.makeText(getActivity(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, errorResponse.toString());
                        showProgress(false);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<DoctorDTO> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
                showProgress(false);
            }
        });

    }


    //================== Clinic GPS Location Picker set dialog =============================

    private void showClinicGpsLocPicker() {
        //PlacePicker.IntentBulider
        clinic_location = null;

    }

    //================== Working Schedule set dialog =============================
    private void showSetWorkingSchedule() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.set_working_schedule, null);

        ListView workingScheduleListView = layout.findViewById(R.id.WorkingSchedule_list);
        WorkingScheduleAdapter workingScheduleAdapter = new WorkingScheduleAdapter(getContext(), R.layout.working_schedule_row_layout);


        workingScheduleListView.setAdapter(workingScheduleAdapter);

        workingScheduleAdapter.addAll(myWorkingSchedule);


        layout.findViewById(R.id.WorkingSchedule_add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddWorkingSchedule(workingScheduleAdapter);
            }
        });


        builder.setView(layout);

        DialogInterface.OnClickListener setClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (workingScheduleAdapter.getCount() != 0) {
                    myWorkingSchedule.clear();
                    for (int i = 0; i < workingScheduleAdapter.getCount(); i++) {
                        myWorkingSchedule.add(workingScheduleAdapter.getItem(i));
                    }

                }
                initSelectedWorkingScuduleFrame();


            }
        };


        builder.setPositiveButton(R.string.set, setClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void initSelectedWorkingScuduleFrame() {
        if (myWorkingSchedule.isEmpty()) {
            DoctorEdit_working_schedule.setText(R.string.nothing_selected);
        } else {
            StringBuilder workingDays = new StringBuilder();
            for (WorkingScheduleDTO dto : myWorkingSchedule) {
                if (!dto.isHoliday()) {
                    workingDays.append(WeekDays.of(dto.getDayOfWeek())).append(" , ");
                }
            }
            DoctorEdit_working_schedule.setText(workingDays.toString());
        }
    }

    private void showAddWorkingSchedule(WorkingScheduleAdapter workingScheduleAdapter) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.working_schedule_add_dialog, null);

        Spinner day_spinner = layout.findViewById(R.id.AddWorkingSchedule_day_spinner);
        ArrayAdapter<WeekDays> weekDaysArrayAdapter = new ArrayAdapter<WeekDays>(getContext(), android.R.layout.simple_spinner_item, WeekDays.values());
        weekDaysArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day_spinner.setAdapter(weekDaysArrayAdapter);


        EditText fromTimeEditText = layout.findViewById(R.id.AddWorkingSchedule_fromTime);
        EditText toTimeEditText = layout.findViewById(R.id.AddWorkingSchedule_toTime);
        CheckBox holidayCheckBox = layout.findViewById(R.id.AddWorkingSchedule_holiday);


        fromTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                fromTimeEditText.setText(hourOfDay + ":" + minute);
                            }
                        }, 0, 0, true);
                timePickerDialog.show();
            }
        });

        toTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                toTimeEditText.setText(hourOfDay + ":" + minute);
                            }
                        }, 0, 0, true);
                timePickerDialog.show();
            }
        });

        builder.setView(layout);

        DialogInterface.OnClickListener addClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };


        builder.setPositiveButton(R.string.action_add, null);//Set to null. We override the onclick
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (day_spinner.getSelectedItem() != null &&
                                !fromTimeEditText.getText().toString().isEmpty() &&
                                !toTimeEditText.getText().toString().isEmpty()) {
                            int day = -1;
                            boolean isHoliday;
                            Date fromTime = null;
                            Date toTime = null;
                            try {
                                fromTime = sdf.parse(fromTimeEditText.getText().toString());
                                toTime = sdf.parse(toTimeEditText.getText().toString());

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (fromTime.after(toTime)) {
                                fromTimeEditText.setError(getString(R.string.time_error));
                                fromTimeEditText.requestFocus();
                                Toast.makeText(getContext(), R.string.time_error, Toast.LENGTH_SHORT).show();
                                return;

                            }


                            day = ((WeekDays) day_spinner.getSelectedItem()).getValue();
                            isHoliday = holidayCheckBox.isChecked();
                            if (day != -1 && fromTime != null && toTime != null) {
                                WorkingScheduleDTO dto = new WorkingScheduleDTO();
                                dto.setDayOfWeek(Byte.parseByte(Integer.toString(day)));
                                dto.setStartTime(fromTime);
                                dto.setEndTime(toTime);
                                dto.setHoliday(isHoliday);

                                workingScheduleAdapter.add(dto);
                                workingScheduleAdapter.notifyDataSetChanged();

                                //Dismiss once everything is OK.
                                dialog.dismiss();
                            }

                        }

                    }
                });
            }
        });

        dialog.show();

    }


    //================== Doctor Services set dialog =============================
    private void showSetServices() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.set_services_dialog, null);

        ListView servicesListView = layout.findViewById(R.id.DoctorServices_list);
        DoctorServiceAdapter doctorServiceAdapter = new DoctorServiceAdapter(getContext(), R.layout.doctor_service_row_layout);

        servicesListView.setAdapter(doctorServiceAdapter);

        doctorServiceAdapter.addAll(myDoctorServices);


        layout.findViewById(R.id.DoctorService_add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDoctorService(doctorServiceAdapter);
            }
        });


        SearchView searchView = layout.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doctorServiceAdapter.getFilter().filter(newText);
                return true;
            }
        });


        builder.setView(layout);

        DialogInterface.OnClickListener setClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (doctorServiceAdapter.getCount() != 0) {
                    myDoctorServices.clear();
                    for (int i = 0; i < doctorServiceAdapter.getCount(); i++) {
                        myDoctorServices.add(doctorServiceAdapter.getItem(i));
                    }
                    initSelectedServicesFrame();
                }
            }
        };


        builder.setPositiveButton(R.string.set, setClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    // ------------ add doctor service dialog ------------------------
    private void showAddDoctorService(DoctorServiceAdapter doctorServiceAdapter) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.doctor_service_add_dialog, null);


        TextView selectedDocServiceTextView = layout.findViewById(R.id.AddDoctorService_selectedDocService);

        layout.findViewById(R.id.AddDoctorService_SetDocService_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDocService(selectedDocServiceTextView);
            }
        });

        EditText feeEditText = layout.findViewById(R.id.AddDocService_fee);
        EditText estDurEditText = layout.findViewById(R.id.AddDocService_estDur);

        builder.setView(layout);

        DialogInterface.OnClickListener addClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedDocServiceDTO != null &&
                        !feeEditText.getText().toString().isEmpty() &&
                        !estDurEditText.getText().toString().isEmpty()) {
                    DoctorServiceDTO dto = new DoctorServiceDTO();
                    dto.setService(selectedDocServiceDTO);
                    dto.setEstimatedDuration(Byte.parseByte(estDurEditText.getText().toString()));
                    dto.setFee(BigDecimal.valueOf(Double.parseDouble(feeEditText.getText().toString())));

                    doctorServiceAdapter.add(dto);
                    doctorServiceAdapter.notifyDataSetChanged();
                }

            }
        };


        builder.setPositiveButton(R.string.action_add, addClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }

    // --------------------- show select doc service ------------------
    private void showSelectDocService(TextView selectedDocServiceTextView) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.select_doc_service_dialog, null);


        ListView doc_servicesListView = layout.findViewById(R.id.selectDocService_list);
        DocServiceAdapter docServiceAdapter = new DocServiceAdapter(getContext(), R.layout.doc_service_row_layout);

        doc_servicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                selectedDocServiceDTO = docServiceAdapter.getItem(position);
            }
        });

        doc_servicesListView.setAdapter(docServiceAdapter);

        if (serverDocServices == null || serverDocServices.isEmpty())
            load_doc_services(docServiceAdapter);
        else
            docServiceAdapter.addAll(serverDocServices);


        SearchView searchView = layout.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                docServiceAdapter.getFilter().filter(newText);
                return true;
            }
        });


        builder.setView(layout);

        DialogInterface.OnClickListener setClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (selectedDocServiceDTO == null) {
                    Toast.makeText(getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
                } else {
                    initSelectedDocServiceFrame(selectedDocServiceTextView);
                }
            }
        };


        builder.setPositiveButton(R.string.set, setClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }

    private void initSelectedDocServiceFrame(TextView selectedDocServiceTextView) {
        selectedDocServiceTextView.setText(selectedDocServiceDTO.getTitle());
    }

    private void load_doc_services(DocServiceAdapter docServiceAdapter) {

        // get list of doc services
        DocService_Service docService_service = ServiceGenerator.createService(DocService_Service.class);
        Call<ArrayList<DocServiceDTO>> request = docService_service.get_docservices();
        request.enqueue(new Callback<ArrayList<DocServiceDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<DocServiceDTO>> call, Response<ArrayList<DocServiceDTO>> response) {
                if (response.body() != null) {
                    docServiceAdapter.addAll(response.body());
                    docServiceAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<DocServiceDTO>> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
            }
        });
    }
    // --------------------- end show select doc service ------------------

    // -------------------------- end add doctor service -----------------------

    private void initSelectedServicesFrame() {
        if (myDoctorServices.size() == 0) {
            DoctorEdit_services.setText(R.string.nothing_selected);
        } else {
            StringBuilder services = new StringBuilder();
            for (DoctorServiceDTO serviceDTO : myDoctorServices) {
                services.append(serviceDTO.getService().getTitle() + " , ");
            }
            DoctorEdit_services.setText(services.toString());
        }

    }


    //=================== Specialities set dialog ===========================
    private void load_specialities(SpecialityAdapter specialityAdapter, SparseBooleanArray selectedSpecialtiesSBA) {

        // get list of specialities
        SpecialityService specialityService = ServiceGenerator.createService(SpecialityService.class);
        Call<ArrayList<SpecialityDTO>> request = specialityService.get_specialities();
        request.enqueue(new Callback<ArrayList<SpecialityDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<SpecialityDTO>> call, Response<ArrayList<SpecialityDTO>> response) {
                if (response.body() != null) {
                    specialityAdapter.addAll(response.body());
                    specialityAdapter.notifyDataSetChanged();

                    serverSpecialities.addAll(response.body());
                    // init selectedSpecialtiesSBA
                    for (int i = 0; i < serverSpecialities.size(); i++) {
                        selectedSpecialtiesSBA.put(i, false);
                    }
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SpecialityDTO>> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
            }
        });


    }

    private void showSetSpecialties() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.select_specialties_dialog, null);

        ListView specailitiesListView = layout.findViewById(R.id.selectSpecialities_list);


        SpecialityAdapter specialityAdapter = new SpecialityAdapter(
                getContext(), R.layout.speciality_row_layout, selectedSpecialtiesSBA);
        specailitiesListView.setAdapter(specialityAdapter);


        specailitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (selectedSpecialtiesSBA.get(position)) { // view allready selected
                    view.setBackgroundColor(Color.WHITE);
                    selectedSpecialtiesSBA.put(position, false);
                } else {
                    view.setBackgroundColor(getContext().getResources().getColor(R.color.colorSelected));
                    selectedSpecialtiesSBA.put(position, true);
                }
            }
        });

        if (serverSpecialities == null || serverSpecialities.isEmpty()) {
            load_specialities(specialityAdapter, selectedSpecialtiesSBA);
        } else {
            specialityAdapter.addAll(serverSpecialities);
            // init selectedSpecialtiesSBA
            for (int i = 0; i < serverSpecialities.size(); i++) {
                selectedSpecialtiesSBA.put(i, false);
            }
        }


        SearchView searchView = layout.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                specialityAdapter.getFilter().filter(newText);
                return true;
            }
        });


        builder.setView(layout);

        DialogInterface.OnClickListener setClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mySpecialities.clear();
                for (int i = 0; i < selectedSpecialtiesSBA.size(); i++) {
                    if (selectedSpecialtiesSBA.get(i)) {
                        mySpecialities.add(specialityAdapter.getItem(i));
                    }
                }

                if (mySpecialities.size() == 0) {
                    Toast.makeText(getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
                } else {
                    initSelectedSpecialtiesFrame();
                }
            }
        };


        builder.setPositiveButton(R.string.set, setClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void initSelectedSpecialtiesFrame() {
        if (mySpecialities.size() == 0) {
            DoctorEdit_specialities.setText(R.string.nothing_selected);
        } else {
            StringBuilder specialties = new StringBuilder();
            for (SpecialityDTO specialityDTO : mySpecialities) {
                specialties.append(specialityDTO.getTitle() + " , ");
            }
            DoctorEdit_specialities.setText(specialties.toString());
        }


    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mDoctorEditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mDoctorEditFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDoctorEditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mDoctorEditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private class GPSLocation {
        double lat;
        double lng;

        @Override
        public String toString() {
            return "lat= " + lat + " : lng= " + lng;
        }
    }
}
