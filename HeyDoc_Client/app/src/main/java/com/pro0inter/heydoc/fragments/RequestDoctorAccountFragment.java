package com.pro0inter.heydoc.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.pro0inter.heydoc.api.DTOs.DoctorDTO_request;
import com.pro0inter.heydoc.api.DTOs.DoctorServiceDTO;
import com.pro0inter.heydoc.api.DTOs.SpecialityDTO;
import com.pro0inter.heydoc.api.DTOs.WorkingScheduleDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.DocService_Service;
import com.pro0inter.heydoc.api.Services.DoctorService;
import com.pro0inter.heydoc.api.Services.FileUploadService;
import com.pro0inter.heydoc.api.Services.SpecialityService;
import com.pro0inter.heydoc.utils.UserInfoHolder;
import com.pro0inter.heydoc.utils.WeekDays;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestDoctorAccountFragment extends Fragment {


    private static final String TAG = "ReqDocAccFragment";

    private final static String TIME_PATTREN = "HH:mm";
    private final static SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTREN);
    private static final int GALLERY = 120;
    private static final int CAMERA = 123;


    private View layout;

    private View mProgressView;
    private View mDocReqFormView;

    private TextView DocReq_clinic_gps_loc, DocReq_specialities, DocReq_services, DocReq_working_schedule;
    private EditText DocReq_clinic_addrs_et, DocReq_clinic_zip_code_et;
    private ImageView DocReq_account_picture;
    private String DocReq_account_picture_file_path,DocReq_account_picture_uploaded_image;
    private GPSLocation clinic_location;


    private DocServiceDTO selectedDocServiceDTO; // used for select doc service dialog
    private ArrayList<DocServiceDTO> serverDocServices = new ArrayList<>();
    private ArrayList<SpecialityDTO> serverSpecialities = new ArrayList<>();

    private ArrayList<SpecialityDTO> mySpecialities = new ArrayList<>();
    private ArrayList<DoctorServiceDTO> myDoctorServices = new ArrayList<>();
    private ArrayList<WorkingScheduleDTO> myWorkingSchedule = new ArrayList<>();

    private SparseBooleanArray selectedSpecialtiesSBA = new SparseBooleanArray();

    public RequestDoctorAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_request_doctor_account, container, false);

        // init ui refrences
        DocReq_clinic_addrs_et = layout.findViewById(R.id.DocReq_clinic_addrs_et);
        DocReq_clinic_zip_code_et = layout.findViewById(R.id.DocReq_clinic_zip_code_et);

        DocReq_clinic_gps_loc = layout.findViewById(R.id.DocReq_clinic_gps_loc);
        DocReq_specialities = layout.findViewById(R.id.DocReq_specialities);
        DocReq_services = layout.findViewById(R.id.DocReq_services);
        DocReq_working_schedule = layout.findViewById(R.id.DocReq_working_schedule);

        DocReq_account_picture = layout.findViewById(R.id.DocReq_account_picture);

        mProgressView = layout.findViewById(R.id.DocReq_sending_request_progress);
        mDocReqFormView = layout.findViewById(R.id.DocReq_form);

        // init btns actions
        layout.findViewById(R.id.DocReq_SetAccountPicture_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetAccountPicture();
            }
        });

        layout.findViewById(R.id.DocReq_SetClinicGpsLoc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClinicGpsLocPicker();
            }
        });

        layout.findViewById(R.id.DocReq_SetSpecialties_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetSpecialties();
            }
        });

        layout.findViewById(R.id.DocReq_SetServices_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetServices();

            }
        });

        layout.findViewById(R.id.DocReq_SetWorkingSchedule_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetWorkingSchedule();
            }
        });

        layout.findViewById(R.id.send_doctor_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clinic_location = new GPSLocation();
                clinic_location.lat = 10;
                clinic_location.lng = 10;
                showProgress(true);
                if(DocReq_account_picture_file_path!=null && !DocReq_account_picture_file_path.isEmpty()){
                    uploadFile(DocReq_account_picture_file_path,()->{
                        send_doctor_request(
                                DocReq_account_picture_uploaded_image,
                                DocReq_clinic_addrs_et.getText().toString(),
                                Integer.parseInt(DocReq_clinic_zip_code_et.getText().toString())
                                , clinic_location, mySpecialities, myWorkingSchedule, myDoctorServices);
                    });
                }else{
                    send_doctor_request(
                            null,
                            DocReq_clinic_addrs_et.getText().toString(),
                            Integer.parseInt(DocReq_clinic_zip_code_et.getText().toString())
                            , clinic_location, mySpecialities, myWorkingSchedule, myDoctorServices);
                }


            }
        });

        return layout;
    }

    private void uploadFile(String docReq_account_picture_file_path, Runnable onFinished) {

        FileUploadService fileUploadService = ServiceGenerator.createServiceSecured(FileUploadService.class);


        File file = new File(docReq_account_picture_file_path);

        //creating request body for file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);


        Call<String> request = fileUploadService.uploadFile(body);
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
               if(response.isSuccessful()){
                   DocReq_account_picture_uploaded_image=response.body();
               }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("FileUpload",t.toString());
            }
        });


    }

    private void showSetAccountPicture() {
        AlertDialog.Builder pictureDialogBuilder=new AlertDialog.Builder(getContext());

        String[] options=new String[]{"Pick From Gallery","Take A Picture"};
        pictureDialogBuilder.setTitle("Select Action");
        pictureDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               switch (which){
                   case 0:{ //Pick From Gallery
                       choosePhotoFromGallary();
                       break;
                   }
                   case 1:{ //Take A Picture
                       takePhotoFromCamera();
                       break;
                   }
               }
            }
        });
        pictureDialogBuilder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    DocReq_account_picture_file_path = saveImage(bitmap);
                    Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    DocReq_account_picture.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            DocReq_account_picture.setImageBitmap(thumbnail);
            DocReq_account_picture_file_path=saveImage(thumbnail);
            Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void choosePhotoFromGallary() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, GALLERY);
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(getActivity().getCacheDir()+ "images/");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void send_doctor_request(
            String docReq_account_picture, String clinic_addrs,
            int clinic_zipCode,
            GPSLocation clinic_location,
            ArrayList<SpecialityDTO> mySpecialities,
            ArrayList<WorkingScheduleDTO> myWorkingSchedule,
            ArrayList<DoctorServiceDTO> myDoctorServices) {

        DoctorDTO_request doctorDTO_request = new DoctorDTO_request();

        doctorDTO_request.setUser_id(UserInfoHolder.getInstance(getContext()).getUser().getUser_id());

        doctorDTO_request.setPicture(docReq_account_picture);

        doctorDTO_request.setAddress(clinic_addrs);
        doctorDTO_request.setZipCode(clinic_zipCode);
        doctorDTO_request.setClinic_addrs_lat(clinic_location.lat);
        doctorDTO_request.setClinic_addrs_lng(clinic_location.lng);

        doctorDTO_request.setDoctorServices(myDoctorServices);
        doctorDTO_request.setSpecialities(mySpecialities);
        doctorDTO_request.setWorkingSchedule(myWorkingSchedule);



        DoctorService doctorService = ServiceGenerator.createServiceSecured(DoctorService.class);
        Call<DoctorDTO> request = doctorService.requestDoctorAccount(doctorDTO_request);

        request.enqueue(new Callback<DoctorDTO>() {
            @Override
            public void onResponse(Call<DoctorDTO> call, Response<DoctorDTO> response) {
                switch (response.code()) {
                    case 201: {
                        Toast.makeText(getActivity(), "Your Request has been sended, Your account will be approved by the admin first to be able to use it", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                        startActivity(new Intent(getContext(), MainActivity.class));
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
                    //initSelectedWorkingScuduleFrame();
                }
                if (myWorkingSchedule.isEmpty()) {
                    DocReq_working_schedule.setText(R.string.nothing_selected);
                } else {
                    StringBuilder workingDays = new StringBuilder();
                    for (WorkingScheduleDTO dto : myWorkingSchedule) {
                        if (!dto.isHoliday()) {
                            workingDays.append(WeekDays.of(dto.getDayOfWeek())).append(" , ");
                        }
                    }
                    DocReq_working_schedule.setText(workingDays.toString());
                }

            }
        };


        builder.setPositiveButton(R.string.set, setClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
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

        builder.setPositiveButton(R.string.action_add, null);//Set to null. We override the onclick
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button button = ((android.support.v7.app.AlertDialog) dialog).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
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
            DocReq_services.setText(R.string.nothing_selected);
        } else {
            StringBuilder services = new StringBuilder();
            for (DoctorServiceDTO serviceDTO : myDoctorServices) {
                services.append(serviceDTO.getService().getTitle() + " , ");
            }
            DocReq_services.setText(services.toString());
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
            DocReq_specialities.setText(R.string.nothing_selected);
        } else {
            StringBuilder specialties = new StringBuilder();
            for (SpecialityDTO specialityDTO : mySpecialities) {
                specialties.append(specialityDTO.getTitle() + " , ");
            }
            DocReq_specialities.setText(specialties.toString());
        }


    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mDocReqFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mDocReqFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDocReqFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mDocReqFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class GPSLocation {
        double lat;
        double lng;
    }
}
