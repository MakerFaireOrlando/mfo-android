package com.makerfaireorlando.makerfaireorlando.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.makerfaireorlando.makerfaireorlando.Activities.MakerDetailActivity;
import com.makerfaireorlando.makerfaireorlando.Models.Maker;
import com.makerfaireorlando.makerfaireorlando.Models.ProjectDetail;
import com.makerfaireorlando.makerfaireorlando.Models.ProjectsList;
import com.makerfaireorlando.makerfaireorlando.R;
import com.makerfaireorlando.makerfaireorlando.Utils.MakerRestClient;
import com.makerfaireorlando.makerfaireorlando.Utils.ProjectsListAdapter;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MakersFragment extends Fragment
                            implements SearchView.OnQueryTextListener{

    private ProjectsList mProjectsList;
    private static List<ProjectDetail> mAcceptedMakers;
    Gson gson = new Gson();

    OnMakerSelectedListener mCallback;
    RecyclerView.Adapter mAdapter;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private int SPAN_COUNT = 2; // num columns in grid


    /*
    public static MakersFragment newInstance(int index) {
        MakersFragment f = new MakersFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_makers, container, false);

        // set up recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.maker_recycler_view);

        mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(getActivity(), MakerDetailActivity.class);
                startActivityForResult(d, 1);
            }
        });

        this.setHasOptionsMenu(true);
        try {
            getItemList();
        } catch (JSONException e) {
            Log.wtf("MakersFragment", "Json exception at maker parse");
        }

        return view;
    }


        @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    // Container Activity must implement this interface
    public interface OnMakerSelectedListener {
       void onMakerSelected(ProjectDetail projectDetail);
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
        //mAdapter.getFilter().filter(newText);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        // this is your adapter that will be filtered
        //mAdapter.getFilter().filter(query);
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

    public void getItemList() throws JSONException {

        MakerRestClient.get("makers-json", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject itemObject) {
                parseItemList(itemObject.toString());
            }
        });
    }

    public void parseItemList(String jsonString){
        try {
            mProjectsList = gson.fromJson(jsonString, ProjectsList.class);
            mAcceptedMakers = mProjectsList.accepteds;

            //Sort the accepted makers
            Collections.sort(mAcceptedMakers, new Comparator() {

                public int compare(Object o1, Object o2) {
                    ProjectDetail p1 = (ProjectDetail) o1;
                    ProjectDetail p2 = (ProjectDetail) o2;
                    return p1.location.compareToIgnoreCase(p2.location);
                }

            });

            //Custom list adapter for custom list view

            mAdapter = new ProjectsListAdapter(mAcceptedMakers, getActivity());
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            Log.wtf("com.makerfaireorlando.makerfaireorlando.MainActivity", "Exception at GSON parse");
        }
    }

    /*
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

        public void setAcceptedMakers(List<ProjectDetail> mProjects) {
            this.mProjects = mProjects;
            originalData = this.mProjects;
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
                convertView = inflater.inflate(R.layout.list_item_maker, null);
                holder.textTitle = (TextView) convertView.findViewById(R.id.block_title);
                holder.textSubTitle = (TextView) convertView.findViewById(R.id.block_subtitle);
                holder.primaryTouchTargetView =  convertView.findViewById(R.id.list_item_middle_container);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.textTitle.setText(mProjects.get(position).project_name);

            // TODO This should probably be location when they are available
            holder.textSubTitle.setText(mProjects.get(position).location);

            //touch events
            holder.primaryTouchTargetView.setEnabled(true);
            holder.primaryTouchTargetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onMakerSelected(mProjects.get(mPosition));
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
    */
}
