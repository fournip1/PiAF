package com.paf.piaf;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class WarningFragment extends DialogFragment {
    private DatabaseHelper dBHelper;
    private User user;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dBHelper = new DatabaseHelper(getActivity());
        user = dBHelper.getUserRuntimeDao().queryForFirst();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_warning)
                .setPositiveButton(R.string.all_right, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                })
                .setNegativeButton(R.string.not_again, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Let's update user's preferences
                       user.setWarning(false);
                       dBHelper.getUserRuntimeDao().update(user);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        if (dBHelper!=null) {
            dBHelper.close();
        }
        // Log.i(ResetFragment.class.getName(),"Reset fragment destroyed.");
        super.onDestroy();
    }
}