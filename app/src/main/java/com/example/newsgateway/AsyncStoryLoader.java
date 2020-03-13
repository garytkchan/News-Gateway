package com.example.newsgateway;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;

public class AsyncStoryLoader extends AsyncTask<String, Integer, ArrayList<Story>> {

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private String selectedSubRegion;

    private static final String dataURL =
            "https://newsapi.org/v2/top-headlines?sources=cnn&language=en&apiKey=b182d524e719444bbc70af0b09d201da";

    private static final String frontURL =
            "https://newsapi.org/v2/top-headlines?sources=";

    private static final String rearURL =
            "&language=en&apiKey=b182d524e719444bbc70af0b09d201da";

    AsyncStoryLoader(MainActivity ma) {
        mainActivity = ma;
    }


    @Override
    protected void onPostExecute(ArrayList<Story> storyList) {
        mainActivity.setCountries(storyList);
    }


    @Override
    protected ArrayList<Story> doInBackground(String... params) {

        selectedSubRegion = params[0];

        Uri dataUri = Uri.parse(frontURL + selectedSubRegion + rearURL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return parseJSON(sb.toString());



    }


    private ArrayList<Story> parseJSON(String s) {

        ArrayList<Story> storyList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            String id, name, author, title, description, url, urlToImage, publishedAt;

            // look for "sources" JSON Array in JSON Object
            JSONArray jArticles = jObjMain.getJSONArray(("articles"));

            // read name and category
            for (int j = 0; j < jArticles.length(); j++) {

                JSONObject jCountry = (JSONObject) jArticles.get(j);

                JSONObject jSource = jCountry.getJSONObject("source");
                id = jSource.getString("id");
                name = jSource.getString("name");

                if (name.isEmpty())
                    name = "Unspecified";

                author = jCountry.getString("author");
                title = jCountry.getString("title");
                description = jCountry.getString("description");
                url = jCountry.getString("url");
                urlToImage = jCountry.getString("urlToImage");
                publishedAt = jCountry.getString("publishedAt");

                /*
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDateTime ldateTime = LocalDateTime.parse(publishedAt, dateFormatter);
                publishedAt = dateFormatter.format(ldateTime);
                */

                DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                Date date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(publishedAt);
                publishedAt = formatter.format((date1));

                Drawable drawable = null;
                try {
                    InputStream input = new java.net.URL(urlToImage).openStream();
                    drawable = Drawable.createFromStream(input, "src name");
                    //SVG svg = SVG.getFromInputStream(input);
                    //drawable = new PictureDrawable(svg.renderToPicture());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                storyList.add(new Story(id, name, author, title, description,
                        url, publishedAt, drawable));
            }

            return storyList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
