package com.paf.piaf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SoundsArrayAdapter extends ArrayAdapter<Sound>  {
    private final Context context;
    private final List<Sound> sounds;
    public SoundsArrayAdapter(Context context, List<Sound> sounds) {
        super(context, -1, sounds);
        this.context = context;
        this.sounds = sounds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.sounds_listview, parent, false);
        Sound currentSound = sounds.get(position);
        TextView soundTextView = rowView.findViewById(R.id.soundLine);
        TextView creditTextView = rowView.findViewById(R.id.creditLine);
        soundTextView.setText(currentSound.getType());
        creditTextView.setText("Credit sonore: " + currentSound.getCredit());


        return rowView;
    }

}
