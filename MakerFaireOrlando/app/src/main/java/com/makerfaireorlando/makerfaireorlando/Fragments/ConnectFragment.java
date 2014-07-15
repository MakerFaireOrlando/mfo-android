package com.makerfaireorlando.makerfaireorlando.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by conner on 6/16/13.
 */
public class ConnectFragment extends ListFragment {
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] values = new String[] { "MakerFaireOrlando.com",
                "Directions", "Google+", "Facebook", "Twitter"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //get item that was clicked

        Object o = this.getListAdapter().getItem(position);
        String keyword = o.toString();

        //launch intents when different list items are clicked
        if(keyword.equals("MakerFaireOrlando.com")){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.makerfaireorlando.com"));
            startActivity(browserIntent);
        }
        else if(keyword.equals("Directions")){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/maps?q=Orlando+Science+Center,+East+Princeton+Street,+Orlando,+FL&hl=en&ll=28.572481,-81.367836&spn=0.010949,0.021136&sll=27.698638,-83.804601&sspn=11.288453,21.643066&oq=orlando+sci&t=m&z=16"));
            startActivity(browserIntent);
        }
        else if(keyword.equals("Google+")){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/117567997621098640425/posts?utm_source=chrome_ntp_icon&utm_medium=chrome_app&utm_campaign=chrome"));
            startActivity(browserIntent);
        }
        else if(keyword.equals("Facebook")){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/orlandominimakerfaire"));
            startActivity(browserIntent);
        }else if(keyword.equals("Twitter")){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/OrlMakerFaire"));
            startActivity(browserIntent);
        }
    }
}