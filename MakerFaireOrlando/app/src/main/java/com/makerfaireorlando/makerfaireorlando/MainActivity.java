/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0import android.app.Fragment;
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.makerfaireorlando.makerfaireorlando;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.makerfaireorlando.makerfaireorlando.Fragments.ConnectFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.EventDetailFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.EventsFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.MakerDetailFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.MakersFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.MapFragment;
import com.makerfaireorlando.makerfaireorlando.Models.ProjectDetail;
import com.makerfaireorlando.makerfaireorlando.Models.ProjectsList;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity
                implements MakersFragment.OnMakerSelectedListener, EventsFragment.OnEventSelectedListener{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] titles;
    private String[] classes;


    private static List<String> mCategories;

    private String mCurCheckPosition = "com.makerfaireorlando.makerfaireorlando.MainActivity";

    SharedPreferences prefs = null;


    private static final String FIRST_LAUNCH = "first_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Assume true if the key does not yet exist
        prefs = getSharedPreferences("com.makerfaireorlando.makerfaireorlando", MODE_PRIVATE);
        mCategories = new ArrayList<String>();

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getString("curChoice");
        }

        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        titles = getResources().getStringArray(R.array.selection_array);

        classes = getResources().getStringArray(R.array.nav_classes);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                 R.layout.drawer_list_item, titles));

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action ClickListenerto toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
                //getSupportFragmentManager().findFragmentByTag(mCurCheckPosition).setMenuVisibility(true);
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
                //getSupportFragmentManager().findFragmentByTag(mCurCheckPosition).setMenuVisibility(false);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction().addToBackStack(null);
            tx.replace(R.id.content_frame,
                    Fragment.instantiate(MainActivity.this, classes[0]), classes[0]);
            tx.commit();
            mCurCheckPosition = classes[0];
            mDrawerList.setItemChecked(0, true);
        }

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (prefs.getBoolean("firstrun", true)) {
            mDrawerLayout.openDrawer(mDrawerList);
            Intent intent = new Intent(this, EmailActivity.class);
            startActivity(intent);
            prefs.edit().putBoolean("firstrun", false).commit();

        }
        // Previous way we cached data
        // may want to implement this for makers and events
        /*
        try{
            FileInputStream fis = this.openFileInput("json_file");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder outputstring = new StringBuilder();
            String chunk = null;
            try {
                while ((chunk = br.readLine()) != null) {
                    outputstring.append(chunk);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mCacheJSONString = outputstring.toString();

            //parseItemList(mCacheJSONString);

        } catch(IOException e){
            Log.wtf("MainActivity on Resume", "could not find file");
        }
        */
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // caching
        /*
        outState.putString("curChoice", mCurCheckPosition);
        String FILENAME = "json_file";
        if(mCacheJSONString != null){
            try{
                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write(mCacheJSONString.getBytes());
                fos.close();
            }catch(FileNotFoundException e){
                Log.wtf("File not found", "I dont know what happend sorry");
            }catch(IOException e){
                Log.wtf("IO Exception", "great");
            }
        }
        */
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);


        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_settings).setVisible(true);
        //getSupportFragmentManager().findFragmentByTag(mCurCheckPosition).setMenuVisibility(drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_sign_up:
            Intent intent = new Intent(this, EmailActivity.class);
            startActivity(intent);
            return true;
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Selects the item clicked in the list
     * @param position in nav drawer list
     */
    private void selectItem(int position) {
        switch (position) {

            case 0:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new EventsFragment()).addToBackStack(null)
                        .commit();
                break;

            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new MakersFragment()).addToBackStack(null)
                        .commit();
                break;

            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new MapFragment()).addToBackStack(null)
                        .commit();
                break;

            case 3:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new ConnectFragment()).addToBackStack(null)
                        .commit();
                break;
        }

        //currentPage = position;
        mCurCheckPosition = classes[position];

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);

        setTitle(titles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        //getSupportFragmentManager().popBackStack();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * Callbacks for fragments
     */
    public void onMakerSelected(String item) {
        String name = item;
        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article
        MakerDetailFragment newFragment = new MakerDetailFragment();
        Bundle args = new Bundle();
        args.putString("Name", name);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.content_frame, newFragment).setCustomAnimations(android.R.anim.slide_in_left, 0, 0, android.R.anim.slide_out_right);
        transaction.addToBackStack(null);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Commit the transaction
        transaction.commit();
    }

    public void onEventSelected(int p){
        EventDetailFragment newFragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putInt("Position", p);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.content_frame, newFragment).setCustomAnimations(android.R.anim.slide_in_left, 0, 0, android.R.anim.slide_out_right);
        transaction.addToBackStack(null);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Commit the transaction
        transaction.commit();

    }

    public static List<String> getmCategories(){
        return mCategories;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //turn on the Navigation Drawer image; this is called in the LowerLevelFragments
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        //getSupportFragmentManager().popBackStack();
    }
}