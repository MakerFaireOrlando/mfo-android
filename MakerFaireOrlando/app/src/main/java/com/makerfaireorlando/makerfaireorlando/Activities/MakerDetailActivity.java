package com.makerfaireorlando.makerfaireorlando.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makerfaireorlando.makerfaireorlando.Models.ProjectDetail;
import com.makerfaireorlando.makerfaireorlando.R;
import com.makerfaireorlando.makerfaireorlando.Utils.Constants;
import com.makerfaireorlando.makerfaireorlando.Utils.DownloadImageTask;
import com.makerfaireorlando.makerfaireorlando.Views.NotifyScrollView;

public class MakerDetailActivity extends AppCompatActivity implements NotifyScrollView.Callback {
    public static ProjectDetail mProjectDetail;

    private final String TAG = getClass().getSimpleName();
    private NotifyScrollView mNotifyScrollView;

    private FrameLayout mImageFrameLayout;

    private LinearLayout mContentLinearLayout;
    private LinearLayout mContentDetailLayout;

    private LinearLayout mToolbarLinearLayout;
    private Toolbar mToolbar;

    private ImageView mImageView;

    private TextView mProjectLocation;
    private TextView mProjectDescription;

    private TextView mMakerDescription;
    private TextView mMakerName;

    private void loadViews()
    {
        mNotifyScrollView = (NotifyScrollView) findViewById(R.id.notify_scroll_view);

        mImageFrameLayout = (FrameLayout) findViewById(R.id.image_frame_layout);
        mContentDetailLayout = (LinearLayout) findViewById(R.id.content_detail_layout);

        mImageView          = (ImageView) findViewById(R.id.image_view);
        mProjectLocation    = (TextView) findViewById(R.id.project_location);
        mProjectDescription = (TextView) findViewById(R.id.project_description);
        mMakerDescription   = (TextView) findViewById(R.id.maker_description);
        mMakerName          = (TextView) findViewById(R.id.maker_name);


        mContentLinearLayout = (LinearLayout) findViewById(R.id.content_linear_layout);
        mToolbarLinearLayout = (LinearLayout) findViewById(R.id.toolbar_linear_layout);

        mToolbar = (Toolbar) findViewById(R.id.project_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        loadViews();

        Bundle extras = getIntent().getExtras();

        /* set up button to do a deal based on the current deal */
        if (getIntent().getExtras() == null) {
            throw new IllegalArgumentException("DetailView requires extras");
        }

        mProjectDetail = (ProjectDetail) extras.getSerializable(Constants.PROJECT);

        if (mProjectDetail.photo_link != null) {
            new DownloadImageTask((ImageView) findViewById(R.id.image_view))
                    .execute(mProjectDetail.photo_link);
        } else {
            // todo show placeholder image
        }

        mToolbar.setTitle(mProjectDetail.project_name);

        // Show location
        if (mProjectDetail.location != null) {
            mProjectLocation.setText(mProjectDetail.location);
        } else {
            mProjectLocation.setVisibility(View.GONE);
        }

        mProjectDescription.setText(mProjectDetail.description);
        // Todo add all extra content dynamically for the project

        /* Setup Maker content */
        mMakerName.setText(mProjectDetail.maker.name);
        mMakerDescription.setText(mProjectDetail.maker.description);
        // Todo find place to put maker picture

        // Setup scrollview
        setupNotifyScrollView();
        setupToolbar();
    }


    private void setupNotifyScrollView() {
        mNotifyScrollView.setCallback(this);

        ViewTreeObserver viewTreeObserver = mNotifyScrollView.getViewTreeObserver();
        if (!viewTreeObserver.isAlive()) {
            Log.d(TAG, "TreeObserver is dead");
            return;
        }

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // get size
                int toolbarLinearLayoutHeight = mToolbarLinearLayout.getHeight();
                int imageHeight = mImageView.getHeight();

                // adjust image frame layout height
                ViewGroup.LayoutParams layoutParams = mImageFrameLayout.getLayoutParams();
                if (layoutParams.height != imageHeight) {
                    Log.d(TAG, "Layout = imageHeight (" + imageHeight + ")");
                    layoutParams.height = imageHeight;
                    mImageFrameLayout.setLayoutParams(layoutParams);
                }

                // adjust top margin of content linear layout
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mContentLinearLayout.getLayoutParams();
                if (marginLayoutParams.topMargin != toolbarLinearLayoutHeight + imageHeight) {
                    Log.d(TAG, "Margin Layout top = toolbarLineraLayoutHeight + imageHeight");
                    marginLayoutParams.topMargin = toolbarLinearLayoutHeight + imageHeight;
                    mContentLinearLayout.setLayoutParams(marginLayoutParams);
                }

                // call onScrollChange to update initial properties.
                Log.d(TAG, "Setting initial scroll to zero");
                onScrollChanged(0, 0, 0, 0);
            }
        });
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

    @Override
    public void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        // get scroll y
        int scrollY = mNotifyScrollView.getScrollY();

        // calculate new y (for toolbar translation)
        float newY = Math.max(mImageView.getHeight(), scrollY);

        // translate toolbar linear layout and image frame layout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mToolbarLinearLayout.setTranslationY(newY);
            mImageFrameLayout.setTranslationY(scrollY * 0.5f);
        } else {
            ViewCompat.setTranslationY(mToolbarLinearLayout, newY);
            ViewCompat.setTranslationY(mImageFrameLayout, scrollY * 0.5f);
        }
    }
}
