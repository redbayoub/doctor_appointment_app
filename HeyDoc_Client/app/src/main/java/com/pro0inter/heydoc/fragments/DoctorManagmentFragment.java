package com.pro0inter.heydoc.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.adapters.DoctorAdapter;
import com.pro0inter.heydoc.adapters.DoctorServiceAdapter;
import com.pro0inter.heydoc.adapters.SpecialityAdapter;
import com.pro0inter.heydoc.adapters.WorkingScheduleAdapter;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO;
import com.pro0inter.heydoc.api.DTOs.DoctorDTO_update;
import com.pro0inter.heydoc.api.DTOs.DoctorServiceDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.DoctorService;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorManagmentFragment extends Fragment {

    private static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BIRTH_DATE_FORMAT);
    private static final String TAG = "DoctorManaFragment";


    private DoctorAdapter mDoctorAdapter;
    private ProgressBar mProgressBar;


    public DoctorManagmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_doctor_managment, container, false);

        ListView mListView = layout.findViewById(R.id.doctors_listview);

        mDoctorAdapter = new DoctorAdapter(getActivity(), R.layout.doctor_row_layout);
        mListView.setAdapter(mDoctorAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // show context menu
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.manage_context_menu);

                popupMenu.getMenu().findItem(R.id.manage_edit).setTitle(getResources().getString(R.string.approve));
                DoctorDTO selectedDoctor = mDoctorAdapter.getItem(position);
                if (selectedDoctor.isApproved()) {
                    popupMenu.getMenu().findItem(R.id.manage_edit).setTitle(getResources().getString(R.string.disapprove));
                }


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.manage_view: {
                                showViewDialog(position);
                                return true;
                            }
                            case R.id.manage_edit: { // approve or disapprove
                                if (selectedDoctor.isApproved()) {
                                    showDisApproveDialog(position);
                                } else {
                                    showApproveDialog(position);
                                }
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


        mProgressBar = layout.findViewById(R.id.loading_doctors_list_progress);

        load_doctors();

        setHasOptionsMenu(true);

        return layout;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.management_fregments_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.management_frags_search).getActionView();
        setupSearchView(searchView);

        super.onCreateOptionsMenu(menu, inflater);
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
                mDoctorAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.management_frags_refresh: {
                load_doctors();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void load_doctors() {
        DoctorService doctorService = ServiceGenerator.createService(DoctorService.class);
        Call<List<DoctorDTO>> request = doctorService.get_all();
        request.enqueue(new Callback<List<DoctorDTO>>() {
            @Override
            public void onResponse(Call<List<DoctorDTO>> call, Response<List<DoctorDTO>> response) {
                if (response.body() != null) {
                    mDoctorAdapter.clear();
                    for (DoctorDTO dto : response.body()) {

                        mDoctorAdapter.add(dto);
                    }
                    mDoctorAdapter.notifyDataSetChanged();
                }

                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<DoctorDTO>> call, Throwable t) {
                Log.d(TAG, t.toString());
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


    private void showViewDialog(int position) {
        DoctorDTO selectedDoctor = mDoctorAdapter.getItem(position);

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


    private void showApproveDialog(int position) {
        DoctorDTO selectedDoctor = mDoctorAdapter.getItem(position);

        DoctorDTO_update new_info = new DoctorDTO_update();
        new_info.setAccount_id(selectedDoctor.getAccount_id());
        new_info.setApproved(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.approve_dialog_message)
                .setTitle(R.string.approve_dialog_title);

        builder.setPositiveButton(R.string.approve, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DoctorService doctorService = ServiceGenerator.createServiceSecured(DoctorService.class);
                Call<DoctorDTO> request = doctorService.updateDoctor(new_info.getAccount_id(), new_info);
                request.enqueue(new Callback<DoctorDTO>() {
                    @Override
                    public void onResponse(Call<DoctorDTO> call, Response<DoctorDTO> response) {
                        switch (response.code()) {
                            case 200: {

                                Toast.makeText(getActivity(),
                                        selectedDoctor.getFirstName() + " " + selectedDoctor.getLastName() + " User APPROVED", Toast.LENGTH_SHORT).show();

                                mDoctorAdapter.remove(selectedDoctor);
                                mDoctorAdapter.insert(response.body(), position);
                                mDoctorAdapter.notifyDataSetChanged();

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
                    public void onFailure(Call<DoctorDTO> call, Throwable t) {

                    }
                });

            }


        });
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }


    private void showDisApproveDialog(int position) {
        DoctorDTO selectedDoctor = mDoctorAdapter.getItem(position);

        DoctorDTO_update new_info = new DoctorDTO_update();
        new_info.setAccount_id(selectedDoctor.getAccount_id());
        new_info.setApproved(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.disapprove_dialog_message)
                .setTitle(R.string.disapprove_dialog_title);

        builder.setPositiveButton(R.string.disapprove, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DoctorService doctorService = ServiceGenerator.createServiceSecured(DoctorService.class);
                Call<DoctorDTO> request = doctorService.updateDoctor(new_info.getAccount_id(), new_info);
                request.enqueue(new Callback<DoctorDTO>() {
                    @Override
                    public void onResponse(Call<DoctorDTO> call, Response<DoctorDTO> response) {
                        switch (response.code()) {
                            case 200: {

                                Toast.makeText(getActivity(),
                                        selectedDoctor.getFirstName() + " " + selectedDoctor.getLastName() + " User DIS-APPROVED", Toast.LENGTH_SHORT).show();

                                mDoctorAdapter.remove(selectedDoctor);
                                mDoctorAdapter.insert(response.body(), position);
                                mDoctorAdapter.notifyDataSetChanged();

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
                    public void onFailure(Call<DoctorDTO> call, Throwable t) {

                    }
                });

            }


        });
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }


    private void showDeleteDialog(int position) {
        DoctorDTO selectedDoctor = mDoctorAdapter.getItem(position);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_dialog_title);

        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                DoctorService doctorService = ServiceGenerator.createServiceSecured(DoctorService.class);
                Call<Void> request = doctorService.delDoctor(selectedDoctor.getAccount_id());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case 200: {

                                Toast.makeText(getActivity(),
                                        selectedDoctor.getFirstName() + " " + selectedDoctor.getLastName() + " User DELETED", Toast.LENGTH_SHORT).show();

                                mDoctorAdapter.remove(selectedDoctor);
                                mDoctorAdapter.notifyDataSetChanged();

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
