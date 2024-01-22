package com.paf.piaf;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ResetFragment extends DialogFragment {
    private DatabaseHelper dBHelper;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dBHelper = new DatabaseHelper(getActivity());
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_reset_game)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dBHelper.resetGame();
                        Level level = dBHelper.getLevelRuntimeDao().queryForFirst();
                        ((MainActivity) getActivity()).setWelcomeLevel(level);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                       // ((MainActivity) getActivity()).showWelcome(user.getLevel());
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