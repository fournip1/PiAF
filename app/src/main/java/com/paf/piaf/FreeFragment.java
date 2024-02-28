package com.paf.piaf;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import com.j256.ormlite.dao.RuntimeExceptionDao;

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
    private Button showButton, replayButton;
    private TextView freeQuestionTextView, creditTextView;
    private ImageView iconBird;
    // these are used for the swipe
    private float x1, x2;
    public static final int MIN_DISTANCE = 150;


    public FreeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.fragment_free, container, false);
        // Inflate the layout for this fragment

        showButton = currentView.findViewById(R.id.showButton);
        replayButton = currentView.findViewById(R.id.replayButton);

        showButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAnswer();
            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                replay();
            }
        });

        freeQuestionTextView = currentView.findViewById(R.id.freeQuestionTextView);
        creditTextView = currentView.findViewById(R.id.creditTextView);
        iconBird = currentView.findViewById(R.id.iconBird);

        // we want to detect the swipes left and right
        iconBird.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // this boolean is true if swipe right
                boolean right;
                if (answerShown) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x1 = event.getX();
                            break;
                        case MotionEvent.ACTION_UP:
                            x2 = event.getX();
                            float deltaX = x2 - x1;
                            if (Math.abs(deltaX) > MIN_DISTANCE) {
                                // Left to Right swipe action
                                if (x2 > x1) {
                                    right = true;
                                    Toast.makeText(getActivity(), getString(R.string.answer_correct), Toast.LENGTH_SHORT).show();
                                }
                                // Right to left swipe action
                                else {
                                    right = false;
                                    Toast.makeText(getActivity(), getString(R.string.answer_incorrect), Toast.LENGTH_SHORT).show();
                                }
                                recordAnswer(right);
                            }
                    }
                }
                return true;
            }
        });

        idQuestion = 1;
        initializeQuizz();

        String className = this.getClass().getName();
        if (dBHelper.hasHints(className) && user.isHint()) {
            HintMessageFragment hintMessageFragment = HintMessageFragment.newInstance(className);
            hintMessageFragment.show(((MainActivity) getActivity()).getSupportFragmentManager(),null);
        }

        return currentView;
    }

    public void playSound(Sound sound) {
        int soundResourceId = getResources().getIdentifier(sound.getBasePath(), "raw", getActivity().getPackageName());
        if (soundResourceId != 0) {
            stopSound();
            mediaPlayer = MediaPlayer.create(getActivity(), soundResourceId);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    public void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void replay() {
        playSound(selectedSound);
    }

    public void showAnswer() {
        if (!answerShown) {
            freeQuestionTextView.setText(selectedSound.getBird().getFrench());
            int imageResourceId = getActivity().getResources().getIdentifier(selectedSound.getBird().getImageBasePath(), "drawable", getActivity().getPackageName());
            if (imageResourceId != 0) {
                iconBird.setImageResource(imageResourceId);
            } else {
                iconBird.setImageResource(getActivity().getResources().getIdentifier("standard_bird", "drawable", getActivity().getPackageName()));
            }
            showButton.setVisibility(View.GONE);
            replayButton.setVisibility(View.GONE);
            answerShown = true;
        }
    }

    public void recordAnswer(boolean right) {
        if (right) {
            scoreRunTimeDao.create(new Score(selectedSound, 1, null));
        } else {
            scoreRunTimeDao.create(new Score(selectedSound, 0, null));
        }
        // maybe it is the last question
        if (idQuestion == nbQuestions + 1) {
            lastQuestionRoutine();
            return;
        }
        showNexQuestion();
    }

    public void lastQuestionRoutine() {
        // we display the plain answer fragment!
        stopSound();
        if (dBHelper.validateLevel() && !user.isFinished()) {
            user.setLastValidationTimestamp();
            // Log.i(AnswersFragment.class.getName(), "Level validated!");
            if (presentLevel.getId() < levels.size()) {
                user.setLevel(dBHelper.getLevelRuntimeDao().queryForId(presentLevel.getId() + 1));
            } else {
                // user has finished the game
                user.setFinished(true);
            }
            userRunTimeDao.update(user);
            ((MainActivity) getActivity()).showFiestaWelcome(idQuestion - 1, presentLevel);
        } else {
            ((MainActivity) getActivity()).showAnswers(idQuestion - 1);
        }
    }

    public void showNexQuestion() {
        quizzHelper.setSounds(soundsByLevel);
        selectedSound = quizzHelper.getSelectedSound();
        creditTextView.setText(getString(R.string.sound_credit) + " " + selectedSound.getCredit());
        soundsByLevel.removeAll(selectedSound.getBird().getSounds());
        // Log.i(FreeFragment.class.getName(), "Remaining sounds: " + soundsByLevel.size());
        idQuestion++;
        playSound(selectedSound);
        freeQuestionTextView.setText(getString(R.string.free_question));
        int imageResourceId = getActivity().getResources().getIdentifier("ic_launcher", "mipmap", getActivity().getPackageName());
        if (imageResourceId != 0) {
            iconBird.setImageResource(imageResourceId);
        } else {
            iconBird.setImageResource(getActivity().getResources().getIdentifier("standard_bird", "drawable", getActivity().getPackageName()));
        }
        showButton.setVisibility(View.VISIBLE);
        replayButton.setVisibility(View.VISIBLE);
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
        creditTextView.setText(getString(R.string.sound_credit) + " " + selectedSound.getCredit());
        soundsByLevel.removeAll(selectedSound.getBird().getSounds());
        idQuestion++;
        playSound(selectedSound);
    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        userRunTimeDao = null;
        scoreRunTimeDao = null;
        if (dBHelper!=null) {
            dBHelper.close();
        }
        stopSound();
        // Log.i(FreeFragment.class.getName(), "Free fragment destroyed.");
        super.onDestroy();
    }
}