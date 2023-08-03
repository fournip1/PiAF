package com.paf.piaf;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;


public class QCMFragment extends Fragment {
    public final static int NB_CHOICES = 4;
    private DatabaseHelper dBHelper;
    private MediaPlayer mediaPlayer;
    private User user;
    private Level presentLevel;
    private List<Level> levels;
    private int nbQuestions, idQuestion;
    private List<Sound> soundsByLevel;
    private Sound selectedSound;
    private Bird selectedBird;
    private RuntimeExceptionDao<User, Integer> userRunTimeDao;
    private RuntimeExceptionDao<Score, Long> scoreRunTimeDao;
    private QuizzHelper quizzHelper;
    private final List<Bird> selectedBirds = new ArrayList<>();
    private ArrayAdapter<Bird> arrayAdapter;
    private Button nextButton;
    private ListView questionsList;


    public QCMFragment() {
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

        View currentView = inflater.inflate(R.layout.fragment_q_c_m, container, false);

        questionsList = (ListView) currentView.findViewById(R.id.questionsList);
        nextButton = (Button) currentView.findViewById(R.id.nextButton);
        Button replayButton = (Button) currentView.findViewById(R.id.replayButton);

        // the two following commands are the result of an Android FCkinG BUG concerning binding buttons with methods when using Fragments
        //
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                next();
            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                replay();
            }
        });


        arrayAdapter = new QuestionsArrayAdapter(getActivity(), selectedBirds);
        questionsList.setAdapter(arrayAdapter);
        questionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Object o = parent.getItemAtPosition(position);
                selectedBird = (Bird) o;
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

    public void next() {
        // First, we handle the previous question's' result
        questionsList.clearChoices();
        questionsList.requestLayout();
        arrayAdapter.notifyDataSetChanged();
        if (selectedSound.getBird().equals(selectedBird)) {
            scoreRunTimeDao.create(new Score(selectedSound, 1));
        } else {
            scoreRunTimeDao.create(new Score(selectedSound, 0));
        }

        // if this is the last question, we quit.
        // if it is the last but one question, we change the text of the button.
        if (idQuestion == nbQuestions+1) {
            // we display the plain answer fragment!
            if (dBHelper.validateLevel()) {
                user.setLastValidationTimestamp();
                Log.i(AnswersFragment.class.getName(),"Level validated!");
                if (presentLevel.getId() < levels.size()) {
                    Level nextLevel = dBHelper.getLevelRuntimeDao().queryForId(presentLevel.getId()+1);
                    user.setLevel(nextLevel);
                    userRunTimeDao.update(user);
                }
                ((MainActivity) getActivity()).showFiestaWelcome(idQuestion-1);
            } else {
                ((MainActivity) getActivity()).showAnswers(idQuestion-1);
            }
            return;
        } else if (idQuestion == nbQuestions) {
            nextButton.setText(getString(R.string.button_end));
        }

        // then we load a new sound and a new birds' list.
        quizzHelper.setSounds(soundsByLevel);
        selectedSound = quizzHelper.getSelectedSound();
        soundsByLevel.remove(selectedSound);
        selectedBirds.clear();
        selectedBirds.addAll(quizzHelper.getBirds());
        arrayAdapter.notifyDataSetChanged();
/*        Log.i("Selected sound", "Sound name: " + selectedSound.toString());
        Log.i(QuizzActivity.class.getName(), "Bird 1: " + selectedBirds.get(0).toString());
        Log.i(QuizzActivity.class.getName(), "Bird 2: " + selectedBirds.get(1).toString());
        Log.i(QuizzActivity.class.getName(), "Bird 3: " + selectedBirds.get(2).toString());
        Log.i(QuizzActivity.class.getName(), "Bird 4: " + selectedBirds.get(3).toString());*/
        Log.i(QCMFragment.class.getName(), "Remaining sounds: " + soundsByLevel.size());
        idQuestion++;
        playSound(selectedSound);
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
        selectedBirds.clear();
        selectedBirds.addAll(quizzHelper.getBirds());

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

        Log.i(QCMFragment.class.getName(),"QCM fragment destroyed.");
        super.onDestroy();
    }
}