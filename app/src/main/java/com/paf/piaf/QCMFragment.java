package com.paf.piaf;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;


public class QCMFragment extends Fragment {
    private int nbChoices;
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
    private TextView creditTextView;

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

        questionsList = currentView.findViewById(R.id.questionsList);
        nextButton = currentView.findViewById(R.id.nextButton);
        Button replayButton = currentView.findViewById(R.id.replayButton);
        creditTextView = currentView.findViewById(R.id.creditTextView);


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

    public void next() {
        /*The following lines are due to a F..CKING bug in Android, which does not
                deselect item when reloading the view. See the following discussion:
        https://stackoverflow.com/questions/9754170/listview-selection-remains-persistent-after-exiting-choice-mode*/
        questionsList.setChoiceMode(ListView.CHOICE_MODE_NONE);
        questionsList.setAdapter(arrayAdapter);

        // First, we handle the previous question's' result
        if (selectedSound.getBird().equals(selectedBird)) {
            scoreRunTimeDao.create(new Score(selectedSound, 1, null));
        } else {
            scoreRunTimeDao.create(new Score(selectedSound, 0, selectedBird));
        }

        // if this is the last question, we quit.
        // if it is the last but one question, we change the text of the button.
        if (idQuestion == nbQuestions+1) {
            stopSound();
            // we display the plain answer fragment!
            if (dBHelper.validateLevel() && !user.isFinished()) {
                user.setLastValidationTimestamp();
                // (AnswersFragment.class.getName(),"Level validated!");
                if (presentLevel.getId() < levels.size()) {
                    user.setLevel(dBHelper.getLevelRuntimeDao().queryForId(presentLevel.getId()+1));
                } else {
                    // user has finished the game
                    user.setFinished(true);
                }
                // we update the user
                userRunTimeDao.update(user);
                ((MainActivity) getActivity()).showFiestaWelcome(idQuestion-1, presentLevel);
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
        creditTextView.setText(getString(R.string.sound_credit) + " " + selectedSound.getCredit());
        soundsByLevel.removeAll(selectedSound.getBird().getSounds());
        selectedBirds.clear();
        selectedBirds.addAll(quizzHelper.getBirds(nbChoices));
        arrayAdapter.notifyDataSetChanged();
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
        nbChoices = user.getNbChoices();

        // nbQuestions =  Integer.parseInt(preferences.getString("nb_questions","10"));;
        // finally we get the sounds for the corresponding level
        soundsByLevel = dBHelper.getSoundsByLevel(presentLevel);
        // Only for debugging purposes
        quizzHelper = new QuizzHelper(soundsByLevel);
        selectedSound = quizzHelper.getSelectedSound();
        creditTextView.setText(getString(R.string.sound_credit) + " " + selectedSound.getCredit());
        soundsByLevel.removeAll(selectedSound.getBird().getSounds());
        selectedBirds.clear();
        selectedBirds.addAll(quizzHelper.getBirds(nbChoices));
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
        // Log.i(QCMFragment.class.getName(),"QCM fragment destroyed.");
        super.onDestroy();
    }
}