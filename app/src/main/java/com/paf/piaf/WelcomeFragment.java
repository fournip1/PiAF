package com.paf.piaf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class WelcomeFragment extends Fragment {
    private DatabaseHelper dBHelper;
    private User user;
    private Level presentLevel, nextLevel;
    private static final String ARG_VALIDATION_LEVEL = "isValidated";
    private boolean validated = false, isFirst= false;
    private TextView levelTextView, targetTextView;
    private Button playButton;
    private ImageView icon;


    public WelcomeFragment() {
        // Required empty public constructor
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
        dBHelper = new DatabaseHelper(getActivity());
        user = dBHelper.getUserRuntimeDao().queryForFirst();
        nextLevel = user.getLevel();
        isFirst = user.isFirst();


        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_welcome, container, false);

        // We set a listener to the button
        // modify the text of the textview
        // modify the image
        playButton = currentView.findViewById(R.id.playButton);
        levelTextView = currentView.findViewById(R.id.levelTextView);
        targetTextView = currentView.findViewById(R.id.targetTextView);
        icon = currentView.findViewById(R.id.iconUser);

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playQuizz();
            }
        });

        if (validated) {
            playButton.setVisibility(View.GONE);
            int levelValidationImageResourceId = getActivity().getResources().getIdentifier(presentLevel.getLevelValidationImageBasePath(), "drawable", getActivity().getPackageName());
            if (levelValidationImageResourceId != 0) {
                Glide.with(this).load(levelValidationImageResourceId).into(icon);
            } else {
                icon.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
            }
            levelTextView.setText(getString(R.string.level_validation) + " " + presentLevel.getFrench() + "!");
        } else {
            playButton.setVisibility(View.VISIBLE);
            updateTextAndImage();
        }

        return currentView;
    }

    public void updateTextAndImage() {
        int imageResourceId = getActivity().getResources().getIdentifier(nextLevel.getImageBasePath(), "drawable", getActivity().getPackageName());
        if (imageResourceId != 0) {
            icon.setImageResource(imageResourceId);
        } else {
            icon.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
        }
        levelTextView.setText(getString(R.string.level_information) + " " + nextLevel.getFrench() + ".");
        targetTextView.setText(getString(R.string.target_information_1) + " " + Long.toString(dBHelper.getTargetNextLevel()) + " " + getString(R.string.target_information_2));
        if (isFirst) {
            playButton.setText(getString(R.string.button_first));
        } else {
            playButton.setText(getString(R.string.button_play));
        }
    }

    public void setPresentLevel(Level presentLevel) {
        this.presentLevel = presentLevel;
    }

    public void setNextLevel(Level nextLevel) {
        this.nextLevel = nextLevel;
        // we update as well the text and images
        updateTextAndImage();
    }

    public void playQuizz() {
        // here, we do not use the preferences stored
        // because it did not work
        // instead we use the database

        // Log.i(MainActivity.class.getName(),"QCM before game: " + isQCM);


        if (isFirst) {
            isFirst = false;
            user.setFirst(false);
            dBHelper.getUserRuntimeDao().update(user);
            ((MainActivity) getActivity()).showPrivacy();
        } else {
            if (user.isQCM()) {
                ((MainActivity) getActivity()).playQCMQuizz();
            } else {
                ((MainActivity) getActivity()).playFreeQuizz();
            }
        }
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        if (dBHelper!=null) {
            dBHelper.close();
        }
        // Log.i(WelcomeFragment.class.getName(),"Welcome fragment destroyed.");
        super.onDestroy();
    }
}