package com.makerfaireorlando.makerfaireorlando.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.makerfaireorlando.makerfaireorlando.Models.Items;
import com.makerfaireorlando.makerfaireorlando.R;
import com.makerfaireorlando.makerfaireorlando.Utils.Constants;

public class EventDetailActivity extends AppCompatActivity {

    private Items mEvent;

    /* Views */
    private Toolbar mToolbar;
    private TextView mTitle;
    private TextView mLocation;
    private TextView mDescription;

    private void loadViews() {
        mTitle          = (TextView) findViewById(R.id.event_title);
        mLocation       = (TextView) findViewById(R.id.event_location);
        mDescription    = (TextView) findViewById(R.id.event_description_full);
        mToolbar        = (Toolbar) findViewById(R.id.toolbar_actionbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        loadViews();
        setupToolbar();

        Bundle extras = getIntent().getExtras();

         /* set up button to do a deal based on the current deal */
        if (getIntent().getExtras() == null) {
            throw new IllegalArgumentException("DetailView requires extras");
        }

        mEvent = (Items) extras.getSerializable(Constants.EVENT);

        mTitle.setText(mEvent.summary);

        if (mEvent.location!=null) {
            mLocation.setText(mEvent.location);
        } else {
            mLocation.setVisibility(View.GONE);
        }

        mDescription.setText(mEvent.description);
    }

    private void setupToolbar() {
        // set ActionBar as Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
