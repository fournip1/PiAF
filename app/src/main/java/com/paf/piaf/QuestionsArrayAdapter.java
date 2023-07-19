package com.paf.piaf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class QuestionsArrayAdapter extends ArrayAdapter<Bird> {
    private final Context context;
    private List<Bird> birds = new ArrayList<>();
    public QuestionsArrayAdapter(Context context, List<Bird> birds) {
        super(context, -1, birds);
        this.context = context;
        this.birds = birds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_listview, parent, false);
        Bird currentBird = birds.get(position);
        TextView firstTextView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondTextView = (TextView) rowView.findViewById(R.id.secondLine);
        firstTextView.setText(currentBird.getFrench());
        secondTextView.setText(currentBird.getLatin());
        return rowView;
    }
}
