package com.paf.piaf;


import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QCMQuizzActivity extends AppCompatActivity implements Serializable {
    public final static int NB_CHOICES = 4;
    private DatabaseHelper dBHelper;
    private MediaPlayer mediaPlayer;
    private User user;
    private int idLevel, nbQuestions, idQuestion;
    private List<Sound> soundsByLevel;
    private Sound selectedSound;
    private Bird selectedBird;
    private RuntimeExceptionDao<User, Integer> userRunTimeDao;
    private RuntimeExceptionDao<Score, Long> scoreRunTimeDao;
    private RuntimeExceptionDao<Sound, Integer> soundRunTimeDao;
    private QuizzHelper quizzHelper;
    private List<Bird> selectedBirds = new ArrayList<>();

    private ListView questionsList;
    private ArrayAdapter<Bird> arrayAdapter;
    private Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        idQuestion = 1;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qcmquizz);

        questionsList = (ListView) findViewById(R.id.questionsList);
        nextButton = (Button) findViewById(R.id.nextButton);

        /*  linking the bird list with the questions ListView
        To be able to do so, we have to create a dummy activity Listview
        and to edit the xml accordingly to that explanation:
        https://abhiandroid.com/ui/listview
        Then, to be able to get the selected item and highlight it, need to define a listener.
        Android is such a pain in the ass! */

        arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, R.id.questionTextView, selectedBirds);
        questionsList.setAdapter(arrayAdapter);
        questionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Object o = parent.getItemAtPosition(position);
                selectedBird = (Bird) o;
/*                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                view.setAlpha(1);
                            }
                        });*/
            }

        });

        // we set-up the dbHelper
        // we prepare as well the DAO for sounds and scores
        dBHelper = new DatabaseHelper(getApplicationContext());
        userRunTimeDao = dBHelper.getUserRuntimeDao();
        scoreRunTimeDao = dBHelper.getScoreRuntimeDao();
        soundRunTimeDao = dBHelper.getSoundRuntimeDao();

        // we get the user and the level
        user = userRunTimeDao.queryForAll().get(0);
        idLevel = user.getIdLevel();
        nbQuestions = user.getNbQuestions();

        // finally we get the sounds for the corresponding level
        try {
            soundsByLevel = soundRunTimeDao.queryBuilder()
                    .where()
                    .le(Sound.ID_LEVEL_FIELD_NAME, idLevel)
                    .query();
            Log.i(QCMQuizzActivity.class.getName(), "Number of sounds " + soundsByLevel.size());
        } catch (SQLException e) {
            Log.e(QCMQuizzActivity.class.getName(), "Error in the SQL Query to get the sounds for a given level.");
        }

        // Only for debugging purposes
        quizzHelper = new QuizzHelper(soundsByLevel);
        selectedSound = quizzHelper.getSelectedSound();
        soundsByLevel.remove(selectedSound);
        selectedBirds.removeAll(selectedBirds);
        selectedBirds.addAll(quizzHelper.getBirds());
        Log.i(QCMQuizzActivity.class.getName(), "Sound name: " + selectedSound.toString());
        Log.i(QCMQuizzActivity.class.getName(), "Bird 1: " + selectedBirds.get(0).toString());
        Log.i(QCMQuizzActivity.class.getName(), "Bird 2: " + selectedBirds.get(1).toString());
        Log.i(QCMQuizzActivity.class.getName(), "Bird 3: " + selectedBirds.get(2).toString());
        Log.i(QCMQuizzActivity.class.getName(), "Bird 4: " + selectedBirds.get(3).toString());


        idQuestion++;
        playSound();
    }

    public void playSound() {
        int soundResourceId = getResources().getIdentifier(selectedSound.getBasePath(), "raw", getPackageName());
        if (soundResourceId != 0) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
                Log.i(QCMQuizzActivity.class.getName(), "Releasing media player.");
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), soundResourceId);
            Log.i(QCMQuizzActivity.class.getName(), "Starting media player.");
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    public void replay(View view) {
        playSound();
    }

    public void next(View view) {
        // First, we handle the previous question's' result
        if (selectedSound.getBird().equals(selectedBird)) {
            scoreRunTimeDao.create(new Score(selectedSound, 1));
        } else {
            scoreRunTimeDao.create(new Score(selectedSound, 0));
        }

        // if this is the last question, we quit.
        // if it is the last but one question, we change the text of the button.
        if (idQuestion == nbQuestions+1) {
            Log.i(QCMQuizzActivity.class.getName(),"Level validation?");
            if (dBHelper.validateLevel()) {
                int presentLevel = user.getIdLevel();
                int nextLevel;
                if (presentLevel < Sound.LEVELS.length - 1) {
                    user.setIdLevel(presentLevel + 1);
                    userRunTimeDao.update(user);
                    Log.i(QCMQuizzActivity.class.getName(),"Level validated!");
                }
            }
            finish();
            return;
        } else if (idQuestion == nbQuestions) {
            Log.i(QCMQuizzActivity.class.getName(),"Modifying button.");
            nextButton.setText("Terminer");
        }

        // then we load a new sound and a new birds' list.
        quizzHelper.setSounds(soundsByLevel);
        selectedSound = quizzHelper.getSelectedSound();
        soundsByLevel.remove(selectedSound);
        selectedBirds.removeAll(selectedBirds);
        selectedBirds.addAll(quizzHelper.getBirds());
        arrayAdapter.notifyDataSetChanged();
        Log.i("Selected sound", "Sound name: " + selectedSound.toString());
        Log.i(QCMQuizzActivity.class.getName(), "Bird 1: " + selectedBirds.get(0).toString());
        Log.i(QCMQuizzActivity.class.getName(), "Bird 2: " + selectedBirds.get(1).toString());
        Log.i(QCMQuizzActivity.class.getName(), "Bird 3: " + selectedBirds.get(2).toString());
        Log.i(QCMQuizzActivity.class.getName(), "Bird 4: " + selectedBirds.get(3).toString());
        idQuestion++;
        playSound();
    }
}