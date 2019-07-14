package com.pro0inter.heydoc.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.api.DTOs.UserDTO_In;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.UserService;
import com.pro0inter.heydoc.domain.User;
import com.pro0inter.heydoc.utils.UserInfoHolder;
import com.pro0inter.heydoc.utils.Validations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(BIRTH_DATE_FORMAT);
    private final String TAG = "EditProfileActivity";

    private EditText currentEmailEditText, newEmailEditText, curr_passEditText, new_passEditText, firstNameEditText, lastNameEditText, birthDateEditText;
    private RadioGroup genderGroup;

    private View mProgressView;
    private View mFormView;

    private User current_user;
    private FirebaseAuth mAuth;

    private boolean emailAndPasswordAuthenticated=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.action_edit_profile_info));

        currentEmailEditText = findViewById(R.id.EditProfile_currentEmail);
        newEmailEditText = findViewById(R.id.EditProfile_newEmail);
        curr_passEditText = findViewById(R.id.EditProfile_current_password);
        new_passEditText = findViewById(R.id.EditProfile_new_password);
        firstNameEditText = findViewById(R.id.EditProfile_first_name);
        lastNameEditText = findViewById(R.id.EditProfile_last_name);
        genderGroup = findViewById(R.id.EditProfile_Gender_Group);
        initDatePicker();

        mProgressView = findViewById(R.id.EditProfile_progress);
        mFormView = findViewById(R.id.EditProfile_form);


        current_user = UserInfoHolder.getInstance(this).getUser();
        initFildes();

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        final int year = myCalendar.get(Calendar.YEAR);
        final int month = myCalendar.get(Calendar.MONTH);
        final int day = myCalendar.get(Calendar.DAY_OF_MONTH);
        birthDateEditText = findViewById(R.id.EditProfile_birthDate);


        birthDateEditText.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    v.getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, month);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            birthDateEditText.setText(sdf.format(myCalendar.getTime()));
                        }
                    }, year, month, day);
            dialog.show();
        });
    }

    private void initFildes() {

        String pervEmail = mAuth.getCurrentUser().getEmail();
        currentEmailEditText.setText(pervEmail);

        firstNameEditText.setText(current_user.getFirstName());
        lastNameEditText.setText(current_user.getLastName());
        birthDateEditText.setText(sdf.format(current_user.getDateOfBirth()));

        if (current_user.getGender() == 'M')
            genderGroup.check(R.id.EditProfile_Gender_Male);
        else
            genderGroup.check(R.id.EditProfile_Gender_Female);

    }

    public void cancelModifications(View view) {
        goBack();
    }

    public void saveModifications(View view) {
        currentEmailEditText.setError(null);
        newEmailEditText.setError(null);
        curr_passEditText.setError(null);
        new_passEditText.setError(null);

        // Store values at the time of the login attempt.
        String currEmail = currentEmailEditText.getText().toString();
        String newEmail = currentEmailEditText.getText().toString();
        String curr_password = curr_passEditText.getText().toString();
        String new_password = new_passEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(curr_password)) {
            curr_passEditText.setError(getString(R.string.error_field_required));
            focusView = curr_passEditText;
            cancel = true;
        } else //!TextUtils.isEmpty(curr_password)
            if (!Validations.isPasswordValid(curr_password)) {
                curr_passEditText.setError(getString(R.string.error_invalid_password));
                focusView = curr_passEditText;
                cancel = true;
            }

        // Check for a new valid password,
        if (!TextUtils.isEmpty(new_password) && !Validations.isPasswordValid(new_password)) {
            new_passEditText.setError(getString(R.string.error_invalid_password));
            focusView = new_passEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(currEmail)) {
            currentEmailEditText.setError(getString(R.string.error_field_required));
            focusView = currentEmailEditText;
            cancel = true;
        } else if (!Validations.isEmailValid(currEmail)) {
            currentEmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = currentEmailEditText;
            cancel = true;
        }

        // Check for a new valid password,
        if (!TextUtils.isEmpty(new_password) && !Validations.isPasswordValid(new_password)) {
            newEmailEditText.setError(getString(R.string.error_invalid_password));
            focusView = newEmailEditText;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // saving .....

            if (!emailAndPasswordAuthenticated) {
                mAuth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(currEmail, curr_password)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            emailAndPasswordAuthenticated=false;
                        }else{
                            Toast.makeText(EditProfileActivity.this,"Wrong Email or Password",Toast.LENGTH_LONG).show();
                        }

                    }
                });
                return;
            }

            if(!TextUtils.isEmpty(newEmail)){
                mAuth.getCurrentUser().updateEmail(newEmail);
            }
            if (!TextUtils.isEmpty(new_password)) {
                mAuth.getCurrentUser().updatePassword(new_password);
            }



            // check if user info is changed
            UserDTO_In new_user_info = getNewUserInfoIfChanged(current_user);
            boolean info_changed = (new_user_info == null);
            if (!info_changed) {
                goBack();
            }
            // update user info
            UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
            Call<UserDTO_In> request = userService.updateUser(
                    current_user.getUser_id(),
                    new_user_info
            );
            showProgress(true);
            request.enqueue(new Callback<UserDTO_In>() {
                @Override
                public void onResponse(Call<UserDTO_In> call, Response<UserDTO_In> response) {
                    switch (response.code()) {
                        case 200: {
                            showProgress(false);
                            UserDTO_In new_info = response.body();
                            UserInfoHolder.getInstance(EditProfileActivity.this).updateUser(new_info, EditProfileActivity.this);

                            Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            goBack();
                            break;
                        }
                        default: {
                            showProgress(false);
                            RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                            // hendel errorResponse
                            Toast.makeText(EditProfileActivity.this, errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorResponse.toString());
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserDTO_In> call, Throwable t) {
                    Log.d(TAG, t.getLocalizedMessage());
                    showProgress(false);
                }
            });


        }
    }


    private UserDTO_In getNewUserInfoIfChanged(User current_user) {
        UserDTO_In res = new UserDTO_In();
        boolean changed = false;

        res.setUser_id(current_user.getUser_id());

        if (!firstNameEditText.getText().toString().equals(current_user.getFirstName())) {
            res.setFirstName(firstNameEditText.getText().toString());
            if (!changed) changed = true;
        }
        if (!lastNameEditText.getText().toString().equals(current_user.getLastName())) {
            res.setLastName(lastNameEditText.getText().toString());
            if (!changed) changed = true;
        }
        if (!birthDateEditText.getText().toString().equals(sdf.format(current_user.getDateOfBirth()))) {
            try {
                res.setDateOfBirth(sdf.parse(birthDateEditText.getText().toString()));
            } catch (ParseException e) {
                // can't happent
                e.printStackTrace();
            }
            if (!changed) changed = true;
        }

        char editedGender = (genderGroup.getCheckedRadioButtonId() == R.id.EditProfile_Gender_Male) ? 'M' : 'F';

        if (editedGender != current_user.getGender()) {
            res.setGender(editedGender);
            if (!changed) changed = true;
        }

        if (changed) return res;
        else return null;
    }

    private void goBack() {
        startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_account_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editAccountMenu_deleteAccount: {
                showDeleteUserDialog();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteUserDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_user_dialog_title);

        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
                Call<Void> request = userService.delUser(current_user.getUser_id());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case 200: {
                                Toast.makeText(EditProfileActivity.this, "Your Account has been DELETED", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                                break;
                            }
                            default: {
                                RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                                // hendel errorResponse
                                Toast.makeText(EditProfileActivity.this, errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
}
