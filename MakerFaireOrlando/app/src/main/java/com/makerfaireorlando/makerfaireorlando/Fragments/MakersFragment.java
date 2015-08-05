package com.makerfaireorlando.makerfaireorlando.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makerfaireorlando.makerfaireorlando.Models.Exhibits.ProjectDetail;
import com.makerfaireorlando.makerfaireorlando.Models.Exhibits.ProjectsList;
import com.makerfaireorlando.makerfaireorlando.R;
import com.makerfaireorlando.makerfaireorlando.Network.MakerRestClient;
import com.makerfaireorlando.makerfaireorlando.Utils.ProjectsListAdapter;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MakersFragment extends Fragment
                            implements SearchView.OnQueryTextListener{

    private ProjectsList mProjectsList;
    private static List<ProjectDetail> mAcceptedMakers;
    Gson gson = new Gson();

    RecyclerView.Adapter mAdapter;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressBar mProgressBar;

    private int SPAN_COUNT = 2; // num columns in grid


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_makers, container, false);

        // get views
        mProgressBar = (ProgressBar) view.findViewById(R.id.maker_progress);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.maker_recycler_view);

        mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);

        this.setHasOptionsMenu(true);

        try {
            getItemList();
        } catch (JSONException e) {
            Log.wtf("MakersFragment", "Json exception at maker parse");
        }

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_projects, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(this);
    }

    public boolean onQueryTextChange(String newText) {
        // this is your adapter that will be filtered
        ((ProjectsListAdapter)mAdapter).getFilter().filter(newText);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        // this is your adapter that will be filtered
        ((ProjectsListAdapter)mAdapter).getFilter().filter(query);
        return true;
    }

    public void getItemList() throws JSONException {
        // show loading indicator
        mProgressBar.setVisibility(View.VISIBLE);

        MakerRestClient.get("makers-json", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject itemObject) {
                try {
                    // Parse json with gson
                    mProjectsList = gson.fromJson(itemObject.toString(), ProjectsList.class);
                    mAcceptedMakers = mProjectsList.accepteds;

                    //Sort the accepted makers
                    Collections.sort(mAcceptedMakers, new Comparator<ProjectDetail>() {
                        public int compare(ProjectDetail o1, ProjectDetail o2) {
                            return o1.project_name.compareToIgnoreCase(o2.project_name);
                        }
                    });

                    // Setup recyclerview
                    mAdapter = new ProjectsListAdapter(mAcceptedMakers, getActivity());
                    mRecyclerView.setAdapter(mAdapter);

                    // Hide progress indicator
                    mProgressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.wtf("com.makerfaireorlando.makerfaireorlando.MainActivity", "Exception at GSON parse");
                }
            }
        });
    }
}
