package com.pro0inter.heydoc.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.activities.MainActivity;
import com.pro0inter.heydoc.api.DTOs.PatientDTO;
import com.pro0inter.heydoc.api.DTOs.UserDTO_In;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.PatientService;
import com.pro0inter.heydoc.utils.UserInfoHolder;
import com.pro0inter.heydoc.utils.Validations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientRegistrationFragment extends Fragment {

    private final static String TAG = "PatientRegistration";


    // UI references.
    private EditText mContactPhNumberView, mEmergencyContactPhNumberView;
    private Spinner mBloodTypeView;
    private ArrayAdapter<CharSequence> blood_types_adapter;
    private View mFormView,mProgressView;

    public PatientRegistrationFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_registration, container, false);


        mContactPhNumberView = view.findViewById(R.id.PatientRegistraion_contact_ph);

        mEmergencyContactPhNumberView = view.findViewById(R.id.PatientRegistraion_emergency_ph);

        mBloodTypeView = view.findViewById(R.id.PatientRegistraion_blood_type);
        blood_types_adapter = ArrayAdapter.createFromResource(getContext(), R.array.blood_types, android.R.layout.simple_spinner_item);
        blood_types_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBloodTypeView.setAdapter(blood_types_adapter);

        mFormView=view.findViewById(R.id.PatientRegistraion_form);
        mProgressView=view.findViewById(R.id.PatientRegistraion_progress);

        Button mAddPatientButton = view.findViewById(R.id.PatientRegistraion_add_patient_account_btn);
        mAddPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAddPatientAccount();
            }
        });


        return view;
    }

    private void attemptAddPatientAccount() {

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
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
             /*
                //Todo send verify_phone_number(ph_number)
                verify_phone_number(contact_ph_number);*/


            PatientService patientService = ServiceGenerator.createServiceSecured(PatientService.class);
            PatientDTO request_body = new PatientDTO(
                    UserInfoHolder.getInstance(getContext()).getUser().getUser_id(),
                    blood_type,
                    contact_ph_number,
                    emergency_ph_number);
            Log.d(TAG, request_body.toString());

            Call<PatientDTO> request = patientService.add_patient(request_body);
            showProgress(true);
            request.enqueue(new Callback<PatientDTO>() {
                @Override
                public void onResponse(Call<PatientDTO> call, Response<PatientDTO> response) {
                    showProgress(false);
                    switch (response.code()) {
                        case 201: { // CREATED
                            Log.d(TAG, response.body().toString());
                            updateUI(response.body());
                            break;
                        }
                        default: {

                            updateUI(null);
                            break;
                        }
                    }

                }

                @Override
                public void onFailure(Call<PatientDTO> call, Throwable t) {
                    Log.d(TAG, t.getLocalizedMessage());
                    showProgress(false);
                }
            });


        }
    }


    private void updateUI(PatientDTO patientDTO) {
        if (patientDTO != null) {

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



}
