package com.makerfaireorlando.makerfaireorlando.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makerfaireorlando.makerfaireorlando.Models.ProjectDetail;
import com.makerfaireorlando.makerfaireorlando.Models.ProjectsList;
import com.makerfaireorlando.makerfaireorlando.R;
import com.makerfaireorlando.makerfaireorlando.Utils.MakerRestClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by conner on 8/21/13.
 */
public class MakersFragment extends ListFragment
                            implements SearchView.OnQueryTextListener{

    //private List<ProjectDetail> mProjects;
    private ProjectsList mProjectsList;
    private static List<ProjectDetail> mAcceptedMakers;

    OnMakerSelectedListener mCallback;
    ProjectsListAdapter customAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        customAdapter = new ProjectsListAdapter(getActivity());

        //mProjects = MainActivity.getAcceptedMakers();
        try {
            getItemList();
        } catch(JSONException e) {

        }

        //Custom list adapter for custom list view
        //customAdapter = new ProjectsListAdapter(getActivity(), mAcceptedMakers);
        //setListAdapter(customAdapter);
    }

    // Container Activity must implement this interface
    public interface OnMakerSelectedListener {
        public void onMakerSelected(String p);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_projects, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(this);
    }

    public boolean onQueryTextChange(String newText) {
        // this is your adapter that will be filtered
        customAdapter.getFilter().filter(newText);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        // this is your adapter that will be filtered
        customAdapter.getFilter().filter(query);
        return true;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMakerSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mCallback.onMakerSelected(mAcceptedMakers.get(position).project_name);
    }

    public void getItemList() throws JSONException {

        MakerRestClient.get("overviewALL.json", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject itemObject) {
                parseItemList(itemObject.toString());
            }
        });
    }

    public void parseItemList(String jsonString){
        try {
            Gson gson = new Gson();
            mProjectsList = gson.fromJson(jsonString, ProjectsList.class);
            mAcceptedMakers = mProjectsList.accepteds;


            //Sort the accepted makers
            Collections.sort(mAcceptedMakers, new Comparator() {

                public int compare(Object o1, Object o2) {
                    ProjectDetail p1 = (ProjectDetail) o1;
                    ProjectDetail p2 = (ProjectDetail) o2;
                    return p1.project_name.compareToIgnoreCase(p2.project_name);
                }

            });

            //Custom list adapter for custom list view
            customAdapter.setAcceptedMakers(mAcceptedMakers);
            setListAdapter(customAdapter);
        } catch (Exception e) {
            Log.wtf("com.makerfaireorlando.makerfaireorlando.MainActivity", "Exception at GSON parse");
        }
    }

    private class ProjectsListAdapter extends BaseAdapter
                                    implements Filterable{

        private LayoutInflater inflater;
        private List<ProjectDetail> mProjects;
        private List<ProjectDetail> originalData;
        private List<ProjectDetail> filteredData;


        private class ViewHolder {
            TextView textTitle;
            TextView textSubTitle;
            View primaryTouchTargetView;
        }

        public ProjectsListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public ProjectsListAdapter(Context context, List<ProjectDetail> mProjects) {
            inflater = LayoutInflater.from(context);
            this.mProjects = mProjects;
            originalData = this.mProjects;
        }

        public void setAcceptedMakers(List<ProjectDetail> mProjects) {
            this.mProjects = mProjects;
        }

        public int getCount() {
            return mProjects.size();
        }

        public ProjectDetail getItem(int position) {
            return mProjects.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            final int mPosition = position;


            if(convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_makers, null);
                holder.textTitle = (TextView) convertView.findViewById(R.id.block_title);
                holder.textSubTitle = (TextView) convertView.findViewById(R.id.block_subtitle);
                holder.primaryTouchTargetView =  convertView.findViewById(R.id.list_item_middle_container);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.textTitle.setText(mProjects.get(position).project_name);

            holder.textSubTitle.setText(mProjects.get(position).location);

            //touch events
            holder.primaryTouchTargetView.setEnabled(true);
            holder.primaryTouchTargetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mCallback.onMakerSelected(mProjects.get(mPosition).project_name);
                }
            });

            return convertView;
        }

        @Override
        public Filter getFilter()
        {
            return new Filter()
            {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence)
                {
                    FilterResults results = new FilterResults();

                    //If there's nothing to filter on, return the original data for your list
                    if(charSequence == null || charSequence.length() == 0)
                    {
                        results.values = originalData;
                        results.count = originalData.size();
                    }
                    else
                    {
                        ArrayList<ProjectDetail> filterResultsData = new ArrayList<ProjectDetail>();

                        for(int i=0; i < originalData.size(); i++)
                        {
                            //In this loop, you'll filter through originalData and compare each item to charSequence.
                            //If you find a match, add it to your new ArrayList
                            //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional

                            if(originalData.get(i).project_name.toLowerCase().contains(charSequence)){
                                filterResultsData.add(originalData.get(i));
                                //Log.wtf("fitler data", originalData.get(i).project_name);
                            }

                        }

                        results.values = filterResultsData;
                        results.count = filterResultsData.size();
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults)
                {
                    filteredData = (ArrayList<ProjectDetail>)filterResults.values;
                    mProjects = filteredData;
                    customAdapter.notifyDataSetChanged();
                }
            };
        }
    }

    public static List<ProjectDetail> getmAcceptedMakers() {
       return mAcceptedMakers;
    }
}
