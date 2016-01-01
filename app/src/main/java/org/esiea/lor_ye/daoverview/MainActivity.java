package org.esiea.lor_ye.daoverview;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button goButton = (Button)findViewById(R.id.go_button);
        goButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                startActivity(new Intent(context, ArtistListActivity.class));

            }
        });
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, new NotificationCompat.Builder(this)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .setContentTitle("Notification")
                .setContentText("Application démarrée").build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help:
                // Comportement du bouton "Aide"
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.menu_settings:
                // Comportement du bouton "Paramètres"
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
