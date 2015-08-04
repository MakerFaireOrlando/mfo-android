package com.makerfaireorlando.makerfaireorlando.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makerfaireorlando.makerfaireorlando.Models.Calendar;
import com.makerfaireorlando.makerfaireorlando.Models.Items;
import com.makerfaireorlando.makerfaireorlando.R;
import com.makerfaireorlando.makerfaireorlando.Utils.EventRestClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventsFragment extends ListFragment {
    String mCacheJSONString;
    static List<Items> mCalendarItems;
    OnEventSelectedListener mCallback;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        try {
            getItemList();
        } catch (JSONException e) {
            Log.wtf("EventsFragment", "Exception at JSON parse");
        }
    }

    public void getItemList() throws JSONException {

        // TODO: Enter api key for calendar res > values > keys.xml

        EventRestClient.get(getString(R.string.calendar_key), null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject itemObject) {
                mCacheJSONString = itemObject.toString();
                //Parses JSON String with GSON
                parseItemList(mCacheJSONString);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void parseItemList(String jsonString){
        try {
            Gson gson = new Gson();
            Calendar calendar = gson.fromJson(jsonString, Calendar.class);
            mCalendarItems = calendar.items;

            Collections.sort(mCalendarItems, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Items p1 = (Items) o1;
                    Items p2 = (Items) o2;
                    return p1.start.dateTime.compareToIgnoreCase(p2.start.dateTime);
                }
            });

            CalendarListAdapter customAdapter = new CalendarListAdapter(getActivity(), mCalendarItems);
            setListAdapter(customAdapter);

        } catch (Exception e) {
            Log.wtf("com.makerfaireorlando.makerfaireorlando.MainActivity", "Exception at GSON parse");
        }
    }

    public static List<Items> getmCalendarItems(){
        return mCalendarItems;
    }

    public interface OnEventSelectedListener {
        void onEventSelected(Items event);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnEventSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mCallback.onEventSelected(mCalendarItems.get(position));
    }

    private class CalendarListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<Items> items;

        private class ViewHolder {
            TextView startTime;
            TextView endTime;
            TextView textTitle;
            TextView textSubTitle;
            View primaryTouchTargetView;
        }

        public CalendarListAdapter(Context context, List<Items> items) {
            inflater = LayoutInflater.from(context);
            this.items = items;
        }

        public int getCount() {
            return items.size();
        }

        public Items getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            final int mPosition = position;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_event, null);
                holder.startTime = (TextView) convertView.findViewById(R.id.block_time);
                holder.endTime = (TextView) convertView.findViewById(R.id.block_endtime);
                holder.textTitle = (TextView) convertView.findViewById(R.id.block_title);
                holder.textSubTitle = (TextView) convertView.findViewById(R.id.block_subtitle);
                holder.primaryTouchTargetView =  convertView.findViewById(R.id.list_item_middle_container);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // "2013-10-05T16:00:00-04:00"
            //parse times
            long mStartDate = 0;
            long mEndDate = 0;
            String startTime = items.get(position).start.dateTime;
            String endTime = items.get(position).end.dateTime;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

            try {
                mStartDate = sdf.parse(startTime).getTime();
                mEndDate= sdf.parse(endTime).getTime();
            } catch(ParseException e) {
                Log.wtf("Calendar List Adapter", "parse exception for date time");
            }

            DateFormat df = new SimpleDateFormat("h:mm a");
            startTime = df.format(mStartDate);
            endTime = df.format(mEndDate);

            holder.startTime.setText(startTime);
            holder.endTime.setText(endTime);
            holder.textTitle.setText(items.get(position).summary);
            holder.textSubTitle.setText(items.get(position).location);
            holder.primaryTouchTargetView.setEnabled(true);

            // setup click listeners
            holder.primaryTouchTargetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onEventSelected(mCalendarItems.get(mPosition));
                }
            });

            return convertView;
        }
    }
}