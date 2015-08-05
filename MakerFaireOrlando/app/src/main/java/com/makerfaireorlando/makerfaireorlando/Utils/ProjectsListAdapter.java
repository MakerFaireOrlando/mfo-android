package com.makerfaireorlando.makerfaireorlando.Utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.makerfaireorlando.makerfaireorlando.Activities.MakerDetailActivity;
import com.makerfaireorlando.makerfaireorlando.Models.Exhibits.ProjectDetail;
import com.makerfaireorlando.makerfaireorlando.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

public class ProjectsListAdapter extends RecyclerView.Adapter<ProjectsListAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private List<ProjectDetail> mDataset;
    private View.OnClickListener clickListener;


    private List<ProjectDetail> originalData;
    private List<ProjectDetail> filteredData;

    public int itemHeight;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public FrameLayout mCardView;
        public TextView mTextView;
        public TextView mSubtitle;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mCardView = (FrameLayout) v.findViewById(R.id.card_view);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getPosition();
                    ProjectDetail s = mDataset.get(pos);

                    Intent d = new Intent(mContext, MakerDetailActivity.class);
                    d.putExtra(Constants.PROJECT, s);
                    mContext.startActivity(d);
                }
            });
            mTextView = (TextView) v.findViewById(R.id.item_text);
//            mSubtitle = (TextView) v.findViewById(R.id.item_subtitle);
            mTextView.setSelected(true);
            mImageView = (ImageView) v.findViewById(R.id.list_item_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProjectsListAdapter(List<ProjectDetail> list, Context context) {
        mDataset = list;
        originalData = this.mDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProjectsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_maker, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        // to prevent stale images from being displayed, unset the image initially
        holder.mImageView.setImageBitmap(null);

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final ProjectDetail p = mDataset.get(position);
        holder.mTextView.setText(p.project_name);

        // image displaying
        String pic = p.photo_link;
        final ImageView img = holder.mImageView;

        // nasty, nasty hack
        float height = mContext.getResources().getDimension(R.dimen.item_image_height);
        ImageSize size = new ImageSize((int) (height * 2), (int) height);

        /* TODO: profile view circle image view is not being set. fix this */

        if (pic != null) {
            ImageLoader.getInstance().loadImage(pic, size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    img.setImageBitmap(loadedImage);

                    if (!p.hasColor) {
                        findColors(loadedImage, p);
                    }
                    if (p.hasColor) {
                        holder.mTextView.setBackgroundColor(p.color);
                        holder.mTextView.setTextColor(p.textColor);
                    }
                }
            });
        } else {
            Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.makey);
            holder.mImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.makey));
            if (!p.hasColor) {
                findColors(b, p);
            }
            if (p.hasColor){
                holder.mTextView.setBackgroundColor(p.color);
                holder.mTextView.setTextColor(p.textColor);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void findColors(Bitmap b, ProjectDetail s) {
        Palette palette = Palette.generate(b);
        Palette.Swatch light = palette.getVibrantSwatch();
        Palette.Swatch dark = palette.getDarkVibrantSwatch();
        if (light != null && dark != null) {
            s.color = light.getRgb();
            s.textColor = light.getTitleTextColor();
            s.darkColor = dark.getRgb();

            s.hasColor = true;
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Filter.FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = originalData;
                    results.count = originalData.size();
                } else {
                    ArrayList<ProjectDetail> filterResultsData = new ArrayList<ProjectDetail>();

                    for (int i = 0; i < originalData.size(); i++) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional

                        if (originalData.get(i).project_name.toLowerCase().contains(charSequence)) {
                            filterResultsData.add(originalData.get(i));
                        }

                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (ArrayList<ProjectDetail>) filterResults.values;
                mDataset = filteredData;
                notifyDataSetChanged();
            }
        };
    }
}

