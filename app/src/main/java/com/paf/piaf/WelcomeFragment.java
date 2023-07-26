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

public class WelcomeFragment extends Fragment {
    private DatabaseHelper dBHelper;
    private User user;
    private Level level;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

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

        levelTextView.setText("Vous êtes au niveau " + level.getFrench() + ".");

        int imageResourceId = getActivity().getResources().getIdentifier(level.getImageBasePath(), "drawable", getActivity().getPackageName());

        if (imageResourceId != 0) {
            icon.setImageResource(imageResourceId);
        } else {
            icon.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
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
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, QCMFragment.class,null)
                    .commit();
        } else {
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.hibou_petit_duc_chant_1);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        dBHelper.close();
        super.onDestroy();
    }
}