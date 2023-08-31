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
    public final static int SPLASH_TIME_OUT = 5000;
    // when adding a fragment to back stack, it will be consumed when hitting backspace iif
    // its position in the backstack is tagged with this parameter
    // see onbackblabla for details
    public final static String BACK_STACK_CONSUMED_TAG = "welcome_to_quizz";
    // this tag is used to mark any other fragment than the welcome one
    public final static String OTHER_FRAGMENTS_TAG = "other";
    private FragmentManager fragmentManager;
    private WelcomeFragment welcomeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

/*        // this is to have a back button
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/


        fragmentManager = getSupportFragmentManager();
        welcomeFragment = new WelcomeFragment();

        // we now use a welcome fragment
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, welcomeFragment, null)
                .commit();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setWelcomeLevel(Level level) {
        welcomeFragment.setNextLevel(level);

/*        fragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, welcomeFragment, null)
                .commit();*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i(MainActivity.class.getName(), "item id: " + item.getTitle());
        String menuItem = String.valueOf(item.getTitle());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            if (menuItem.equals(getString(R.string.menu_item_settings))) {
            fragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, new SettingsFragment(),OTHER_FRAGMENTS_TAG)
                    .addToBackStack(BACK_STACK_CONSUMED_TAG)
                    .commit();
        } else if (menuItem.equals(getString(R.string.menu_item_about))) {
                ShowTextFragment fragment = ShowTextFragment.newInstance("about");
                fragmentManager
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainerView, fragment,OTHER_FRAGMENTS_TAG)
                        .addToBackStack(BACK_STACK_CONSUMED_TAG)
                        .commit();
            } else if (menuItem.equals(getString(R.string.menu_item_credits))) {
            ShowTextFragment fragment = ShowTextFragment.newInstance("credits");
            fragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, fragment,OTHER_FRAGMENTS_TAG)
                    .addToBackStack(BACK_STACK_CONSUMED_TAG)
                    .commit();
        } else if (menuItem.equals(getString(R.string.menu_item_licence))) {
            ShowTextFragment fragment = ShowTextFragment.newInstance("licence");
            fragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, fragment,OTHER_FRAGMENTS_TAG)
                    .addToBackStack(BACK_STACK_CONSUMED_TAG)
                    .commit();
        } else if (menuItem.equals(getString(R.string.menu_item_reset))) {
            ResetFragment resetFragment = new ResetFragment();
            resetFragment.show(fragmentManager,null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAnswers(int answersDepth) {
        AnswersFragment answersFragment = AnswersFragment.newInstance(answersDepth);
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, answersFragment, OTHER_FRAGMENTS_TAG)
                .addToBackStack(BACK_STACK_CONSUMED_TAG)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void playQCMQuizz() {
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, new QCMFragment(),OTHER_FRAGMENTS_TAG)
                .addToBackStack(BACK_STACK_CONSUMED_TAG)
                .commit();
    }


    public void playFreeQuizz() {
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, new FreeFragment(),OTHER_FRAGMENTS_TAG)
                .addToBackStack(BACK_STACK_CONSUMED_TAG)
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
        fragmentManager.popBackStack(BACK_STACK_CONSUMED_TAG,
                fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.findFragmentByTag(OTHER_FRAGMENTS_TAG) != null) {
            // I'm viewing Fragment C
            fragmentManager.popBackStack(BACK_STACK_CONSUMED_TAG,
                    fragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
         } else {
            super.onBackPressed();
         }
    }
}