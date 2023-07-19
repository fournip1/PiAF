package com.paf.piaf;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;

public class QuizzActivity extends AppCompatActivity implements Serializable {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainerView, QCMFragment.class,null)
                .commit();
        // AnswersFragment answersFragment = AnswersFragment.newInstance(10);

        // we now display the new fragment.
/*        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
        .add(R.id.fragmentContainerView, AnswersFragment.class,null)
        .commit();*/
    }


    private void loadFragment(Fragment fragment) {
// create a FragmentManager

    }
}