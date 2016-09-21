package com.makerfaireorlando.mforlando.Activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makerfaireorlando.mforlando.Models.Schedule.Event;
import com.makerfaireorlando.mforlando.R;
import com.makerfaireorlando.mforlando.Utils.Constants;
import com.makerfaireorlando.mforlando.Views.NotifyScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class EventDetailActivity extends AppCompatActivity implements NotifyScrollView.Callback {

    private final String TAG = getClass().getSimpleName();
    private Event mEvent;

    private NotifyScrollView mNotifyScrollView;

    private FrameLayout mImageFrameLayout;

    private LinearLayout mContentLinearLayout;
    private LinearLayout mContentDetailLayout;

    private LinearLayout mToolbarLinearLayout;

    private ImageView mImageView;


    /* Views */
    private Toolbar mToolbar;

    private TextView mLocation;
    private TextView mDescription;
    private TextView mTime;

    private TextView mDurationTitle;
    private TextView mDuration;

    private TextView mCostTitle;
    private TextView mCost;

    private TextView mAdditionalInfoTitle;
    private TextView mAdditionalInfo;

    private void loadViews() {
        mToolbar        = (Toolbar) findViewById(R.id.event_toolbar);

        mNotifyScrollView = (NotifyScrollView) findViewById(R.id.notify_scroll_view);

        mImageFrameLayout = (FrameLayout) findViewById(R.id.image_frame_layout);
        mContentDetailLayout = (LinearLayout) findViewById(R.id.content_detail_layout);

        mContentLinearLayout = (LinearLayout) findViewById(R.id.content_linear_layout);
        mToolbarLinearLayout = (LinearLayout) findViewById(R.id.toolbar_linear_layout);

        mImageView          = (ImageView) findViewById(R.id.image_view);

        // Content
        mLocation       = (TextView) findViewById(R.id.event_location);
        mDescription    = (TextView) findViewById(R.id.event_description);
        mTime           = (TextView) findViewById(R.id.event_start_and_end);
        mDurationTitle       = (TextView) findViewById(R.id.event_duration_title);
        mDuration       = (TextView) findViewById(R.id.event_duration);
        mCostTitle           = (TextView) findViewById(R.id.event_cost_title);
        mCost           = (TextView) findViewById(R.id.event_cost);
        mAdditionalInfoTitle    = (TextView) findViewById(R.id.event_additional_info_title);
        mAdditionalInfo    = (TextView) findViewById(R.id.event_additional_info);

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

        mEvent = (Event) extras.getSerializable(Constants.EVENT);

        if (mEvent.image_large != null) {
            //new DownloadImageTask(mImageView).execute(mEvent.image_large);
            ImageLoader.getInstance().loadImage(mEvent.image_large, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImageView.setImageBitmap(loadedImage);
                }
            });
        }

        mToolbar.setTitle(mEvent.name);

        if (!mEvent.location.isEmpty()) {
            mLocation.setText(mEvent.location);
        } else {
            mLocation.setVisibility(View.GONE);
        }

        if (!mEvent.start_time.isEmpty() && !mEvent.end_time.isEmpty()) {
            mTime.setText(mEvent.start_time + " - " + mEvent.end_time);
        } else {
            mTime.setText(mEvent.start_time);
        }

        if (!mEvent.duration.isEmpty()) {
            mDuration.setText(mEvent.duration);
        } else {
            mDurationTitle.setVisibility(View.GONE);
            mDuration.setVisibility(View.GONE);
        }

        if (!mEvent.cost.isEmpty()) {
            mCost.setText(mEvent.cost);
        } else {
            mCostTitle.setVisibility(View.GONE);
            mCost.setVisibility(View.GONE);
        }

        if (!mEvent.additional_info.isEmpty()) {
            mAdditionalInfo.setText(Html.fromHtml(mEvent.additional_info));
            mAdditionalInfo.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            mAdditionalInfoTitle.setVisibility(View.GONE);
            mAdditionalInfo.setVisibility(View.GONE);
        }

        mDescription.setText(mEvent.description);


        // Setup scrollview
        setupNotifyScrollView();
        setupToolbar();
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
