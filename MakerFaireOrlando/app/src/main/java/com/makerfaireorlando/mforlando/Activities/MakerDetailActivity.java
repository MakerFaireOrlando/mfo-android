package com.makerfaireorlando.mforlando.Activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makerfaireorlando.mforlando.Models.Exhibits.ProjectDetail;
import com.makerfaireorlando.mforlando.R;
import com.makerfaireorlando.mforlando.Utils.Constants;
import com.makerfaireorlando.mforlando.Views.NotifyScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

    private ViewPager mProjectViewPager;

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
        //mProjectViewPager          = (ViewPager) findViewById(R.id.project_pager);
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
            //new DownloadImageTask(mImageView).execute(mProjectDetail.photo_link);
            ImageLoader.getInstance().loadImage(mProjectDetail.photo_link, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImageView.setImageBitmap(loadedImage);
                }
            });
        }

        mToolbar.setTitle(mProjectDetail.project_name);

        if (mProjectDetail.color != 0) {
            mToolbar.setBackgroundColor(mProjectDetail.color);
            mToolbarLinearLayout.setBackgroundColor(mProjectDetail.color);
            mToolbar.setTitleTextColor(mProjectDetail.textColor);
            mProjectLocation.setTextColor(mProjectDetail.textColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mProjectDetail.darkColor);
            }
        }

        // Show location
        if (mProjectDetail.location != null) {
            mProjectLocation.setText(mProjectDetail.location);
        } else {
            mProjectLocation.setVisibility(View.GONE);
        }

        mProjectDescription.setText(mProjectDetail.description);
        mProjectDescription.setMovementMethod(LinkMovementMethod.getInstance());

        // images view view pager
        /*
        if (mProjectDetail.additional_photos != null) {
            mProjectViewPager.setAdapter(new PlaceSlidesFragmentAdapter(getSupportFragmentManager(),
                    mProjectDetail.additional_photos));
        } else {
            mProjectViewPager.setVisibility(View.GONE);
        }
        */

        /* Setup Maker content */
        mMakerName.setText(mProjectDetail.maker.name);

        mMakerDescription.setText(mProjectDetail.maker.description);
        mMakerDescription.setMovementMethod(LinkMovementMethod.getInstance());
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

    /*
    public class PlaceSlidesFragmentAdapter extends FragmentPagerAdapter {

        private List<Photo> photos;


        public PlaceSlidesFragmentAdapter(FragmentManager fm, List<Photo> photos) {
            super(fm);
            this.photos = photos;
        }

        @Override
        public Fragment getItem(int position) {
            return new ImageSlideFragment(photos.get(position).medium);
        }

        @Override
        public int getCount() {
            return photos.size();
        }

    }

    public static final class ImageSlideFragment extends Fragment {
        private String mLink;
        private ImageView mImageView;

        public ImageSlideFragment()
        {}

        public ImageSlideFragment(String link) {
            mLink = link;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View view = inflater.inflate(R.layout.fragment_image_slide, container, false);

            mImageView = (ImageView) view.findViewById(R.id.slide_image);


            float height = getActivity().getResources().getDimension(R.dimen.item_image_height);
            ImageSize size = new ImageSize((int) (height * 2), (int) height);

            ImageLoader.getInstance().loadImage(mLink, size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImageView.setImageBitmap(loadedImage);
                }
            });

            return view;
        }
    }*/
}
