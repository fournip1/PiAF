package com.paf.piaf;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class WelcomeFragment extends Fragment {
    private DatabaseHelper dBHelper;
    private User user;
    private Level level;
    private static final String ARG_VALIDATION_LEVEL = "isValidated";
    private boolean validated = false;


    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance(boolean validated) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_VALIDATION_LEVEL, validated);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            validated = getArguments().getBoolean(ARG_VALIDATION_LEVEL);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_welcome, container, false);

        // We get the user and the level
        dBHelper = new DatabaseHelper(getActivity());
        user = dBHelper.getUserRuntimeDao().queryForFirst();
        level = user.getLevel();

        // We set a listener to the button
        // modify the text of the textview
        // modify the image
        Button playButton = (Button) currentView.findViewById(R.id.playButton);
        TextView levelTextView = (TextView)  currentView.findViewById(R.id.levelTextView);
        ImageView icon = (ImageView)  currentView.findViewById(R.id.iconUser);

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playQuizz();
            }
        });


        if (validated) {
            int levelValidationImageResourceId = getActivity().getResources().getIdentifier(level.getLevelValidationImageBasePath(), "drawable", getActivity().getPackageName());
            if (levelValidationImageResourceId != 0) {
                Glide.with(this).load(levelValidationImageResourceId).into(icon);
            } else {
                icon.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
            }
            levelTextView.setText(getString(R.string.level_validation) + " " + level.getFrench() + "!");
        } else {
            int imageResourceId = getActivity().getResources().getIdentifier(level.getImageBasePath(), "drawable", getActivity().getPackageName());
            if (imageResourceId != 0) {
                icon.setImageResource(imageResourceId);
            } else {
                icon.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
            }
            levelTextView.setText(getString(R.string.level_information) + " " + level.getFrench() + ".");
        }
        return currentView;
    }

    public void playQuizz() {

        // here, we do not use the preferences stored
        // because it did not work
        // instead we use the database

        boolean isQCM = user.isQCM();
        Log.i(MainActivity.class.getName(),"QCM before game: " + isQCM);

        if (isQCM) {
            ((MainActivity) getActivity()).playQCMQuizz();
        } else {
            ((MainActivity) getActivity()).playFreeQuizz();
        }
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        dBHelper.close();
        Log.i(WelcomeFragment.class.getName(),"Welcome fragment destroyed.");
        super.onDestroy();
    }
}