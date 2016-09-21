package com.makerfaireorlando.mforlando.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makerfaireorlando.mforlando.Activities.EventDetailActivity;
import com.makerfaireorlando.mforlando.Models.Schedule.Day;
import com.makerfaireorlando.mforlando.Models.Schedule.Event;
import com.makerfaireorlando.mforlando.R;

public class DayListAdapter extends RecyclerView.Adapter<DayListAdapter.ViewHolder> {
    private Day day;
    private Context mContext;

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mLocation;
        public TextView mStart;
        public TextView mEnd;
        public LinearLayout mLinearLayout;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.block_title);
            mLocation = (TextView) v.findViewById(R.id.block_subtitle);
            mStart = (TextView) v.findViewById(R.id.block_time);
            mEnd = (TextView) v.findViewById(R.id.block_endtime);
            mLinearLayout = (LinearLayout) v.findViewById(R.id.list_item_middle_container);
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getPosition();

                    Intent i = new Intent(mContext, EventDetailActivity.class);
                    i.putExtra(Constants.EVENT, day.events.get(pos));
                    mContext.startActivity(i);
                }
            });
        }
    }

    public DayListAdapter(Context context, Day day) {
        this.day = day;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_event, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event e = day.events.get(position);

        if (e != null) {
            holder.mTitle.setText(e.name);
            holder.mStart.setText(e.start_time);
            holder.mEnd.setText(e.end_time);
            holder.mLocation.setText(e.location);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return day.events.size();
    }
}
