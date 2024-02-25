package com.paf.piaf;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
    // this stack is related to a user starting from the quizz welcome screen
    public final static String BACK_STACK_WELCOME_TAG = "welcome_stack";
    // this stack is related to a user starting from the birds'catalog screen
    public final static String BACK_STACK_CARD_TAG = "card_stack";

    // this tag is used to mark any other fragment than the welcome one, except bird card tag
    public final static String OTHER_FRAGMENTS_TAG = "other";
    //
    public final static String CARD_FRAGMENTS_TAG = "bird";


    //
    private FragmentManager fragmentManager;
    private WelcomeFragment welcomeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolBar = findViewById(R.id.toolbar);
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

    public void updateWelcomeTextAndImages() {
        welcomeFragment.updateTextAndImage();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Log.i(MainActivity.class.getName(), "item id: " + item.getTitle());
        String menuItem = String.valueOf(item.getTitle());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            if (menuItem.equals(getString(R.string.menu_item_settings))) {
            fragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, new SettingsFragment(),OTHER_FRAGMENTS_TAG)
                    .addToBackStack(BACK_STACK_WELCOME_TAG)
                    .commit();
        } else if (menuItem.equals(getString(R.string.menu_item_about))) {
                ShowTextFragment fragment = ShowTextFragment.newInstance("about");
                fragmentManager
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainerView, fragment,OTHER_FRAGMENTS_TAG)
                        .addToBackStack(BACK_STACK_WELCOME_TAG)
                        .commit();
            } else if (menuItem.equals(getString(R.string.menu_item_credits))) {
            ShowTextFragment fragment = ShowTextFragment.newInstance("credits");
            fragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, fragment,OTHER_FRAGMENTS_TAG)
                    .addToBackStack(BACK_STACK_WELCOME_TAG)
                    .commit();
        } else if (menuItem.equals(getString(R.string.menu_item_privacy))) {
                ShowTextFragment fragment = ShowTextFragment.newInstance("privacy");
                fragmentManager
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainerView, fragment,OTHER_FRAGMENTS_TAG)
                        .addToBackStack(BACK_STACK_WELCOME_TAG)
                        .commit();
            } else if (menuItem.equals(getString(R.string.menu_item_licence))) {
            ShowTextFragment fragment = ShowTextFragment.newInstance("licence");
            fragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, fragment,OTHER_FRAGMENTS_TAG)
                    .addToBackStack(BACK_STACK_WELCOME_TAG)
                    .commit();
        } else if (menuItem.equals(getString(R.string.menu_item_reset))) {
            ResetFragment resetFragment = new ResetFragment();
            resetFragment.show(fragmentManager,null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        } else if (menuItem.equals(getString(R.string.menu_item_catalog))) {
                BirdsScrollingFragment fragment = new BirdsScrollingFragment();
                fragmentManager
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainerView, fragment,OTHER_FRAGMENTS_TAG)
                        .addToBackStack(BACK_STACK_WELCOME_TAG)
                        .commit();
            }
        return super.onOptionsItemSelected(item);
    }

    public void showAnswers(int answersDepth) {
        AnswersFragment answersFragment = AnswersFragment.newInstance(answersDepth);
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, answersFragment, OTHER_FRAGMENTS_TAG)
                .addToBackStack(BACK_STACK_WELCOME_TAG)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void playQCMQuizz() {
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, new QCMFragment(),OTHER_FRAGMENTS_TAG)
                .addToBackStack(BACK_STACK_WELCOME_TAG)
                .commit();
    }


    public void showPrivacy() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ShowTextFragment fragment = ShowTextFragment.newInstance("privacy");
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, fragment,OTHER_FRAGMENTS_TAG)
                .addToBackStack(BACK_STACK_WELCOME_TAG)
                .commit();
    }

    public void showBirdCard(int birdId, int levelId) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        BirdCardFragment fragment = BirdCardFragment.newInstance(birdId,levelId);
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, fragment,CARD_FRAGMENTS_TAG)
                .addToBackStack(BACK_STACK_CARD_TAG)
                .commit();
    }


    public void playFreeQuizz() {
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, new FreeFragment(),OTHER_FRAGMENTS_TAG)
                .addToBackStack(BACK_STACK_WELCOME_TAG)
                .commit();
    }

    public void showFiestaWelcome(int answersDepth, Level pastLevel) {
        // we display the welcome fragment with a fiesta image for a few seconds!
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeFragment.setValidated(false);
                showAnswers(answersDepth);
            }
        }, SPLASH_TIME_OUT);
        welcomeFragment.setValidated(true);
        welcomeFragment.setPastLevel(pastLevel);
        fragmentManager.popBackStack(BACK_STACK_WELCOME_TAG,
                fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    @Override
    public void onBackPressed() {
        if (fragmentManager.findFragmentByTag(CARD_FRAGMENTS_TAG) != null) {
            // I'm viewing Fragment C
            fragmentManager.popBackStack(BACK_STACK_CARD_TAG,
                    fragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (fragmentManager.findFragmentByTag(OTHER_FRAGMENTS_TAG) != null) {
            // I'm viewing Fragment C
            fragmentManager.popBackStack(BACK_STACK_WELCOME_TAG,
                    fragmentManager.POP_BACK_STACK_INCLUSIVE);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
         } else {
            super.onBackPressed();
         }
    }
}