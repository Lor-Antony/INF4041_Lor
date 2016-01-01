package org.esiea.lor_ye.daoverview;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DADownloadService extends IntentService {

    public static final String ACTION_DOWNLOAD = "fr.esiea.ye.test1.action.DOWNLOAD";
    public static final String DATA_UPDATE = "fr.esiea.ye.test1.DATA_UPDATE";
    public static final String DATA_ERROR = "fr.esiea.ye.test1.DATA_ERROR";

    private String url;
    private String type;

    public DADownloadService() {
        super("DADownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                this.type = intent.getStringExtra("type");
                this.url = intent.getStringExtra("url");
                Log.d("[YES D]", "DownloadService: " + type + " " + url);
                handleActionDownload();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownload() {
        try {
            URL url = new URL("http://backend.deviantart.com/oembed?url=" + URLEncoder.encode(this.url, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                byte[] byteArray = inputStreamToBytes(conn.getInputStream());
                Log.d("[YES1]", "Data downloaded!");

                // Broadcast downloaded data
                Intent broadcastIntent = new Intent(DATA_UPDATE);
                broadcastIntent.putExtra("url", this.url);
                broadcastIntent.putExtra("data", byteArray);
                broadcastIntent.putExtra("type", this.type);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            }
        } catch (MalformedURLException e) {
            Log.d("[YES]", "MalformedURLException");
            e.printStackTrace();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(DATA_ERROR));
        } catch (IOException e) {
            Log.d("[YES]", "IOException");
            e.printStackTrace();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(DATA_ERROR));
        }

    }

    private byte[] inputStreamToBytes(InputStream in){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            Log.e("Exception", "Input read failed: " + e.toString());
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
