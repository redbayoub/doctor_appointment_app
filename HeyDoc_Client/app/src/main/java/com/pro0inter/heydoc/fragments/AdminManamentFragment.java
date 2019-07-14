package com.pro0inter.heydoc.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.adapters.AdminAdapter;
import com.pro0inter.heydoc.adapters.UserAdapter;
import com.pro0inter.heydoc.api.DTOs.AdminDTO;
import com.pro0inter.heydoc.api.DTOs.UserDTO_In;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.AdminService;
import com.pro0inter.heydoc.api.Services.UserService;
import com.pro0inter.heydoc.utils.UserInfoHolder;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminManamentFragment extends Fragment {

    private static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BIRTH_DATE_FORMAT);
    private static final String TAG = "adminsManagmentFragment";


    UserDTO_In selected_user;
    private AdminAdapter mAdminAdapter;
    private ProgressBar mProgressBar;


    public AdminManamentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_manament, container, false);

        FloatingActionButton button = view.findViewById(R.id.add_admin);
        button.setOnClickListener(v -> {
            // Add admin
            showAddDialog();

        });


        ListView mListView = view.findViewById(R.id.admins_listview);

        mAdminAdapter = new AdminAdapter(getActivity(), R.layout.user_row_layout);
        mListView.setAdapter(mAdminAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AdminDTO selectedAdminDTO = mAdminAdapter.getItem(position);
                // show context menu
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.manage_context_menu);
                if (selectedAdminDTO.isDirector())
                    popupMenu.getMenu().findItem(R.id.manage_edit).setTitle(getString(R.string.manage_downgrade));
                else
                    popupMenu.getMenu().findItem(R.id.manage_edit).setTitle(getString(R.string.manage_upgrade));

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.manage_view: {
                                showViewDialog(position);
                                return true;
                            }

                            case R.id.manage_edit: {
                                showUpDownGradeDialog(position);
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


        mProgressBar = view.findViewById(R.id.loading_admins_list_progress);

        load_admins();

        setHasOptionsMenu(true);

        return view;

    }

    private void showAddDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.select_user_dialog, null);

        ListView usersListView = layout.findViewById(R.id.selectUser_list);

        UserAdapter userAdapter = new UserAdapter(getContext(), R.layout.user_row_layout);
        usersListView.setAdapter(userAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                selected_user = userAdapter.getItem(position);
            }
        });

        loadUsers(userAdapter);


        SearchView searchView = layout.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return true;
            }
        });


        builder.setView(layout);

        DialogInterface.OnClickListener addClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (selected_user == null) {
                    Toast.makeText(getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
                } else {
                    showAddConfiramtionDialog(selected_user);
                }
            }
        };


        builder.setPositiveButton(R.string.action_add, addClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void showAddConfiramtionDialog(UserDTO_In selected_user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.add_dialog_message)
                .setTitle(R.string.add_dialog_title);


        builder.setPositiveButton(R.string.action_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AdminService adminService = ServiceGenerator.createServiceSecured(AdminService.class);
                AdminDTO to_add = new AdminDTO();
                to_add.setUser_id(selected_user.getUser_id());
                Call<AdminDTO> request = adminService.add_admin(to_add);
                request.enqueue(new Callback<AdminDTO>() {
                    @Override
                    public void onResponse(Call<AdminDTO> call, Response<AdminDTO> response) {
                        switch (response.code()) {
                            case 200: {
                                AdminDTO res = response.body();

                                Toast.makeText(getActivity(),
                                        res.getFirstName() + " " + res.getLastName() + " User is ADDED as Admin", Toast.LENGTH_LONG).show();

                                mAdminAdapter.add(res);
                                mAdminAdapter.notifyDataSetChanged();

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
                    public void onFailure(Call<AdminDTO> call, Throwable t) {
                        Log.d(TAG, t.toString());
                    }
                });


            }


        });
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void loadUsers(UserAdapter userAdapter) {
        UserService userService = ServiceGenerator.createServiceSecured(UserService.class);
        Call<List<UserDTO_In>> request = userService.get_users();
        request.enqueue(new Callback<List<UserDTO_In>>() {
            @Override
            public void onResponse(Call<List<UserDTO_In>> call, Response<List<UserDTO_In>> response) {
                if (response.body() != null) {
                    long current_user_id = UserInfoHolder.getInstance(getContext()).getUser().getUser_id();
                    for (UserDTO_In dto : response.body()) {
                        if (!dto.getUser_id().equals(current_user_id))
                            userAdapter.add(dto);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO_In>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });


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
                mAdminAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.management_frags_refresh: {
                load_admins();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void load_admins() {
        AdminService adminService = ServiceGenerator.createServiceSecured(AdminService.class);
        Call<List<AdminDTO>> request = adminService.get_all_admins();
        showProgress(true);
        request.enqueue(new Callback<List<AdminDTO>>() {
            @Override
            public void onResponse(Call<List<AdminDTO>> call, Response<List<AdminDTO>> response) {
                switch (response.code()) {
                    case 200: {
                        if (response.body() != null) {
                            mAdminAdapter.clear();
                            for (AdminDTO dto : response.body()) {

                                if (dto.getUser_id() == UserInfoHolder.getInstance(getContext()).getUser().getUser_id()) {
                                    continue;
                                }
                                mAdminAdapter.add(dto);
                            }
                            mAdminAdapter.notifyDataSetChanged();
                        }

                        break;
                    }
                    default: {
                        RestErrorResponse errorResponse = ErrorUtils.parseError(response);
                        // hendel errorResponse
                        Toast.makeText(getContext(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), errorResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, errorResponse.toString());
                        break;
                    }
                }


                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<AdminDTO>> call, Throwable t) {
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
        AdminDTO selectedAdmin = mAdminAdapter.getItem(position);

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

    private void showUpDownGradeDialog(int position) {
        AdminDTO selectedAdmin = mAdminAdapter.getItem(position);
        String positiveActionName = getString(R.string.manage_upgrade);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setMessage(R.string.delete_dialog_message);
        if (selectedAdmin.isDirector()) {
            builder.setTitle(R.string.downgrade_dialog_title);
            positiveActionName = getString(R.string.manage_downgrade);
        } else
            builder.setTitle(R.string.upgrade_dialog_title);


        builder.setPositiveButton(positiveActionName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                AdminService adminService = ServiceGenerator.createServiceSecured(AdminService.class);
                selectedAdmin.setDirector(!selectedAdmin.isDirector());

                Call<AdminDTO> request = adminService.update_admin(selectedAdmin.getAccount_id(), selectedAdmin);
                request.enqueue(new Callback<AdminDTO>() {
                    @Override
                    public void onResponse(Call<AdminDTO> call, Response<AdminDTO> response) {
                        switch (response.code()) {
                            case 200: {

                                AdminDTO responseDto = response.body();
                                mAdminAdapter.remove(selectedAdmin);
                                mAdminAdapter.insert(responseDto, position);
                                mAdminAdapter.notifyDataSetChanged();

                                Toast.makeText(getActivity(), responseDto.getFirstName() + " " + responseDto.getLastName() + " Admin Updated", Toast.LENGTH_SHORT).show();
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
                    public void onFailure(Call<AdminDTO> call, Throwable t) {

                    }
                });

            }


        });
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }


    private void showDeleteDialog(int position) {
        AdminDTO selectedAdmin = mAdminAdapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_dialog_title);


        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                AdminService adminService = ServiceGenerator.createServiceSecured(AdminService.class);
                Call<Void> request = adminService.del_admin(selectedAdmin.getAccount_id());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case 200: {

                                Toast.makeText(getActivity(),
                                        selectedAdmin.getFirstName() + " " + selectedAdmin.getLastName() + " User DELETED", Toast.LENGTH_SHORT).show();

                                mAdminAdapter.remove(selectedAdmin);
                                mAdminAdapter.notifyDataSetChanged();

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
