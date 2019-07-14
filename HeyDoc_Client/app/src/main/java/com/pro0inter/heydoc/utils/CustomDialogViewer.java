package com.pro0inter.heydoc.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by redayoub on 5/15/19.
 */

public class CustomDialogViewer {
    public static final int BTN_NONE = -1;

    public static Dialog createDialog(
            Activity activity,
            int dialogLayout,
            int dialogTitleTextView,
            int dialogTitle,
            HashMap<Integer, String> init,
            Integer[] fromKeys,
            Integer[] toIds,
            int positive_btn_string,
            int negative_btn_string,
            DialogInterface.OnClickListener onPositiveClick,
            DialogInterface.OnClickListener onNegativeClick
    ) {
        DialogInterface.OnClickListener onNegativeClickListener = onNegativeClick;
        if (onNegativeClick == null) {
            onNegativeClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(dialogLayout, null);
        ((TextView) view.findViewById(dialogTitleTextView)).setText(dialogTitle);
        if (init != null && !init.isEmpty()) {
            if (fromKeys.length != toIds.length)
                throw new RuntimeException("from table dosn't match to table");

            for (int i = 0; i < fromKeys.length; i++) {
                int key = fromKeys[i];
                int id = toIds[i];
                EditText et = view.findViewById(id);
                et.setText(init.get(key));
            }
        }
        builder.setView(view);
        // Add action buttons
        if (positive_btn_string != BTN_NONE) {
            // this is a view dialog
            builder.setPositiveButton(positive_btn_string, onPositiveClick);
        }
        if (negative_btn_string != BTN_NONE)
            builder.setNegativeButton(negative_btn_string, onNegativeClick);
        //builder.setTitle(dialogTitle);
        return builder.create();
    }
}
