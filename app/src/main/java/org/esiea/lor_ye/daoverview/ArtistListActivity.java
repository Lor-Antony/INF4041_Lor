package org.esiea.lor_ye.daoverview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ArtistListActivity extends AppCompatActivity {

    private HashMap<String,String[]> datasetMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);

        EditText searchView = (EditText)findViewById(R.id.searchEditText);
        searchView.setOnEditorActionListener(new SearchLaunchListener());

        this.populateDatasetMap();
        //String[] dataset = datasetMap.keySet().toArray(new String[datasetMap.size()]);
        //Log.d("[YES]", "Dataset: "); for (String s : dataset) Log.d("[YES]", s);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.Adapter adapter = new DAAdapter(datasetMap);
        recyclerView.setAdapter(adapter);
    }

    private void populateDatasetMap() {
        HashMap<String,String[]> datasetMap = new HashMap<String,String[]>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("dataset.txt")));
            String strLine, currentAuthor = "";
            ArrayList<String> metadataUrlBase = new ArrayList<String>();

            while ((strLine = br.readLine()) != null)   {
                if (!strLine.startsWith("#")) {
                    if (strLine.trim().equals("")) {
                        datasetMap.put(currentAuthor, metadataUrlBase.toArray(new String[metadataUrlBase.size()]));
                        currentAuthor = "";
                        metadataUrlBase.clear();
                    } else if (currentAuthor.equals("")) {
                        currentAuthor = strLine;
                    } else {
                        metadataUrlBase.add(strLine);
                    }
                }
            }
            datasetMap.put(currentAuthor, metadataUrlBase.toArray(new String[metadataUrlBase.size()]));
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.datasetMap = datasetMap;
    }

    private String buildDAURL(String author, String image) {
        String res = "http://backend.deviantart.com/oembed?url=http://" + author + ".deviantart.com";
        if (image != null) res += "/art/" + image;
        return res;
    }


    class SearchLaunchListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView searchView, int i, KeyEvent keyEvent) {
            Toast.makeText(searchView.getContext(), searchView.getText(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    class ImageMetadata {

        private String title;
        private String authorName;
        private String authorUrl;
        private String imgUrl;
        private String thumbnailUrl;

        public String getAuthorName() { return authorName; }
        public void setAuthorName(String authorName) { this.authorName = authorName; }

        public String getAuthorUrl() { return authorUrl; }
        public void setAuthorUrl(String authorUrl) { this.authorUrl = authorUrl; }

        public String getImgUrl() { return imgUrl; }
        public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    public ImageMetadata parseJsonToImgMetadata(String jsonString) {
        ImageMetadata imageMetadata = new ImageMetadata();
        try {
            JSONObject json = new JSONObject(jsonString);
            imageMetadata.title = json.getString("title");
            imageMetadata.authorName = json.getString("author_name");
            imageMetadata.authorUrl = json.getString("author_url");
            imageMetadata.imgUrl = json.getString("url");
            imageMetadata.thumbnailUrl = json.getString("thumbnail_url_200h");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageMetadata;
    }

}
