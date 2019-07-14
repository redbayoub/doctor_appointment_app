package com.pro0inter.heydoc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.pro0inter.heydoc.R;
import com.pro0inter.heydoc.api.DTOs.DocServiceDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by redayoub on 5/28/19.
 */


public class DocServiceAdapter extends ArrayAdapter<DocServiceDTO> {
    private LayoutInflater mLayoutInflater;
    private Set<DocServiceDTO> notFiltredSet;

    public DocServiceAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                initNotFilterdSet();
                FilterResults results = new FilterResults();
                ArrayList<DocServiceDTO> filtredResult = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {

                    results.values = notFiltredSet;
                    results.count = notFiltredSet.size();
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < getCount(); i++) {
                        DocServiceDTO dto = getItem(i);
                        if (dto.getTitle() != null && dto.getTitle().toLowerCase().startsWith(constraint.toString()))
                            filtredResult.add(dto);
                        else if (dto.getDescription() != null && dto.getDescription().toLowerCase().startsWith(constraint.toString()))
                            filtredResult.add(dto);

                    }

                    results.count = filtredResult.size();
                    results.values = filtredResult;

                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll((Collection<? extends DocServiceDTO>) results.values);

                notifyDataSetChanged();
            }
        };
    }

    private void initNotFilterdSet() {
        if (notFiltredSet == null)
            notFiltredSet = new HashSet<>();
        for (int i = 0; i < getCount(); i++) {
            DocServiceDTO dto = getItem(i);
            notFiltredSet.add(dto);
        }
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        DataHandler handler;
        if (convertView == null) {

            row = mLayoutInflater.inflate(R.layout.doc_service_row_layout, parent, false);
            handler = new DataHandler();
            handler.title = row.findViewById(R.id.DocService_title);
            handler.description = row.findViewById(R.id.DocService_description);
            row.setTag(handler);
        } else {
            handler = (DataHandler) row.getTag();
        }
        DocServiceDTO dataProvider = getItem(pos);
        // empty handler
        handler.clear();

        handler.title.setText(dataProvider.getTitle());
        if (dataProvider.getDescription() != null && !dataProvider.getDescription().trim().isEmpty())
            handler.description.setText(dataProvider.getDescription());
        else
            handler.description.setText(getContext().getString(R.string.no_description));
        return row;
    }

    static class DataHandler {
        TextView title;
        TextView description;

        public void clear() {
            title.setText("");
            description.setText("");
        }
    }
}
