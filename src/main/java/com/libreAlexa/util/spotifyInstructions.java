package com.libreAlexa.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity;
import com.libreAlexa.R;
import java.util.Locale;

public class spotifyInstructions extends CTDeviceDiscoveryActivity implements View.OnClickListener {
    private TextView openSpotify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_instructions);
        ImageButton m_back = (ImageButton) findViewById(R.id.back);
        m_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        openSpotify = (TextView) findViewById(R.id.openSpotify);
        TextView learnMore = (TextView) findViewById(R.id.learnMore);
        learnMore.setTextColor(getResources().getColor(R.color.spotify_color));
        openSpotify.setOnClickListener(this);
        Locale.getDefault().getDisplayLanguage();
        learnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://spotify.com/connect");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!openApp(spotifyInstructions.this, "com.spotify.music")) {
            openSpotify.setText("GET SPOTIFY FREE");
        }
        else{
            openSpotify.setText("OPEN SPOTIFY APP");
        }
    }
    public static void launchPlayStoreWithAppPackage(Context context, String packageName) {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
        context.startActivity(i);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.openSpotify) {

            if (!LaunchApp(spotifyInstructions.this,"com.spotify.music"))
                launchPlayStoreWithAppPackage(spotifyInstructions.this, "com.spotify.music");
        }
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
            }

            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
    public static boolean LaunchApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
