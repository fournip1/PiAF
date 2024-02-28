package com.paf.piaf;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class HintMessageFragment extends DialogFragment {
    // this string is used as a key to pass an argument to the bundle
    private static final String ARG_FRAGMENT_NAME = "fragmentName";

    private String fragmentName="";
    private DatabaseHelper dBHelper;
    private Hint hint;


    public HintMessageFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentName defines the fragment in which the hint is displayed.
     * @return A new instance of fragment HintMessageFragment.
     */
    public static HintMessageFragment newInstance(String fragmentName) {
        HintMessageFragment fragment = new HintMessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_NAME, fragmentName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            fragmentName = getArguments().getString(ARG_FRAGMENT_NAME);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dBHelper = new DatabaseHelper(getActivity());
        hint = dBHelper.getRandomHintByFragment(fragmentName);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PafDialog);
        builder.setMessage(hint.getHint())
                .setTitle(R.string.hint_label)
                .setPositiveButton(R.string.all_right, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                })
                .setNegativeButton(R.string.not_again, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Let's update user's preferences
                        hint.setShow(false);
                        dBHelper.getHintRuntimeDao().update(hint);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        if (dBHelper != null) {
            dBHelper.close();
        }
        // Log.i(ResetFragment.class.getName(),"Reset fragment destroyed.");
        super.onDestroy();
    }
}