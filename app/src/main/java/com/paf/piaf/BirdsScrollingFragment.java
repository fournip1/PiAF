package com.paf.piaf;

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
import java.util.List;

public class BirdsScrollingFragment extends Fragment {
    private DatabaseHelper dBHelper;
    private List<Bird> selectedBirds = new ArrayList<>();


    public BirdsScrollingFragment() {
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

        View currentView = inflater.inflate(R.layout.fragment_birds_scrolling, container, false);
        ListView birdsList = currentView.findViewById(R.id.birdsList);
        ArrayAdapter<Bird> arrayAdapter = new QuestionsArrayAdapter(getActivity(), selectedBirds);
        birdsList.setAdapter(arrayAdapter);

        birdsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                           int position, long id) {
                Bird selectedBird = ((Bird) parent.getItemAtPosition(position));
                ((MainActivity) getActivity()).showBirdCard(selectedBird.getId(),dBHelper.getUserRuntimeDao().queryForFirst().getLevel().getId());
                return true;
            }
        });
        initializeBirds();
        return currentView;
    }

    public void initializeBirds() {
        // we set-up the dbHelper
        dBHelper = new DatabaseHelper(getActivity());
        selectedBirds.clear();
        selectedBirds.addAll(dBHelper.getBirds());
        Log.i(BirdsScrollingFragment.class.getName(),"Number of birds: " + selectedBirds.size());
    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        if (dBHelper!=null) {
            dBHelper.close();
        }

        // Log.i(BirdsScrollingFragment.class.getName(),"Birds' scrolling fragment destroyed.");
        super.onDestroy();
    }
}