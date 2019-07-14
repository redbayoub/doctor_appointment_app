package com.pro0inter.heydoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.domain.User;
import com.pro0inter.heydoc.fragments.AdminManamentFragment;
import com.pro0inter.heydoc.fragments.DocServiceManagmentFragment;
import com.pro0inter.heydoc.fragments.DoctorAppointmentListFragment;
import com.pro0inter.heydoc.fragments.DoctorManagmentFragment;
import com.pro0inter.heydoc.fragments.EditDoctorAccountFragment;
import com.pro0inter.heydoc.fragments.EditPatientAccountFragment;
import com.pro0inter.heydoc.fragments.PatientAppointmentListFragment;
import com.pro0inter.heydoc.fragments.PatientManagmentFragment;
import com.pro0inter.heydoc.fragments.PatientRegistrationFragment;
import com.pro0inter.heydoc.fragments.RequestDoctorAccountFragment;
import com.pro0inter.heydoc.fragments.SpecialitiesManagmentFragment;
import com.pro0inter.heydoc.fragments.UsersManagmentFragment;
import com.pro0inter.heydoc.fragments.interfaces.ViewPervFragment;
import com.pro0inter.heydoc.utils.Roles;
import com.pro0inter.heydoc.utils.UserInfoHolder;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPervFragment {

    private DrawerLayout mDrawerLayout;
    private View mNavHeader;
    private Menu mNavMenu;


    private Fragment currFragment, pervFragment,
            editPatientAccountFragment, doctorAppointmentListFragment,
            adminMangmentFregment, docServiceMangmentFragment,
            doctorManagmentFragment, patientAppointmentListFragment,
            patientManagmentFragment, requestDoctorAccountFragment,
            specialtiesMangmentFregment, usersMangmentFragment;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setTitle(R.string.app_name);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mNavHeader = navigationView.getHeaderView(0);

        mNavMenu = navigationView.getMenu();


        AppCompatImageButton edit_profile_btn = mNavHeader.findViewById(R.id.edit_profile_btn);
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), EditProfileActivity.class));
            }
        });

        // open Drawer on create
        mDrawerLayout.openDrawer(GravityCompat.START);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    private void load_fragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();


        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            if (UserInfoHolder.getInstance(this).getUser() == null) {
                UserInfoHolder.init(currentUser, this, () -> { // onfinishd
                    updateNav();

                    if (currFragment != null)
                        load_fragment(currFragment);

                    initNavMenu();
                });
            } else {
                updateNav();

                if (currFragment != null)
                    load_fragment(currFragment);

                initNavMenu();
            }
        }
    }

    private void updateNav() {
        User curr_user = UserInfoHolder.getInstance(this).getUser();
        TextView fullusername = mNavHeader.findViewById(R.id.full_username_tv);
        fullusername.setText(curr_user.getFirstName() + " " + curr_user.getLastName());
/*
        Todo enable after fixing FileUploadContoller in Server
        if(mAuth.getCurrentUser().getPhotoUrl()!=null){
            ImageView userAvatar=mNavHeader.findViewById(R.id.user_avatar);
            Picasso.get()
                    .load(mAuth.getCurrentUser().getPhotoUrl())
                    .into(userAvatar);
        }
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout_mi: {
                UserInfoHolder.clearPreferences(this);
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            }

            case R.id.management_frags_search: {
                return false;
            }

        }


        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, item.getTitle().toString() + " selected", Toast.LENGTH_SHORT).show();
        getSupportActionBar().setTitle(item.getTitle());
        pervFragment = currFragment;
        switch (item.getItemId()) {
            case R.id.patient_create_account: {
                PatientRegistrationFragment patientRegistrationFragment = new PatientRegistrationFragment();
                currFragment = patientRegistrationFragment;
                load_fragment(patientRegistrationFragment);
                break;
            }
            case R.id.patient_appointment_list: {
                if (patientAppointmentListFragment == null)
                    patientAppointmentListFragment = new PatientAppointmentListFragment();
                currFragment = patientAppointmentListFragment;
                load_fragment(patientAppointmentListFragment);
                break;
            }

            case R.id.patient_edit_account_info: {
                if (editPatientAccountFragment == null)
                    editPatientAccountFragment = new EditPatientAccountFragment();
                currFragment = editPatientAccountFragment;
                load_fragment(editPatientAccountFragment);
                break;
            }

            case R.id.doctor_request_account: {
                if (requestDoctorAccountFragment == null)
                    requestDoctorAccountFragment = new RequestDoctorAccountFragment();
                currFragment = requestDoctorAccountFragment;
                load_fragment(requestDoctorAccountFragment);
                break;
            }

            case R.id.doctor_appointment_list: {
                if (doctorAppointmentListFragment == null)
                    doctorAppointmentListFragment = new DoctorAppointmentListFragment();
                currFragment = doctorAppointmentListFragment;
                load_fragment(doctorAppointmentListFragment);
                break;
            }

            case R.id.doctor_edit_account_info: {
                currFragment = new EditDoctorAccountFragment();
                load_fragment(currFragment);
                break;
            }


            case R.id.admin_manage_users: {
                if (usersMangmentFragment == null)
                    usersMangmentFragment = new UsersManagmentFragment();
                currFragment = usersMangmentFragment;
                load_fragment(usersMangmentFragment);
                break;
            }
            case R.id.admin_manage_patients: {
                if (patientManagmentFragment == null)
                    patientManagmentFragment = new PatientManagmentFragment();
                currFragment = patientManagmentFragment;
                load_fragment(patientManagmentFragment);
                break;
            }
            case R.id.admin_manage_doctors: {
                if (doctorManagmentFragment == null)
                    doctorManagmentFragment = new DoctorManagmentFragment();
                currFragment = doctorManagmentFragment;
                load_fragment(doctorManagmentFragment);
                break;
            }
            case R.id.admin_manage_doc_services: {
                if (docServiceMangmentFragment == null)
                    docServiceMangmentFragment = new DocServiceManagmentFragment();
                currFragment = docServiceMangmentFragment;
                load_fragment(docServiceMangmentFragment);
                break;
            }
            case R.id.admin_manage_specialities: {
                if (specialtiesMangmentFregment == null)
                    specialtiesMangmentFregment = new SpecialitiesManagmentFragment();
                currFragment = specialtiesMangmentFregment;
                load_fragment(specialtiesMangmentFregment);
                break;
            }
            case R.id.admin_manage_admin: {
                if (adminMangmentFregment == null)
                    adminMangmentFregment = new AdminManamentFragment();
                currFragment = adminMangmentFregment;
                load_fragment(adminMangmentFregment);
                break;
            }

        }


        mDrawerLayout.closeDrawer(GravityCompat.START);


        return true;
    }

    @Override
    public void viewPervFragment() {
        if (pervFragment != null)
            load_fragment(pervFragment);
    }


    public void initNavMenu() {
        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {

                    Map<String, Object> claims = task.getResult().getClaims();

                    if (!claims.containsKey(Roles.PATIENT) || Boolean.FALSE.equals(claims.get(Roles.PATIENT))) {
                        mNavMenu.findItem(R.id.patient_create_account).setVisible(Boolean.TRUE);
                        mNavMenu.findItem(R.id.patient_appointment_list).setVisible(Boolean.FALSE);
                        mNavMenu.findItem(R.id.patient_edit_account_info).setVisible(Boolean.FALSE);
                    } else {
                        mNavMenu.findItem(R.id.patient_create_account).setVisible(Boolean.FALSE);
                        mNavMenu.findItem(R.id.patient_appointment_list).setVisible(Boolean.TRUE);
                        mNavMenu.findItem(R.id.patient_edit_account_info).setVisible(Boolean.TRUE);
                    }
                    if (!claims.containsKey(Roles.NOT_APPROVED_DOCTOR) && !claims.containsKey(Roles.APPROVED_DOCTOR)    || (Boolean.FALSE.equals(claims.get(Roles.NOT_APPROVED_DOCTOR)) && Boolean.FALSE.equals(claims.get(Roles.APPROVED_DOCTOR)))) {
                        mNavMenu.findItem(R.id.doctor_request_account).setVisible(Boolean.TRUE);
                        mNavMenu.findItem(R.id.doctor_appointment_list).setVisible(Boolean.FALSE);
                        mNavMenu.findItem(R.id.doctor_edit_account_info).setVisible(Boolean.FALSE);

                    }
                    if (claims.containsKey(Roles.NOT_APPROVED_DOCTOR) && Boolean.TRUE.equals(claims.get(Roles.NOT_APPROVED_DOCTOR))) {
                        mNavMenu.findItem(R.id.doctor_options).setVisible(Boolean.FALSE);
                    }

                    if (claims.containsKey(Roles.APPROVED_DOCTOR) && Boolean.TRUE.equals(claims.get(Roles.APPROVED_DOCTOR))) {
                        mNavMenu.findItem(R.id.doctor_request_account).setVisible(Boolean.FALSE);
                    }

                    if (!claims.containsKey(Roles.ADMIN) || Boolean.FALSE.equals(claims.get(Roles.ADMIN))) {
                        mNavMenu.findItem(R.id.admin_options).setVisible(Boolean.FALSE);
                    }
                    if (!claims.containsKey(Roles.DIRECTOR) || Boolean.FALSE.equals(claims.get(Roles.DIRECTOR))) {
                        mNavMenu.findItem(R.id.director_options).setVisible(Boolean.FALSE);
                    }

                } else {
                    // handel failed get id token
                }

            }
        });

    }
}
