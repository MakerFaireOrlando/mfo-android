package com.makerfaireorlando.makerfaireorlando;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.makerfaireorlando.makerfaireorlando.Activities.EventDetailActivity;
import com.makerfaireorlando.makerfaireorlando.Fragments.EventsFragment;
import com.makerfaireorlando.makerfaireorlando.Activities.MakerDetailActivity;
import com.makerfaireorlando.makerfaireorlando.Fragments.MakersFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.MapFragment;
import com.makerfaireorlando.makerfaireorlando.Models.Items;
import com.makerfaireorlando.makerfaireorlando.Models.ProjectDetail;
import com.makerfaireorlando.makerfaireorlando.Utils.Constants;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, EventsFragment.OnEventSelectedListener, MakersFragment.OnMakerSelectedListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Toolbar mToolbar;
    private boolean shouldHideToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shouldHideToolbar = false;

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                mTitle = getString(R.string.title_section1);
                changeFragment(new EventsFragment());
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                changeFragment(new MakersFragment());
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                changeFragment(new MapFragment());
                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        if (!shouldHideToolbar)
            mToolbar.setVisibility(View.VISIBLE);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onEventSelected(Items event)
    {
        Intent i = new Intent(this, EventDetailActivity.class);
        i.putExtra(Constants.EVENT, event);
        this.startActivity(i);
    }

    public void onMakerSelected(ProjectDetail projectDetail) {
        Intent i = new Intent(this, MakerDetailActivity.class);
        i.putExtra(Constants.PROJECT, projectDetail);
        this.startActivity(i);
    }

    public void changeFragment(Fragment f)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }
}
