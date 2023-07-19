package com.paf.piaf;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnswersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswersFragment extends Fragment {
    private static final String ARG_ANSWERS_DEPTH = "answersDepth";

    private int answsersDepth;
    private DatabaseHelper dBHelper;
    private ListView answsersList;
    private List<Score> answersScores = new ArrayList<>();
    // private ArrayAdapter<Score> arrayAdapter;
    private AnswersArrayAdapter arrayAdapter;
    private MediaPlayer mediaPlayer;

    public AnswersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param answsersDepth defines how many scores we should take.
     * @return A new instance of fragment AswersFragment.
     */

    public static AnswersFragment newInstance(int answsersDepth) {
        AnswersFragment fragment = new AnswersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ANSWERS_DEPTH, answsersDepth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            answsersDepth = getArguments().getInt(ARG_ANSWERS_DEPTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_answers, container, false);

        answsersList = (ListView) currentView.findViewById(R.id.answersList);

        // arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.activity_listview, R.id.listTextView, answersScores);
        arrayAdapter = new AnswersArrayAdapter(getActivity(),answersScores);
        answsersList.setAdapter(arrayAdapter);
        answsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Object object = parent.getItemAtPosition(position);
                playSound(((Score) object).getSound());
            }
        });
        initializeAnswers();
        return currentView;
    }

    private void initializeAnswers() {
        // we set-up the dbHelper
        // we prepare as well the DAO for scores
        dBHelper = new DatabaseHelper(getActivity());
        answersScores.clear();
        answersScores.addAll(dBHelper.getLastScores(Long.valueOf(answsersDepth)));
        int n = answersScores.size();
        answsersList.getItemAtPosition(0);
        Log.i(AnswersFragment.class.getName(),"Number of sounds in answers: " + answersScores.size());
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

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        dBHelper.close();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}