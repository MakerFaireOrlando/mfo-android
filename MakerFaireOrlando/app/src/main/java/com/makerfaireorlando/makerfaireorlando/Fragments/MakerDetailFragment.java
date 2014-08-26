package com.makerfaireorlando.makerfaireorlando.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.makerfaireorlando.makerfaireorlando.Utils.DownloadImageTask;
import com.makerfaireorlando.makerfaireorlando.Models.ProjectDetail;
import com.makerfaireorlando.makerfaireorlando.R;

import java.util.List;

/**
 * Created by conner on 9/12/13.
 */
public class MakerDetailFragment extends Fragment {
    public static ProjectDetail projectDetail;
    List<ProjectDetail> projectList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_project_detail, container, false);

        projectList = MakersFragment.getmAcceptedMakers();
        String name = getArguments().getString("Name");

        for(ProjectDetail project : projectList)
            if(project.project_name.equals(name))
                projectDetail = project;

        setHasOptionsMenu(true);

        if(projectDetail.photo_link != null) {
            new DownloadImageTask((ImageView) rootView.findViewById(R.id.projectImageHeader))
                    .execute(projectDetail.photo_link);
        }

        TextView title = (TextView) rootView.findViewById(R.id.titleText);
        title.setText(projectDetail.project_name);

        TextView location = (TextView) rootView.findViewById(R.id.location);
        if(projectDetail.location != null){
            location.setText(projectDetail.location);
        }else{
            location.setVisibility(View.GONE);
        }

        TextView webSite = (TextView) rootView.findViewById(R.id.website);
        if(projectDetail.web_site != null){
            webSite.setText(Html.fromHtml(projectDetail.web_site));
        }else{
            webSite.setVisibility(View.GONE);
        }

        webSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(projectDetail.web_site.substring(0, 3).equals("www")){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + projectDetail.web_site));
                    startActivity(browserIntent);
                }else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(projectDetail.web_site));
                    startActivity(browserIntent);
                }
            }
        });

        TextView organization = (TextView) rootView.findViewById(R.id.organization);

        if(projectDetail.organization != null){
            organization.setText(projectDetail.organization);
        }else{
            organization.setVisibility(View.GONE);
        }

        TextView descriptionView = (TextView) rootView.findViewById(R.id.descriptionText);
        descriptionView.setText(projectDetail.description);

        Button youTubeButton = (Button) rootView.findViewById(R.id.youtubebutton);
        if (projectDetail.video_link != null) {
            youTubeButton.setVisibility(View.VISIBLE);
            youTubeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(projectDetail.video_link));
                    startActivity(browserIntent);
                }
            });
        } else {
            youTubeButton.setVisibility(View.GONE);
        }
        return rootView;
    }
}
