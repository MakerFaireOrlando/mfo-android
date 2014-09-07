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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.makerfaireorlando.makerfaireorlando.Fragments.ConnectFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.EventDetailFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.EventsFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.MakerDetailFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.MakersFragment;
import com.makerfaireorlando.makerfaireorlando.Fragments.MapFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends ActionBarActivity
                implements MakersFragment.OnMakerSelectedListener, EventsFragment.OnEventSelectedListener{
    private static final String TAG = "MainActivity";
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

    // GCM
    // TODO: replace default values
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "443805360452";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    String regid;

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
        getSupportActionBar().setDisplayShowTitleEnabled(true);

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
        if(context == null){
            context = getApplicationContext();
        }
        if(checkPlayServices()){
            Log.i(TAG, "This device supports Google Play Services.");
            gcm = GoogleCloudMessaging.getInstance(this);

            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        if (prefs.getBoolean("firstrun", true)) {
            mDrawerLayout.openDrawer(mDrawerList);
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            prefs.edit().putBoolean("firstrun", false).commit();
            checkPlayServices();
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

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    Log.i("regid", regid);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();
                    storeRegistrationId(getApplicationContext(), regid);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    //storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
                Log.i(TAG, "message is = " + msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://ckinberger.hashbang.sh:9999/");
        JSONObject object = new JSONObject();
        String message;
        try {
            // Add your data
            object.put("id", regid);
            message = object.toString();

            httppost.setEntity(new StringEntity(message, "UTF8"));
            httppost.setHeader("Content-type", "application/json");

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
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
            Intent intent = new Intent(this, SignUpActivity.class);
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
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * Callbacks for fragments
     */
    public void onMakerSelected(String item) {
        MakerDetailFragment newFragment = new MakerDetailFragment();
        Bundle args = new Bundle();
        args.putString("Name", item);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.content_frame, newFragment).setCustomAnimations(android.R.anim.slide_in_left, 0, 0, android.R.anim.slide_out_right);
        transaction.addToBackStack(null);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    }

}