package com.makerfaireorlando.makerfaireorlando.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makerfaireorlando.makerfaireorlando.Models.Schedule.Schedule;
import com.makerfaireorlando.makerfaireorlando.R;
import com.makerfaireorlando.makerfaireorlando.Network.ScheduleRestClient;

import org.apache.http.Header;
import org.json.JSONObject;

public class ScheduleFragment extends Fragment {
    Gson gson = new Gson();
    Schedule mSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // get schedules
        ScheduleRestClient.get("mfo_devel_sched.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mSchedule = gson.fromJson(response.toString(), Schedule.class);

                ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
                pager.setAdapter(new MyPagerAdapter(getChildFragmentManager(), mSchedule));

                // Bind the tabs to the ViewPager
                PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
                tabs.setShouldExpand(true);
                tabs.setTextColor(Color.WHITE);
                tabs.setIndicatorColor(Color.WHITE);
                tabs.setViewPager(pager);
            }
        });
        return view;
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        private Schedule schedule;

        public MyPagerAdapter(FragmentManager fm, Schedule schedule) {
            super(fm);
            this.schedule = schedule;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return schedule.days.get(position).date_title;
        }

        @Override
        public int getCount() {
            return schedule.days.size();
        }

        @Override
        public Fragment getItem(int position) {
            return DayFragment.newInstance(schedule.days.get(position));
        }
    }

}