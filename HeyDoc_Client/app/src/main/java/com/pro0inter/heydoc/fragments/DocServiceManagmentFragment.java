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
import com.pro0inter.heydoc.adapters.DocServiceAdapter;
import com.pro0inter.heydoc.api.DTOs.DocServiceDTO;
import com.pro0inter.heydoc.api.Error.ErrorUtils;
import com.pro0inter.heydoc.api.Error.RestErrorResponse;
import com.pro0inter.heydoc.api.ServiceGenerator;
import com.pro0inter.heydoc.api.Services.DocService_Service;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocServiceManagmentFragment extends Fragment {

    private static final String TAG = "DocServicesManFragment";


    private DocServiceAdapter mDocServiceAdapter;
    private ProgressBar mProgressBar;

    public DocServiceManagmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doc_service_managment, container, false);

        FloatingActionButton button = view.findViewById(R.id.add_doc_service);
        button.setOnClickListener(v -> {
            // Add speciality
            showAddDialog();

        });

        ListView mListView = view.findViewById(R.id.doc_services_listview);

        mDocServiceAdapter = new DocServiceAdapter(getActivity(), R.layout.doc_service_row_layout);
        mListView.setAdapter(mDocServiceAdapter);

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


        mProgressBar = view.findViewById(R.id.loading_doc_services_list_progress);

        load_doc_services();

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
                mDocServiceAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.management_frags_refresh: {
                load_doc_services();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void load_doc_services() {
        DocService_Service docService_service = ServiceGenerator.createService(DocService_Service.class);
        Call<ArrayList<DocServiceDTO>> request = docService_service.get_docservices();
        showProgress(true);
        request.enqueue(new Callback<ArrayList<DocServiceDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<DocServiceDTO>> call, Response<ArrayList<DocServiceDTO>> response) {
                if (response.body() != null) {
                    mDocServiceAdapter.clear();
                    for (DocServiceDTO dto : response.body()) {

                        mDocServiceAdapter.add(dto);
                    }
                    mDocServiceAdapter.notifyDataSetChanged();
                }

                showProgress(false);
            }

            @Override
            public void onFailure(Call<ArrayList<DocServiceDTO>> call, Throwable t) {
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
        View view = inflater.inflate(R.layout.doc_service_add_edit_dialog, null);

        ((TextView) view.findViewById(R.id.doc_service_dialog_title)).setText(R.string.title_add_doc_service);
        builder.setView(view);

        DialogInterface.OnClickListener addClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = ((EditText) view.findViewById(R.id.doc_service_title_et)).getText().toString();
                String description = ((EditText) view.findViewById(R.id.doc_service_description_et)).getText().toString();

                DocServiceDTO docServiceDTO = new DocServiceDTO(title, description);
                DocService_Service docService_service = ServiceGenerator.createServiceSecured(DocService_Service.class);
                Call<DocServiceDTO> request = docService_service.add_Doc_service(docServiceDTO);
                request.enqueue(new Callback<DocServiceDTO>() {
                    @Override
                    public void onResponse(Call<DocServiceDTO> call, Response<DocServiceDTO> response) {
                        switch (response.code()) {
                            case 201: {
                                DocServiceDTO responseDto = response.body();
                                mDocServiceAdapter.add(responseDto);
                                Toast.makeText(getActivity(), responseDto.getTitle() + "Doctor Service ADDED", Toast.LENGTH_SHORT).show();
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
                    public void onFailure(Call<DocServiceDTO> call, Throwable t) {

                    }
                });

            }
        };


        builder.setPositiveButton(R.string.action_add, addClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();
    }


    private void showViewDialog(int position) {
        DocServiceDTO selectedDocService = mDocServiceAdapter.getItem(position);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.doc_service_view_dialog, null);

        ((TextView) view.findViewById(R.id.doc_service_title_tv)).setText(selectedDocService.getTitle());

        TextView description_tv = view.findViewById(R.id.doc_service_description_tv);
        if (selectedDocService.getDescription() == null || selectedDocService.getDescription().trim().isEmpty()) {
            description_tv.setText(getString(R.string.no_description));

        } else
            description_tv.setText(selectedDocService.getDescription());

        builder.setView(view);

        builder.setNegativeButton(R.string.action_close, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }


    private void showEditDialog(int position) {
        DocServiceDTO selectedDocService = mDocServiceAdapter.getItem(position);


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.doc_service_add_edit_dialog, null);

        ((TextView) view.findViewById(R.id.doc_service_dialog_title)).setText(R.string.title_edit_doc_service);

        EditText doc_service_title_et = view.findViewById(R.id.doc_service_title_et);
        doc_service_title_et.setText(selectedDocService.getTitle());

        EditText doc_service_description_et = view.findViewById(R.id.doc_service_description_et);
        doc_service_description_et.setText(selectedDocService.getDescription());

        builder.setView(view);

        DialogInterface.OnClickListener editClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = doc_service_title_et.getText().toString();
                String description = doc_service_description_et.getText().toString();

                selectedDocService.setTitle(title);
                selectedDocService.setDescription(description);

                DocService_Service docService_service = ServiceGenerator.createServiceSecured(DocService_Service.class);
                Call<DocServiceDTO> request = docService_service.update_Doc_service(selectedDocService.getId(), selectedDocService);
                request.enqueue(new Callback<DocServiceDTO>() {
                    @Override
                    public void onResponse(Call<DocServiceDTO> call, Response<DocServiceDTO> response) {
                        switch (response.code()) {
                            case 200: {
                                DocServiceDTO responseDto = response.body();
                                mDocServiceAdapter.remove(selectedDocService);
                                mDocServiceAdapter.insert(responseDto, position);
                                mDocServiceAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), responseDto.getTitle() + "Doctor Service EDITED", Toast.LENGTH_SHORT).show();
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
                    public void onFailure(Call<DocServiceDTO> call, Throwable t) {

                    }
                });

            }

        };


        builder.setPositiveButton(R.string.action_edit, editClickListener);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        builder.create().show();

    }


    private void showDeleteDialog(int position) {
        DocServiceDTO selectedDocService = mDocServiceAdapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_dialog_title);

        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete
                DocService_Service docService_service = ServiceGenerator.createServiceSecured(DocService_Service.class);
                Call<Void> request = docService_service.delete_Doc_service(selectedDocService.getId());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case 200: {

                                Toast.makeText(getActivity(), selectedDocService.getTitle() + " Speciality DELETED", Toast.LENGTH_SHORT).show();

                                mDocServiceAdapter.remove(selectedDocService);
                                mDocServiceAdapter.notifyDataSetChanged();

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
