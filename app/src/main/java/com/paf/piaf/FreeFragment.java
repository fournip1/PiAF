package com.paf.piaf;

import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;


public class FreeFragment extends Fragment {
    private DatabaseHelper dBHelper;
    private MediaPlayer mediaPlayer;
    private User user;
    private Level presentLevel;
    private List<Level> levels;
    private int nbQuestions, idQuestion;
    private List<Sound> soundsByLevel;
    private Sound selectedSound;
    private boolean answerShown = false;
    private RuntimeExceptionDao<User, Integer> userRunTimeDao;
    private RuntimeExceptionDao<Score, Long> scoreRunTimeDao;
    private QuizzHelper quizzHelper;
    private Button yesButton, noButton;
    private TextView freeQuestionTextView;
    private ImageView iconBird;


    public FreeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.fragment_free, container, false);
        // Inflate the layout for this fragment

        yesButton = (Button) currentView.findViewById(R.id.yesButton);
        noButton = (Button) currentView.findViewById(R.id.noButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                yes();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                no();
            }
        });

        freeQuestionTextView = (TextView) currentView.findViewById(R.id.freeQuestionTextView);
        iconBird = (ImageView) currentView.findViewById(R.id.iconBird);

        iconBird.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.equals(iconBird)) {
                    replay();
                }
            }
        });

        idQuestion = 1;
        initializeQuizz();

        return currentView;
    }

    public void playSound(Sound sound) {
        int soundResourceId = getResources().getIdentifier(sound.getBasePath(), "raw", getActivity().getPackageName());
        if (soundResourceId != 0) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(getActivity(), soundResourceId);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    public void replay() {
        playSound(selectedSound);
    }

    public void yes() {
        if (!answerShown) {
            freeQuestionTextView.setText(getString(R.string.free_answer) + " " + selectedSound.getBird());
            int imageResourceId = getActivity().getResources().getIdentifier(selectedSound.getBird().getImageBasePath(), "drawable", getActivity().getPackageName());
            if (imageResourceId != 0) {
                iconBird.setImageResource(imageResourceId);
            } else {
                iconBird.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
            }
            yesButton.setText(R.string.button_correct);
            noButton.setText(R.string.button_incorrect);
            answerShown = true;
        } else {
            scoreRunTimeDao.create(new Score(selectedSound, 1));
            // maybe it is the last question
            if (idQuestion == nbQuestions + 1) {
                lastQuestionRoutine();
                return;
            }
            showNexQuestion();
        }
    }

    public void no() {
        // this is the case where the answer is shown but incorrect
        if (answerShown) {
            scoreRunTimeDao.create(new Score(selectedSound, -1));
            // maybe it is the last question
            if (idQuestion == nbQuestions + 1) {
                lastQuestionRoutine();
                return;
            }
            showNexQuestion();
        } else {
            // this is the case where the answer is not shown and the player ignores it.
            // we want to display the answer briefly and then move to another question
            scoreRunTimeDao.create(new Score(selectedSound, 0));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNexQuestion();
                }
            }, MainActivity.SPLASH_TIME_OUT);
            freeQuestionTextView.setText(getString(R.string.free_answer) + " " + selectedSound.getBird());
            int imageResourceId = getActivity().getResources().getIdentifier(selectedSound.getBird().getImageBasePath(), "drawable", getActivity().getPackageName());
            if (imageResourceId != 0) {
                iconBird.setImageResource(imageResourceId);
            } else {
                iconBird.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
            }
        }
    }

    public void lastQuestionRoutine() {
        // we display the plain answer fragment!
        if (dBHelper.validateLevel()) {
            user.setLastValidationTimestamp();
            Log.i(AnswersFragment.class.getName(), "Level validated!");
            if (presentLevel.getId() < levels.size()) {
                Level nextLevel = dBHelper.getLevelRuntimeDao().queryForId(presentLevel.getId() + 1);
                user.setLevel(nextLevel);
                userRunTimeDao.update(user);
            }
            ((MainActivity) getActivity()).showFiestaWelcome(idQuestion - 1);
        } else {
            ((MainActivity) getActivity()).showAnswers(idQuestion - 1);
        }
    }

    public void showNexQuestion() {
        quizzHelper.setSounds(soundsByLevel);
        selectedSound = quizzHelper.getSelectedSound();
        soundsByLevel.remove(selectedSound);
        Log.i(QCMFragment.class.getName(), "Remaining sounds: " + soundsByLevel.size());
        idQuestion++;
        playSound(selectedSound);
        freeQuestionTextView.setText(getString(R.string.free_question));
        yesButton.setText(R.string.button_yes);
        noButton.setText(R.string.button_no);
        int imageResourceId = getActivity().getResources().getIdentifier("mystery_bird", "drawable", getActivity().getPackageName());
        if (imageResourceId != 0) {
            iconBird.setImageResource(imageResourceId);
        } else {
            iconBird.setImageResource(getActivity().getResources().getIdentifier("standard_bird","drawable",getActivity().getPackageName()));
        }
        answerShown = false;
    }

    public void initializeQuizz() {
        // we set-up the dbHelper
        // we prepare as well the DAO for sounds and scores
        dBHelper = new DatabaseHelper(getActivity());
        userRunTimeDao = dBHelper.getUserRuntimeDao();
        scoreRunTimeDao = dBHelper.getScoreRuntimeDao();


        // we get the user and the level
        user = userRunTimeDao.queryForFirst();
        presentLevel = user.getLevel();
        levels = dBHelper.getLevelRuntimeDao().queryForAll();
        nbQuestions = user.getNbQuestions();

        // nbQuestions =  Integer.parseInt(preferences.getString("nb_questions","10"));;
        // finally we get the sounds for the corresponding level
        soundsByLevel = dBHelper.getSoundsByLevel(presentLevel);
        // Only for debugging purposes
        quizzHelper = new QuizzHelper(soundsByLevel);
        selectedSound = quizzHelper.getSelectedSound();
        soundsByLevel.remove(selectedSound);
        idQuestion++;
        playSound(selectedSound);
    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        userRunTimeDao = null;
        scoreRunTimeDao = null;
        dBHelper.close();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Log.i(FreeFragment.class.getName(), "Free fragment destroyed.");
        super.onDestroy();
    }
}