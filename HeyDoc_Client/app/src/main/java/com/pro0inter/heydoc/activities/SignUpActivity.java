package com.pro0inter.heydoc.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.api.DTOs.UserDTO_In;
import com.pro0inter.heydoc.api.DTOs.UserDTO_SignUp;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.UserService;
import com.pro0inter.heydoc.utils.UserInfoHolder;
import com.pro0inter.heydoc.utils.Validations;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity  {
    private static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
    private final String TAG = "SignUpActivity";

    // UI references.
    private EditText mEmailView,mPasswordView,mFirstNameView, mLastNameView, mBirthDateView;
    private RadioGroup mGender;
    
    private View mProgressView,mFormView;


    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mProgressView = findViewById(R.id.SignUp_progress);
        mFormView=findViewById(R.id.SignUp_form);
        
        mEmailView = findViewById(R.id.SignUp_email);
        mPasswordView = findViewById(R.id.SignUp_password);
        mFirstNameView = findViewById(R.id.SignUp_first_name);
        mLastNameView = findViewById(R.id.SignUp_last_name);
        mGender = findViewById(R.id.SignUp_Gender_Group);
        initDatePicker();
        
        Button mSignUpButton=findViewById(R.id.SignUp_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });
        
        View mGoToSignInActivity=findViewById(R.id.SignUp_go_to_sign_in_link);
        mGoToSignInActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }


    private void initDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        final int year = myCalendar.get(Calendar.YEAR);
        final int month = myCalendar.get(Calendar.MONTH);
        final int day = myCalendar.get(Calendar.DAY_OF_MONTH);
        mBirthDateView = findViewById(R.id.SignUp_birthDate);


        mBirthDateView.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, month);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                            SimpleDateFormat sdf = new SimpleDateFormat(BIRTH_DATE_FORMAT);
                            mBirthDateView.setText(sdf.format(myCalendar.getTime()));
                        }
                    }, year, month, day);
            dialog.show();
        });
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
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


    public void attemptSignUp() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Validations.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Validations.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                SignUpUserToServer(user);
                            } else {
                                showProgress(false);
                                // If sign up fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                // Handel firebase sign up failed
                            }

                            // ...
                        }
                    });

        }
    }

    private void SignUpUserToServer(FirebaseUser user) {

        // Reset errors.
        mFirstNameView.setError(null);
        mFirstNameView.setError(null);
        mBirthDateView.setError(null);


        // Store values at the time of the sign up attempt.
        String first_name = mFirstNameView.getText().toString();
        String last_name = mLastNameView.getText().toString();
        String birthDate = mBirthDateView.getText().toString();
        char gender = ' ';
        switch (mGender.getCheckedRadioButtonId()) {
            case R.id.SignUp_Gender_Male: {
                gender = 'M';
                break;
            }
            case R.id.SignUp_Gender_Female: {
                gender = 'F';
                break;
            }
        }

        boolean cancel = false;
        View focusView = null;

        SimpleDateFormat sdf = new SimpleDateFormat(BIRTH_DATE_FORMAT);
        Date mBirthDate = null;
        try {
            mBirthDate = sdf.parse(birthDate);
        } catch (ParseException e) {
            cancel = true;
            mBirthDateView.setError("Wrong Date format");
            focusView=mBirthDateView;
        }

        if (TextUtils.isEmpty(first_name)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(last_name)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(birthDate)) {
            mBirthDateView.setError(getString(R.string.error_field_required));
            focusView = mBirthDateView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {


            UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
            UserDTO_SignUp user_signup_dto=new UserDTO_SignUp(first_name, last_name, gender, mBirthDate);
            Call<UserDTO_In> request = userService.signupUser(user_signup_dto);
            request.enqueue(new Callback<UserDTO_In>() {
                @Override
                public void onResponse(Call<UserDTO_In> call, Response<UserDTO_In> response) {
                    showProgress(false);
                    switch (response.code()){
                        case 201:{ // CREATED
                            UserDTO_In userDTO_in = response.body();
                            Log.d(TAG, userDTO_in.toString());
                            OnSuccessSignUp(user,userDTO_in);
                            break;
                        }
                        default: {
                            RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                            // handel errorResponse
                            Log.d(TAG, errorResponse.toString());
                            break;
                        }
                    }

                }

                @Override
                public void onFailure(Call<UserDTO_In> call, Throwable t) {
                    Log.d(TAG, t.getLocalizedMessage());
                    showProgress(false);
                    // Handel failed sign up to server
                }
            });


        }
    }

    private void OnSuccessSignUp(FirebaseUser user, UserDTO_In userDTO_in) {
        Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(intent);
    }


}

