package com.pro0inter.heydoc.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.adapters.SpecialityAdapter;
import com.pro0inter.heydoc.api.DTOs.SpecialityDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.SpecialityService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpecialitiesManagmentFragment extends Fragment {

    private static final String TAG = "SpecialitiesManFragment";


    private SpecialityAdapter mSpecialityAdapter;
    private ProgressBar mProgressBar;


    public SpecialitiesManagmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spiecialities_managment, container, false);

        FloatingActionButton button = view.findViewById(R.id.add_spieciality);
        button.setOnClickListener(v -> {
            // Add speciality
            showAddDialog();

        });

        ListView mListView = view.findViewById(R.id.spiecialities_listview);

        mSpecialityAdapter = new SpecialityAdapter(getActivity(), R.layout.speciality_row_layout);
        mListView.setAdapter(mSpecialityAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // show context menu
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.manage_context_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.manage_view: {
                                showViewDialog(position);
                                return true;
                            }
                            case R.id.manage_edit: {
                                showEditDialog(position);
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


        mProgressBar = view.findViewById(R.id.loading_spiecialities_list_progress);

        load_specialities();
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

    private void setupSearchView(SearchView searchView) {
        searchView.setQueryHint(getString(R.string.type_to_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSpecialityAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.management_frags_refresh: {
                load_specialities();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void load_specialities() {
        SpecialityService specialityService = ServiceGenerator.createService(SpecialityService.class);
        Call<ArrayList<SpecialityDTO>> request = specialityService.get_specialities();
        showProgress(true);
        request.enqueue(new Callback<ArrayList<SpecialityDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<SpecialityDTO>> call, Response<ArrayList<SpecialityDTO>> response) {
                if (response.body() != null) {
                    mSpecialityAdapter.clear();

                    for (SpecialityDTO dto : response.body()) {

                        mSpecialityAdapter.add(dto);
                    }
                    mSpecialityAdapter.notifyDataSetChanged();
                }

                showProgress(false);
            }

            @Override
            public void onFailure(Call<ArrayList<SpecialityDTO>> call, Throwable t) {
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


    private void showAddDialog() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.speciality_add_edit_dialog, null);

        ((TextView) view.findViewById(R.id.speciality_dialog_title)).setText(R.string.title_add_speciality);
        builder.setView(view);

        DialogInterface.OnClickListener addClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = ((EditText) view.findViewById(R.id.speciality_title_et)).getText().toString();
                String description = ((EditText) view.findViewById(R.id.speciality_description_et)).getText().toString();

                SpecialityDTO specialityDTO = new SpecialityDTO(title, description);
                SpecialityService specialityService = ServiceGenerator.createServiceSecured(SpecialityService.class);
                Call<SpecialityDTO> request = specialityService.add_Speciality(specialityDTO);
                request.enqueue(new Callback<SpecialityDTO>() {
                    @Override
                    public void onResponse(Call<SpecialityDTO> call, Response<SpecialityDTO> response) {
                        switch (response.code()) {
                            case 201: {
                                SpecialityDTO responseDto = response.body();
                                mSpecialityAdapter.add(responseDto);
                                Toast.makeText(getActivity(), responseDto.getTitle() + "Speciality ADDED", Toast.LENGTH_SHORT).show();
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
                    public void onFailure(Call<SpecialityDTO> call, Throwable t) {

                    }
                });

            }
        };


        builder.setPositiveButton(R.string.action_add, addClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }


    private void showViewDialog(int position) {
        SpecialityDTO selectedSpeciality = mSpecialityAdapter.getItem(position);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.speciality_view_dialog, null);

        ((TextView) view.findViewById(R.id.speciality_title_tv)).setText(selectedSpeciality.getTitle());

        TextView description_tv = view.findViewById(R.id.speciality_description_tv);
        if (selectedSpeciality.getDescription() == null || selectedSpeciality.getDescription().trim().isEmpty()) {
            description_tv.setText(getString(R.string.no_description));

        } else
            description_tv.setText(selectedSpeciality.getDescription());

        builder.setView(view);

        builder.setNegativeButton(R.string.action_close, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }


    private void showEditDialog(int position) {
        SpecialityDTO selectedSpeciality = mSpecialityAdapter.getItem(position);


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.speciality_add_edit_dialog, null);

        ((TextView) view.findViewById(R.id.speciality_dialog_title)).setText(R.string.title_edit_speciality);

        EditText speciality_title_et = view.findViewById(R.id.speciality_title_et);
        speciality_title_et.setText(selectedSpeciality.getTitle());

        EditText speciality_description_et = view.findViewById(R.id.speciality_description_et);
        speciality_description_et.setText(selectedSpeciality.getDescription());

        builder.setView(view);

        DialogInterface.OnClickListener editClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = speciality_title_et.getText().toString();
                String description = speciality_description_et.getText().toString();

                selectedSpeciality.setTitle(title);
                selectedSpeciality.setDescription(description);

                SpecialityService specialityService = ServiceGenerator.createServiceSecured(SpecialityService.class);
                Call<SpecialityDTO> request = specialityService.update_Speciality(selectedSpeciality.getId(), selectedSpeciality);
                request.enqueue(new Callback<SpecialityDTO>() {
                    @Override
                    public void onResponse(Call<SpecialityDTO> call, Response<SpecialityDTO> response) {
                        switch (response.code()) {
                            case 200: {
                                SpecialityDTO responseDto = response.body();
                                mSpecialityAdapter.remove(selectedSpeciality);
                                mSpecialityAdapter.insert(responseDto, position);
                                mSpecialityAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), responseDto.getTitle() + "Speciality EDITED", Toast.LENGTH_SHORT).show();
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
                    public void onFailure(Call<SpecialityDTO> call, Throwable t) {

                    }
                });

            }

        };


        builder.setPositiveButton(R.string.action_edit, editClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }


    private void showDeleteDialog(int position) {
        SpecialityDTO selectedSpeciality = mSpecialityAdapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_dialog_title);

        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                SpecialityService specialityService = ServiceGenerator.createServiceSecured(SpecialityService.class);
                Call<Void> request = specialityService.delete_Speciality(selectedSpeciality.getId());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case 200: {

                                Toast.makeText(getActivity(), selectedSpeciality.getTitle() + " Speciality DELETED", Toast.LENGTH_SHORT).show();

                                mSpecialityAdapter.remove(selectedSpeciality);
                                mSpecialityAdapter.notifyDataSetChanged();

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
