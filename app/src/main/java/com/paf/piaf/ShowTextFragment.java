package com.paf.piaf;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowTextFragment extends Fragment {

    private static final String ARG_TEXT_ID = "text_id";
    private String textId;
    private WebView webView;


    public ShowTextFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ShowTextFragment newInstance(String textId) {
        ShowTextFragment fragment = new ShowTextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT_ID, textId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            textId = getArguments().getString(ARG_TEXT_ID);
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_show_text, container, false);
        // init webView
        webView = (WebView) currentView.findViewById(R.id.htmlView);
        // displaying content in WebView from html file that stored in assets folder
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/" + textId + ".html");
        return currentView;
    }
}