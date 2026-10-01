package com.gulshanfactory.widgets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Launcher entry point. It simply opens the Gulshan Factory PWA in the
 * browser and closes immediately — the real "app" is the website, the
 * home-screen widgets are the native Android part.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(WidgetHelper.APP_URL));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
