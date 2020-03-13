package com.example.newsgateway;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;


public class AsyncCatSourceLoader extends AsyncTask<String, Integer, String> {

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;

    private static final String dataURL =
            "https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=b182d524e719444bbc70af0b09d201da";

    AsyncCatSourceLoader(MainActivity ma) {
        mainActivity = ma;
    }


    @Override
    protected void onPostExecute(String s) {
        HashMap<String, HashSet<String>> regionMap = parseJSON(s);
        if (regionMap != null) {
            mainActivity.setupRegions(regionMap);
        }
    }


    @Override
    protected String doInBackground(String... params) {


        Uri dataUri = Uri.parse(dataURL);
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

        return sb.toString();
    }


    private HashMap<String, HashSet<String>> parseJSON(String s) {

        HashMap<String, HashSet<String>> regionMap = new HashMap<>();
        try {

            // Start with a JSON Object
            JSONObject jObjMain = new JSONObject(s);
            String category;
            String name;

            // look for "sources" JSON Array in JSON Object
            JSONArray jSources = jObjMain.getJSONArray(("sources"));

                // read name and category
                for (int j = 0; j < jSources.length(); j++) {
                    JSONObject jCountry = (JSONObject) jSources.get(j);
                    name = jCountry.getString("id");
                    category = jCountry.getString("category");


                    if (category.isEmpty())
                        continue;

                        if (name.isEmpty())
                            name = "Unspecified";

                        // new category added to hash map
                    if (!regionMap.containsKey(category))
                        regionMap.put(category, new HashSet<String>());

                    // points to hashset(value) of this category
                    HashSet<String> rSet = regionMap.get(category);

                    // add to the hash set list of that hashmap category
                    if (rSet != null) {
                        rSet.add(name);
                    }

                }
            //}
            return regionMap;
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
