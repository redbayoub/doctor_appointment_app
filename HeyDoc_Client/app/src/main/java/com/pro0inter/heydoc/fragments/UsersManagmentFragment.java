package com.pro0inter.heydoc.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GetTokenResult;
import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.adapters.DoctorServiceAdapter;
import com.pro0inter.heydoc.adapters.SpecialityAdapter;
import com.pro0inter.heydoc.adapters.UserAdapter;
import com.pro0inter.heydoc.adapters.WorkingScheduleAdapter;
import com.pro0inter.heydoc.api.DTOs.AdminDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorServiceDTO;
import com.pro0inter.heydoc.api.DTOs.PatientDTO;
import com.pro0inter.heydoc.api.DTOs.UserDTO_In;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.DoctorService;
import com.pro0inter.heydoc.api.Services.UserService;
import com.pro0inter.heydoc.utils.Roles;
import com.pro0inter.heydoc.utils.UserInfoHolder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersManagmentFragment extends Fragment {
    private static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BIRTH_DATE_FORMAT);
    private static final String TAG = "UsersManagmentFragment";


    private UserAdapter mUserAdapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public UsersManagmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_managment, container, false);

        ListView mListView = view.findViewById(R.id.users_listview);

        mUserAdapter = new UserAdapter(getActivity(), R.layout.user_row_layout);
        mListView.setAdapter(mUserAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // show context menu
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.manage_context_menu);

                popupMenu.getMenu().findItem(R.id.manage_edit).setVisible(false);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.manage_view: {
                                showViewDialog(position);
                                return true;
                            }

                            case R.id.manage_delete: {
                                showDeleteDialog(position);
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


        mProgressBar = view.findViewById(R.id.loading_users_list_progress);

        mSwipeRefreshLayout = view.findViewById(R.id.users_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load_users();
            }
        });

        load_users();

        setHasOptionsMenu(true);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.management_fregments_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.management_frags_search).getActionView();
        setupSearchView(searchView);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.management_frags_refresh: {
                load_users();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView(SearchView searchView) {
        searchView.setQueryHint(getString(R.string.type_to_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mUserAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void load_users() {
        UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
        Call<List<UserDTO_In>> request = userService.get_users();
        if (!mSwipeRefreshLayout.isRefreshing())
            showProgress(true);
        request.enqueue(new Callback<List<UserDTO_In>>() {
            @Override
            public void onResponse(Call<List<UserDTO_In>> call, Response<List<UserDTO_In>> response) {
                switch (response.code()) {
                    case 200: {
                        if (response.body() != null) {

                            mUserAdapter.clear();
                            for (UserDTO_In dto : response.body()) {
                                if (dto.getUser_id() == UserInfoHolder.getInstance(getContext()).getUser().getUser_id()) {
                                    continue;
                                }
                                mUserAdapter.add(dto);
                            }
                            mUserAdapter.notifyDataSetChanged();

                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    }
                    default: {
                        RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                        // hendel errorResponse
                        Toast.makeText(getActivity(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, errorResponse.toString());
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    }
                }

                if (!mSwipeRefreshLayout.isRefreshing())
                    showProgress(false);
            }

            @Override
            public void onFailure(Call<List<UserDTO_In>> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());

                if (!mSwipeRefreshLayout.isRefreshing())
                    showProgress(false);
                mSwipeRefreshLayout.setRefreshing(false);
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


    private void showViewDialog(int position) {
        UserDTO_In selectedUser = mUserAdapter.getItem(position);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.user_view_dialog, null);

        ((TextView) view.findViewById(R.id.user_firstName_tv)).setText(selectedUser.getFirstName());
        ((TextView) view.findViewById(R.id.user_lastName_tv)).setText(selectedUser.getLastName());
        ((TextView) view.findViewById(R.id.user_birthDate_tv)).setText(simpleDateFormat.format(selectedUser.getDateOfBirth()));
        setGender(view.findViewById(R.id.user_gender_tv), selectedUser.getGender());

        Button view_patient_details_btn = view.findViewById(R.id.view_patient_details_btn);
        view_patient_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
                Call<PatientDTO> request = userService.getPatientByUserId(selectedUser.getUser_id());
                request.enqueue(new Callback<PatientDTO>() {
                    @Override
                    public void onResponse(Call<PatientDTO> call, Response<PatientDTO> response) {
                        if (response.body() != null) {
                            ViewPatientDatails(response.body());
                        } else
                            view_patient_details_btn.setEnabled(false);
                    }

                    @Override
                    public void onFailure(Call<PatientDTO> call, Throwable t) {

                    }
                });

            }
        });


        Button view_doctor_details_btn = view.findViewById(R.id.view_doctor_details_btn);
        view_doctor_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
                Call<DoctorDTO> request = userService.getDoctorByUserId(selectedUser.getUser_id());
                request.enqueue(new Callback<DoctorDTO>() {
                    @Override
                    public void onResponse(Call<DoctorDTO> call, Response<DoctorDTO> response) {
                        if (response.body() != null) {
                            ViewDoctorDatails(response.body());
                        } else
                            view_doctor_details_btn.setEnabled(false);
                    }

                    @Override
                    public void onFailure(Call<DoctorDTO> call, Throwable t) {

                    }
                });
            }
        });

        Button view_admin_details_btn = view.findViewById(R.id.view_admin_details_btn);
        view_admin_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
                Call<AdminDTO> request = userService.getAdminByUserId(selectedUser.getUser_id());
                request.enqueue(new Callback<AdminDTO>() {
                    @Override
                    public void onResponse(Call<AdminDTO> call, Response<AdminDTO> response) {
                        if (response.body() != null) {
                            ViewAdminDatails(response.body());
                        } else
                            view_admin_details_btn.setEnabled(false);
                    }

                    @Override
                    public void onFailure(Call<AdminDTO> call, Throwable t) {

                    }
                });
            }
        });


        builder.setView(view);

        builder.setNegativeButton(R.string.action_close, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }

    private void ViewAdminDatails(AdminDTO selectedAdmin) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.user_view_dialog, null);

        ((TextView) view.findViewById(R.id.user_firstName_tv)).setText(selectedAdmin.getFirstName());
        ((TextView) view.findViewById(R.id.user_lastName_tv)).setText(selectedAdmin.getLastName());
        ((TextView) view.findViewById(R.id.user_birthDate_tv)).setText(simpleDateFormat.format(selectedAdmin.getDateOfBirth()));
        if (selectedAdmin.isDirector()) {
            ((TextView) view.findViewById(R.id.admin_director_tv)).setText(Boolean.toString(selectedAdmin.isDirector()));
            ((TextView) view.findViewById(R.id.admin_director_tv)).setTextColor(getContext().getResources().getColor(R.color.color_green));

        } else {
            ((TextView) view.findViewById(R.id.admin_director_tv)).setText(Boolean.toString(selectedAdmin.isDirector()));
            ((TextView) view.findViewById(R.id.admin_director_tv)).setTextColor(getContext().getResources().getColor(R.color.color_red));
        }

        setGender(view.findViewById(R.id.user_gender_tv), selectedAdmin.getGender());

        builder.setView(view);

        builder.setNegativeButton(R.string.action_close, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void ViewDoctorDatails(DoctorDTO selectedDoctor) {
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

    private void ViewPatientDatails(PatientDTO selectedPatient) {
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


    private void showDeleteDialog(int position) {
        UserDTO_In selectedUser = mUserAdapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_dialog_title);

        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
                Call<Void> request = userService.delUser(selectedUser.getUser_id());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case 200: {

                                Toast.makeText(getActivity(),
                                        selectedUser.getFirstName() + " " + selectedUser.getLastName() + " User DELETED", Toast.LENGTH_SHORT).show();

                                mUserAdapter.remove(selectedUser);
                                mUserAdapter.notifyDataSetChanged();

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

                    }
                });

            }


        });
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

}
