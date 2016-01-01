package org.esiea.lor_ye.daoverview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class GalleryActivity extends AppCompatActivity {

    private HashMap<String,String> jsonDataMap;

    private ImageView imageView;
    private TextView titleView1;
    private TextView titleView2;
    private TextView titleView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        jsonDataMap = new HashMap<String,String>();
        imageView = (ImageView)findViewById(R.id.imageView);
        titleView1 = (TextView)findViewById(R.id.titleView1);
        titleView2 = (TextView)findViewById(R.id.titleView2);
        titleView3 = (TextView)findViewById(R.id.titleView3);

        IntentFilter intentFilter1 = new IntentFilter(DADownloadService.DATA_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new DataUpdate(), intentFilter1);

        this.loadContent();
    }

    private void loadContent() {
        Context context = this.getBaseContext();
        Intent thisIntent = getIntent();
        // Get metadata
        String author = thisIntent.getStringExtra("author");
        String[] metadataUrlBaseArray = thisIntent.getStringArrayExtra("metadataUrlBase");

        for (int i = 0; i < 3; i++) {
            String metadataUrlBase = metadataUrlBaseArray[i];
            Log.d("[YES]", "metadata: " + metadataUrlBase);
            // Build download URL
            String metadataUrl = buildDAURL(author, metadataUrlBase);
            File fileToDownload;

            // Check for existing file
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) // Has access to sdcard
                fileToDownload = new File(getExternalFilesDir(null), metadataUrlBase + ".json");
            else fileToDownload = new File(getFilesDir(), metadataUrlBase + ".json");

            if (true) {//(!fileToDownload.exists() || !fileToDownload.canRead()) {
                //Toast.makeText(context, "Download started!", Toast.LENGTH_SHORT).show();
                Log.d("[YES]", "loading content at " + metadataUrl);

                Intent intent = new Intent(context, DADownloadService.class);
                intent.setAction(DADownloadService.ACTION_DOWNLOAD);
                intent.putExtra("url", metadataUrl);
                intent.putExtra("type", "metadata" + i);
                context.startService(intent);

                //Toast.makeText(context, "Download finished!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String buildDAURL(String author, String image) {
        String res = "http://backend.deviantart.com/oembed?url=http://" + author + ".deviantart.com"
                +  "/art/" + image;
        return res;
    }

    public class DataUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            Log.d("[YES T]", "type: " + type);

            if (!intent.getAction().equals(DADownloadService.DATA_UPDATE)) {
                Toast.makeText(context, "Data error", Toast.LENGTH_SHORT).show();
                return;
            }

            if (type.startsWith("metadata")) {
                int i = Integer.parseInt(type.substring(type.length() - 1));
                //Toast.makeText(context, "Data update "+i, Toast.LENGTH_SHORT).show();
                // prévoir une action de notification ici

                String url = intent.getStringExtra("url");
                String jsonData = null;
                try {
                    jsonData = new String(intent.getByteArrayExtra("data"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("[YES3]", url + ": " + jsonData);
                jsonDataMap.put(url, jsonData);
                Log.d("[YES4]", jsonDataMap.toString());

                writeStringToFile(jsonData, new File(getExternalFilesDir(null), getLastBitFromUrl(url) + ".json"));
                //getExternalMediaDirs()[0]
                Log.d("[YES5]", "after writing to file");

                /* Launch image download */
                File fileToDownload;
                String imageTitle = null, thumbUrl = null;
                try {
                    JSONObject json = new JSONObject(jsonData);
                    imageTitle = json.getString("title");
                    thumbUrl = json.getString("thumbnail_url_200h");
                    /*imageMetadata.authorName = json.getString("author_name");
                    imageMetadata.authorUrl = json.getString("author_url");
                    imageMetadata.imgUrl = json.getString("url");*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (i) {
                    case 0: titleView1.setText(imageTitle); break;
                    case 1: titleView2.setText(imageTitle); break;
                    case 2: titleView3.setText(imageTitle); break;
                }

                // Check for existing file
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) // Has access to sdcard
                    fileToDownload = new File(getExternalFilesDir(null), imageTitle);
                else fileToDownload = new File(getFilesDir(), imageTitle);

                if (false) {//(!fileToDownload.exists() || !fileToDownload.canRead()) {

                    Log.d("[YES]", "Thumbnail: " + thumbUrl);
                    Intent intent2 = new Intent(context, DADownloadService.class);
                    intent.setAction(DADownloadService.ACTION_DOWNLOAD);
                    intent.putExtra("url", "http://orig07.deviantart.net/328f/f/2010/325/3/b/ammakou_and_chibikou_by_kaze_hime-d33ck1b.jpg");
                    intent.putExtra("type", "image");
                    context.startService(intent);
                }

            } else if (type.startsWith("image")) {
                Log.d("[YES]", "Creating image");
                byte[] imageBytes = intent.getByteArrayExtra("data");
                Bitmap image = bytesToImage(imageBytes);
                imageView.setImageBitmap(image);
            }
        }
    }

    public static String getLastBitFromUrl(final String url){
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    /* IO functions */

    private void writeStringToFile(String txt, File file) {

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(txt.getBytes());
            stream.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            e.printStackTrace();
        }
    }

    private byte[] readStringFromFile(File file) {
        try
        {
            InputStream is = new FileInputStream(file);
            byte[] buffer =  new byte[is.available()];
            is.read(buffer);
            is.close();
            return buffer;//.toString();
        }
        catch (IOException e) {
            Log.e("Exception", "File read failed: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    private void writeBytesToFile(byte[] bytes, File file) {

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            e.printStackTrace();
        }
    }

    private Bitmap bytesToImage(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
