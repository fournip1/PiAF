package com.paf.piaf;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AnswersArrayAdapter extends ArrayAdapter<Score> {
    private final Context context;
    private List<Score> scores = new ArrayList<>();
    public AnswersArrayAdapter(Context context, List<Score> scores) {
        super(context, -1, scores);
        this.context = context;
        this.scores = scores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_listview, parent, false);
        Score currentScore = scores.get(position);
        TextView firstTextView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondTextView = (TextView) rowView.findViewById(R.id.secondLine);
        firstTextView.setText(currentScore.getSound().getBird().getFrench());
        secondTextView.setText(currentScore.getSound().getType());



        // we want the text to be green if fine and red if not.
        if (currentScore.getScore()==1) {
            firstTextView.setTextColor(Color.GREEN);
            secondTextView.setTextColor(Color.GREEN);
        } else
        {
            firstTextView.setTextColor(Color.RED);
            secondTextView.setTextColor(Color.RED);
        }
        return rowView;
    }
}
