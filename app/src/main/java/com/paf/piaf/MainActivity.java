package com.paf.piaf;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.Serializable;

// on exploite maintenant plutôt le databasehelper
public class MainActivity extends AppCompatActivity implements Serializable {

    private DatabaseHelper dBHelper;
    private User user;
    private int idLevel;
    private RuntimeExceptionDao<User, Integer> userRuntimeExceptionDao;
    private Switch QCMSwitch;
    private Slider nbQuestionsSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // opening database and getting the user
        dBHelper = new DatabaseHelper(getApplicationContext());
        userRuntimeExceptionDao = dBHelper.getUserRuntimeDao();
        user = userRuntimeExceptionDao.queryForAll().get(0);

        // Fetch info about level
        idLevel = user.getIdLevel();

        // display level on screen
        TextView levelTextView = (TextView) findViewById(R.id.levelTextView);
        levelTextView.setText("Vous êtes au niveau " + user.getLevel() +".");

        // update the seek bar with the number of questions
        nbQuestionsSlider = (Slider) findViewById(R.id.nbQuestionsSlider);
        Log.i(MainActivity.class.getName(),"User nb of questions: "+(float) user.getNbQuestions());
        nbQuestionsSlider.setValue((float) user.getNbQuestions());

        // update the switch the QCM boolean
        QCMSwitch = (Switch) findViewById(R.id.QCMSwitch);
        QCMSwitch.setChecked(user.isQCM());
    }

    /**
     * Called when the user taps the Send button
     */
    public void playQuizz(View view) {
        // Intents are used to pass information to QCMQuizzActivity and SimpleQuizzActivity
        Intent intentQCM = new Intent(this, QuizzActivity.class);

        // Fetch info about play mode, i.e. QCM.
        user.setQCM(QCMSwitch.isChecked());

        Log.i(MainActivity.class.getName(), "Number of questions: " + nbQuestionsSlider.getValue());
        user.setNbQuestions((int) nbQuestionsSlider.getValue());
        userRuntimeExceptionDao.update(user);

        if (user.isQCM()) {
            startActivity(intentQCM);
        } else {
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.hibou_petit_duc_chant_1);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    public DatabaseHelper getdBHelper() {
        return dBHelper;
    }
}