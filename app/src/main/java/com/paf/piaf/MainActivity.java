package com.paf.piaf;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity {
    public final static int SPLASH_TIME_OUT = 3000;
    private FragmentManager fragmentManager;
    private WelcomeFragment welcomeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        fragmentManager = getSupportFragmentManager();
        welcomeFragment = new WelcomeFragment();

        // we now use a welcome fragment
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, welcomeFragment, null)
                .commit();
    }

    /**
     * Called when the user taps the Send button
     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i(MainActivity.class.getName(), "item id: " + item.getTitle());
        switch (String.valueOf(item.getTitle())) {
            case "Réglages": {
                // we now use a welcome fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainerView, SettingsFragment.class,null)
                        .addToBackStack(null)
                        .commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAnswers(int answersDepth) {
        AnswersFragment answersFragment = AnswersFragment.newInstance(answersDepth);
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, answersFragment, "Answers")
                .addToBackStack("WELCOME_TO_QUIZZ")
                .commit();
    }

    public void playQCMQuizz() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, QCMFragment.class,null)
                .addToBackStack("WELCOME_TO_QUIZZ")
                .commit();
    }


    public void playFreeQuizz() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, FreeFragment.class,null)
                .addToBackStack("WELCOME_TO_QUIZZ")
                .commit();
    }

    public void showFiestaWelcome(int answersDepth, Level presentLevel) {
        // we display the welcome fragment with a fiesta image for a few seconds!
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeFragment.setValidated(false);
                showAnswers(answersDepth);
            }
        }, SPLASH_TIME_OUT);
        welcomeFragment.setValidated(true);
        welcomeFragment.setPresentLevel(presentLevel);
        fragmentManager.popBackStack("WELCOME_TO_QUIZZ",
                fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("Answers") != null) {
            // I'm viewing Fragment C
            fragmentManager.popBackStack("WELCOME_TO_QUIZZ",
                    fragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }
    }
}