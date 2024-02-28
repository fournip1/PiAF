package com.paf.piaf;

import android.os.Bundle;
import android.util.Log;
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
    private static final String ARG_VALIDATION_LEVEL = "isValidated";
    private DatabaseHelper dBHelper;
    private User user;
    private Level pastLevel, currentLevel;
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
        
        updateTextAndImage();

        String className = this.getClass().getName();
        if (dBHelper.hasHints(className) && user.isHint()) {
            HintMessageFragment hintMessageFragment = HintMessageFragment.newInstance(className);
            hintMessageFragment.show(((MainActivity) getActivity()).getSupportFragmentManager(),null);
        }

        return currentView;
    }

    public void updateTextAndImage() {
        user = dBHelper.getUserRuntimeDao().queryForFirst();
        currentLevel = user.getLevel();
        isFirst = user.isFirst();

        if (validated) {
            playButton.setVisibility(View.GONE);
            int levelValidationImageResourceId = getActivity().getResources().getIdentifier(pastLevel.getLevelValidationImageBasePath(), "drawable", getActivity().getPackageName());
            if (levelValidationImageResourceId != 0) {
                Glide.with(this).load(levelValidationImageResourceId).into(icon);
            } else {
                icon.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
            }
            if (user.isFinished()) {
                levelTextView.setText(getString(R.string.game_won));
            } else {
                levelTextView.setText(getString(R.string.level_validation) + " " + pastLevel.getFrench() + "!");
            }

        } else {
            playButton.setVisibility(View.VISIBLE);
            int imageResourceId = getActivity().getResources().getIdentifier(currentLevel.getImageBasePath(), "drawable", getActivity().getPackageName());
            if (imageResourceId != 0) {
                icon.setImageResource(imageResourceId);
            } else {
                icon.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
            }

            levelTextView.setText(getString(R.string.level_information) + " " + currentLevel.getFrench() + ".");

            if (!user.isFinished()) {
                // targetTextView.setText(getString(R.string.target_information_1) + " " + Long.toString(dBHelper.getTargetNextLevel()) + " " + getString(R.string.target_information_2));
                float vicinity  = dBHelper.getTargetVicinity();
                if (vicinity<0.25) {
                    targetTextView.setText(getString(R.string.target_close));
                } else if (vicinity<0.5) {
                    targetTextView.setText(getString(R.string.target_mid));
                } else {
                    targetTextView.setText(getString(R.string.target_far));
                }
            } else {
                targetTextView.setText(getString(R.string.target_reached));
            }

            if (isFirst) {
                playButton.setText(getString(R.string.button_first));
            } else {
                playButton.setText(getString(R.string.button_play));
            }
        }
    }

    public void setPastLevel(Level pastLevel) {
        this.pastLevel = pastLevel;
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