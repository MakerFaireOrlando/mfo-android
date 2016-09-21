package com.makerfaireorlando.mforlando.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.makerfaireorlando.mforlando.R;

public class MapFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        final WebView webView = (WebView) rootView.findViewById(R.id.webView);
        webView.loadUrl("file:///android_res/drawable/maps_level1.png");
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);

        Button level1 = (Button) rootView.findViewById(R.id.map_floor1);
        level1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imageView.setImageResource(R.drawable.ommf_officialmaps2013_level1);
                webView.loadUrl("file:///android_res/drawable/maps_level1.png");
            }
        });

        Button level2 = (Button) rootView.findViewById(R.id.map_floor2);
        level2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imageView.setImageResource(R.drawable.ommf_officialmaps2013_level2);
                webView.loadUrl("file:///android_res/drawable/maps_level2.png");
            }
        });

        Button level3 = (Button) rootView.findViewById(R.id.map_floor3);
        level3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imageView.setImageResource(R.drawable.ommf_officialmaps2013_level3);
                webView.loadUrl("file:///android_res/drawable/maps_level3.png");
            }
        });

        Button level4 = (Button) rootView.findViewById(R.id.map_floor4);
        level4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imageView.setImageResource(R.drawable.ommf_officialmaps2013_level4);
                webView.loadUrl("file:///android_res/drawable/maps_level4.png");
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }
}