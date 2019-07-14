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
import com.pro0inter.heydoc.adapters.PatientAdapter;
import com.pro0inter.heydoc.api.DTOs.PatientDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.PatientService;
import com.pro0inter.heydoc.utils.UserInfoHolder;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientManagmentFragment extends Fragment {


    private static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BIRTH_DATE_FORMAT);
    private static final String TAG = "PatientManaFragment";


    private PatientAdapter mPatientAdapter;
    private ProgressBar mProgressBar;


    public PatientManagmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_managment, container, false);

        ListView mListView = view.findViewById(R.id.patients_listview);

        mPatientAdapter = new PatientAdapter(getActivity(), R.layout.patient_row_layout);
        mListView.setAdapter(mPatientAdapter);

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


        mProgressBar = view.findViewById(R.id.loading_patients_list_progress);

        load_patients();

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
                mPatientAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.management_frags_refresh: {
                load_patients();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void load_patients() {

        PatientService patientService = ServiceGenerator.createServiceSecured(PatientService.class);
        Call<List<PatientDTO>> request = patientService.get_all_patients();
        showProgress(true);
        request.enqueue(new Callback<List<PatientDTO>>() {
            @Override
            public void onResponse(Call<List<PatientDTO>> call, Response<List<PatientDTO>> response) {
                switch (response.code()) {
                    case 200: {
                        if (response.body() != null) {
                            mPatientAdapter.clear();
                            for (PatientDTO dto : response.body()) {
                                if (dto.getUser_id() == UserInfoHolder.getInstance(getContext()).getUser().getUser_id()) {
                                    continue;
                                }
                                mPatientAdapter.add(dto);
                            }
                            mPatientAdapter.notifyDataSetChanged();
                        }

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


                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<PatientDTO>> call, Throwable t) {
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


    private void showViewDialog(int position) {
        PatientDTO selectedPatient = mPatientAdapter.getItem(position);

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
        PatientDTO selectedPatient = mPatientAdapter.getItem(position);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_dialog_title);

        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                PatientService patientService = ServiceGenerator.createServiceSecured(PatientService.class);
                Call<Void> request = patientService.delPatient(selectedPatient.getAccount_id());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case 200: {

                                Toast.makeText(getActivity(),
                                        selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + " User DELETED", Toast.LENGTH_SHORT).show();

                                mPatientAdapter.remove(selectedPatient);
                                mPatientAdapter.notifyDataSetChanged();

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
