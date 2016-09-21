package com.makerfaireorlando.mforlando.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makerfaireorlando.mforlando.Models.Schedule.Day;
import com.makerfaireorlando.mforlando.R;
import com.makerfaireorlando.mforlando.Utils.Constants;
import com.makerfaireorlando.mforlando.Utils.DayListAdapter;

public class DayFragment extends Fragment {

    private Day mDay;

    private DayListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    public static DayFragment newInstance(Day day) {
        DayFragment f = new DayFragment();

        Bundle args = new Bundle();
        args.putSerializable(Constants.DAY, day);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_day, container, false);

        Bundle args = getArguments();
        mDay = (Day) args.getSerializable(Constants.DAY);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.day_recycler_view);
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DayListAdapter(getActivity(), mDay);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}
