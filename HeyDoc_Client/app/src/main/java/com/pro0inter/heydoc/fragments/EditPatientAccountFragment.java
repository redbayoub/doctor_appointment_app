package com.pro0inter.heydoc.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.activities.MainActivity;
import com.pro0inter.heydoc.api.DTOs.PatientDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.PatientService;
import com.pro0inter.heydoc.domain.Patient;
import com.pro0inter.heydoc.fragments.interfaces.ViewPervFragment;
import com.pro0inter.heydoc.utils.UserInfoHolder;
import com.pro0inter.heydoc.utils.Validations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPatientAccountFragment extends Fragment {


    private static final String TAG = "EditPatientAccountFrag";
    // UI references.
    private EditText mContactPhNumberView, mEmergencyContactPhNumberView;
    private Spinner mBloodTypeView;
    private View mProgressView, mPatientEditForm;

    private PatientDTO currPatient;
    private ArrayAdapter<CharSequence> blood_types_adapter;


    public EditPatientAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_edit_patient_account, container, false);

        mContactPhNumberView = layout.findViewById(R.id.PatientEdit_contact_ph);

        mEmergencyContactPhNumberView = layout.findViewById(R.id.PatientEdit_emergency_ph);

        mBloodTypeView = layout.findViewById(R.id.PatientEdit_blood_type);
        blood_types_adapter = ArrayAdapter.createFromResource(getContext(), R.array.blood_types,
                android.R.layout.simple_spinner_item);
        blood_types_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBloodTypeView.setAdapter(blood_types_adapter);

        mProgressView = layout.findViewById(R.id.PatientEdit_progress);
        mPatientEditForm = layout.findViewById(R.id.PatientEdit_form);

        layout.findViewById(R.id.PatientEdit_save_modifications_btn).setOnClickListener(this::saveModifications);
        layout.findViewById(R.id.PatientEdit_cancel_modifications_btn).setOnClickListener(this::cancelModifications);

        currPatient = patient_to_patientDTO(UserInfoHolder.getInstance(getContext()).getPatient());

        initUI(currPatient);
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
                showDeletePatientDialog();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeletePatientDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_account_dialog_title);

        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                PatientService patientService = ServiceGenerator.createServiceSecured(PatientService.class);
                Call<Void> request = patientService.delPatient(currPatient.getAccount_id());
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


    private void initUI(PatientDTO currPatient) {
        mContactPhNumberView.setText(currPatient.getContact_phone_number());
        mEmergencyContactPhNumberView.setText(currPatient.getEmergency_contact_phone_number());
        mBloodTypeView.setSelection(blood_types_adapter.getPosition(currPatient.getBlood_type()));
    }

    private PatientDTO patient_to_patientDTO(Patient patient) {
        return new PatientDTO(
                patient.getPatient_id(),
                UserInfoHolder.getInstance(getContext()).getUser().getUser_id(),
                patient.getBlood_type(),
                patient.getContact_phone_number(),
                patient.getEmergency_contact_phone_number());
    }


    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPatientEditForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mPatientEditForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPatientEditForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mPatientEditForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void cancelModifications(View view) {
        ((ViewPervFragment) getActivity()).viewPervFragment();
    }

    public void saveModifications(View view) {
        if (!validate()) {
            return;
        }
        PatientDTO newPatientDTO = new PatientDTO(
                currPatient.getAccount_id(),
                UserInfoHolder.getInstance(getContext()).getUser().getUser_id(),
                blood_types_adapter.getItem(mBloodTypeView.getSelectedItemPosition()).toString(),
                mContactPhNumberView.getText().toString(),
                mEmergencyContactPhNumberView.getText().toString());

        PatientService patientService = ServiceGenerator.createServiceSecured(PatientService.class);
        Call<PatientDTO> request = patientService.update_patient(
                currPatient.getAccount_id(),
                newPatientDTO
        );
        showProgress(true);
        request.enqueue(new Callback<PatientDTO>() {
            @Override
            public void onResponse(Call<PatientDTO> call, Response<PatientDTO> response) {
                switch (response.code()) {
                    case 200: { // OK
                        PatientDTO patientDTO = response.body();
                        UserInfoHolder.getInstance(getContext()).updatePatient(patientDTO, getContext());
                        Toast.makeText(getContext(), "Patient Info has been Updated", Toast.LENGTH_SHORT).show();
                        ((ViewPervFragment) getActivity()).viewPervFragment();
                        showProgress(false);
                        break;
                    }
                    default: {
                        RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                        // hendel errorResponse
                        Toast.makeText(getContext(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, errorResponse.toString());
                        showProgress(false);
                        break;
                    }

                }
            }

            @Override
            public void onFailure(Call<PatientDTO> call, Throwable t) {
                Log.d(TAG, t.toString());
                showProgress(false);
            }
        });


    }

    private boolean validate() {

        // Reset errors.
        mContactPhNumberView.setError(null);
        mEmergencyContactPhNumberView.setError(null);

        // Store values at the time of the login attempt.
        String contact_ph_number = mContactPhNumberView.getText().toString();
        String emergency_ph_number = mEmergencyContactPhNumberView.getText().toString();
        String blood_type = blood_types_adapter.getItem(mBloodTypeView.getSelectedItemPosition()).toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(contact_ph_number) || !Validations.isPhoneNumberValid(contact_ph_number)) {
            mContactPhNumberView.setError(getString(R.string.error_field_required));
            focusView = mContactPhNumberView;
            cancel = true;
        }
        if (TextUtils.isEmpty(emergency_ph_number) || !Validations.isPhoneNumberValid(emergency_ph_number)) {
            mEmergencyContactPhNumberView.setError(getString(R.string.error_field_required));
            focusView = mEmergencyContactPhNumberView;
            cancel = true;
        }
        if (TextUtils.isEmpty(blood_type) || !Validations.isBloodTypeValid(blood_type)) {
            Toast.makeText(getContext(), R.string.prompt_blood_type, Toast.LENGTH_SHORT).show();
            focusView = mBloodTypeView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
             /*
                //Todo verify_phone_number(ph_number)
                verify_phone_number(contact_ph_number);*/
            return true;
        }
    }

}
