package com.makerfaireorlando.makerfaireorlando;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.makerfaireorlando.makerfaireorlando.R;

/**
 * Created by conner on 6/2/13.
 */
public class Settings extends ActionBarActivity {

    public final static String EXTRA_LINK = "com.makerfaireorlando.makerfaireorlando.LINK";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final ListView settingsView = (ListView) findViewById(R.id.settingslist);
        String[] values = new String[] { "Sign In" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        settingsView.setAdapter(adapter);

        final ActionBarActivity activityThis = this;
        settingsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //get item that was clicked
                Object o = settingsView.getAdapter().getItem(position);
                String keyword = o.toString();

                //launch intents when different list items are clicked
                if(keyword.equals("Sign In")){
                    /*
                    Intent intent = new Intent(activityThis, WebViewActivity.class);
                    String message = "http://famitracker.herokuapp.com/users/sign_in";
                    intent.putExtra(EXTRA_LINK, message);
                    startActivity(intent);
                    */
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent =  NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                    //finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}