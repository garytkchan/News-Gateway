package com.example.newsgateway;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */



public class StoryFragment extends Fragment {

    private static final String frontURL =
            "https://newsapi.org/v2/top-headlines?sources=";

    private static final String rearURL =
            "&language=en&apiKey=b182d524e719444bbc70af0b09d201da";


    public StoryFragment() {
        // Required empty public constructor
    }


    public static StoryFragment newInstance(Story news,
                                            int index, int max)
    {

        StoryFragment f = new StoryFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("COUNTRY_DATA", news);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_story, container, false);

        Bundle args = getArguments();
        if (args != null) {
            final Story currentNews = (Story) args.getSerializable("COUNTRY_DATA");
            if (currentNews == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            // set title
            TextView title = fragment_layout.findViewById(R.id.title);
            title.setText(currentNews.getTitle());
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickFlag(currentNews.getUrl());
                }
            });

            // set date
            TextView date = fragment_layout.findViewById(R.id.date);
            date.setText(currentNews.getPublishedAt());

            // set author
            TextView author = fragment_layout.findViewById(R.id.author);
            author.setText(currentNews.getAuthor());

            // set description
            TextView description = fragment_layout.findViewById(R.id.description);
            description.setText(currentNews.getDescription());
            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickFlag(currentNews.getUrl());
                }
            });

            // set page number
            TextView pageNum = fragment_layout.findViewById(R.id.page_num);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            // set image
            ImageView imageView = fragment_layout.findViewById(R.id.imageView);
            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            imageView.setImageDrawable(currentNews.getDrawable());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickFlag(currentNews.getUrl());
                }
            });
            return fragment_layout;
        } else {
            return null;
        }
    }



    public void clickFlag(String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

        /*
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(name));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
        */
    }


}
