package com.paf.piaf;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BirdCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BirdCardFragment extends Fragment {
    private static final String ARG_BIRD_ID = "birdId";
    private static final String ARG_LEVEL_ID = "levelId";

    private int birdId;
    private int levelId;
    private DatabaseHelper dBHelper;
    private Bird bird;
    private MediaPlayer mediaPlayer;

    public BirdCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param birdId  defines the bird id that should be portrayed in this fragment.
     * @param levelId reduces the scope of the sounds that should be displayed.
     * @return A new instance of fragment BirdCardFragment.
     */
    public static BirdCardFragment newInstance(int birdId, int levelId) {
        BirdCardFragment fragment = new BirdCardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BIRD_ID, birdId);
        args.putInt(ARG_LEVEL_ID, levelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            birdId = getArguments().getInt(ARG_BIRD_ID);
            levelId = getArguments().getInt(ARG_LEVEL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_bird_card, container, false);

        dBHelper = new DatabaseHelper(getActivity());
        bird = dBHelper.getBirdRuntimeDao().queryForId(birdId);

        TextView birdNameTextView = currentView.findViewById(R.id.birdNameTextView);
        TextView birdLatinNameTextView = currentView.findViewById(R.id.birdLatinNameTextView);
        ImageView icon = currentView.findViewById(R.id.iconBird);

        int imageResourceId = getActivity().getResources().getIdentifier(bird.getImageBasePath(), "drawable", getActivity().getPackageName());
        if (imageResourceId != 0) {
            icon.setImageResource(imageResourceId);
        } else {
            icon.setImageResource(getActivity().getResources().getIdentifier("standard_bird", "drawable", getActivity().getPackageName()));
        }

        icon.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bird.getUrl()));
                startActivity(browserIntent);
                return true;
            }
        });

        birdNameTextView.setText(bird.getFrench());
        birdLatinNameTextView.setText(bird.getLatin());

        ListView soundsListView = currentView.findViewById(R.id.soundsList);
        List<Sound> selectedSounds = dBHelper.getSoundsByBirdAndLevel(birdId, levelId);
        ArrayAdapter<Sound> arrayAdapter = new SoundsArrayAdapter(getActivity(), selectedSounds);
        soundsListView.setAdapter(arrayAdapter);
        soundsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Object object = parent.getItemAtPosition(position);
                playSound(((Sound) object));
            }
        });

        String className = this.getClass().getName();
        if (dBHelper.hasHints(className) && dBHelper.getUserRuntimeDao().queryForFirst().isHint()) {
            HintMessageFragment hintMessageFragment = HintMessageFragment.newInstance(className);
            hintMessageFragment.show(((MainActivity) getActivity()).getSupportFragmentManager(),null);
        }

        return currentView;
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
        stopSound();

        // Log.i(BirdCardFragment.class.getName(),"Bird card fragment destroyed.");
        super.onDestroy();
    }
}