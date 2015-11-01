package com.ucoz.handyby.mrocourse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class ScreenshotActivity extends AppCompatActivity implements IConstants {

    private ImageView imageViewScreenshot;
    private String mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Binarization", Snackbar.LENGTH_LONG)
                        .setAction("Enter by user", null)
                        .setAction("Default (128)", null)
                        .setAction("Method of 120", null).show();*/
                startBinarizationActivity();
            }
        });

        imageViewScreenshot = (ImageView) findViewById(R.id.image_view_screenshot);

        Intent parentIntent = getIntent();
        if (parentIntent != null) {
            Bundle parentBandle = parentIntent.getExtras();
            if (parentBandle != null) {
                String screenshotPath = parentBandle.getString(BUNDLE_PATH);
                mImagePath = screenshotPath;
                if (!TextUtils.isEmpty(screenshotPath)) {
                    Uri imageUri = Uri.fromFile(new File(screenshotPath));
                    imageViewScreenshot.setImageURI(imageUri);
                }
            }
        }
    }

    private void startBinarizationActivity() {
        Intent activityIntent = new Intent(this, BinarizationActivity.class);
        activityIntent.putExtra(BUNDLE_PATH, mImagePath);
        startActivity(activityIntent);
    }

}
