package com.paf.piaf;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnswersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswersFragment extends Fragment {
    // This time is in milliseconds
    private static final String ARG_ANSWERS_DEPTH = "answersDepth";

    private int answersDepth;
    private DatabaseHelper dBHelper;
    private TextView scoreTextView;
    private final List<Score> answersScores = new ArrayList<>();
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
            answersDepth = getArguments().getInt(ARG_ANSWERS_DEPTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_answers, container, false);

        ListView answsersListView = currentView.findViewById(R.id.answersList);
        scoreTextView = currentView.findViewById(R.id.scoreTextView);

        // arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.activity_listview, R.id.listTextView, answersScores);
        AnswersArrayAdapter arrayAdapter = new AnswersArrayAdapter(getActivity(),answersScores);
        answsersListView.setAdapter(arrayAdapter);
        answsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Object object = parent.getItemAtPosition(position);
                playSound(((Score) object).getSound());
            }
        });

        answsersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                stopSound();
                Bird selectedBird = ((Score) parent.getItemAtPosition(position)).getSound().getBird();
                ((MainActivity) getActivity()).showBirdCard(selectedBird.getId(),dBHelper.getUserRuntimeDao().queryForFirst().getLevel().getId());
                return true;
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
        answersScores.addAll(dBHelper.getLastScores(Long.valueOf(answersDepth)));
        long g = answersScores.stream()
                .filter((s) -> (s.getScore() == 1))
                .count();
        scoreTextView.setText(getString(R.string.score_text) + " " + String.valueOf(g) +"/" + String.valueOf(answersDepth));
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

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        if (dBHelper!=null) {
            dBHelper.close();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Log.i(AnswersFragment.class.getName(),"Answers fragment destroyed.");
        super.onDestroy();
    }
}