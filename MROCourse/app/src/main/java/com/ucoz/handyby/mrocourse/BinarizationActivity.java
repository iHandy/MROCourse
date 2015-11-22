package com.ucoz.handyby.mrocourse;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ucoz.handyby.mrocourse.Views.RowsGystogramView;
import com.ucoz.handyby.mrocourse.processors.Binarizer;
import com.ucoz.handyby.mrocourse.processors.ColorWordsProcessor;
import com.ucoz.handyby.mrocourse.processors.GystMember;
import com.ucoz.handyby.mrocourse.processors.GystogramBuilder;
import com.ucoz.handyby.mrocourse.processors.PartImageMember;
import com.ucoz.handyby.mrocourse.processors.SpacesHolder;

import java.io.File;
import java.util.ArrayList;

public class BinarizationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IConstants {

    public static final int DEFAULT_THRESHOLD = 128;
    public static final double ROWS_GYSTOGRAM_SUBSTRACT_PERCENT = 0.3;
    private String mUserThreshold = String.valueOf(DEFAULT_THRESHOLD);

    private ImageView imageViewScreenshot;
    private String mImagePath;
    private RowsGystogramView mGystView;

    private ArrayList<GystMember> mRowsGystogram = null;
    private ArrayList<GystMember> mSpacesInRowsGytogram = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binarization);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        mGystView = (RowsGystogramView) findViewById(R.id.gyst_view);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.binarization, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bin_by_user) {

            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.dialog_threshold, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mUserThreshold = userInput.getText().toString();
                                    if (!TextUtils.isEmpty(mUserThreshold)) {
                                        int threshold = DEFAULT_THRESHOLD;
                                        try {
                                            threshold = Integer.parseInt(mUserThreshold);
                                        } catch (NumberFormatException e) {
                                            Toast.makeText(BinarizationActivity.this, "Invalid threshold! Input again,", Toast.LENGTH_SHORT).show();
                                        }
                                        startBinarization(threshold);
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (id == R.id.nav_bin_default) {
            startBinarization(DEFAULT_THRESHOLD);
        } else if (id == R.id.nav_bin_method_120) {
            start120Binarization();
        } else if (id == R.id.nav_gyst_rows) {
            drawRowsGystogram();
        } else if (id == R.id.nav_gyst_full) {
            showRowsGystOnFullScreen();
        } else if (id == R.id.nav_gyst_substract30) {
            substract30();
        } else if (id == R.id.nav_gyst_spaces_in_rows) {
            getSpacesInRowsGystogram();
        } else if (id == R.id.nav_segment_color_words) {
            drawColorWords();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void drawColorWords() {
        if (mSpacesInRowsGytogram == null || mSpacesInRowsGytogram.isEmpty()) {
            Toast.makeText(this, "Please, create spaces gystogram!", Toast.LENGTH_SHORT).show();
            return;
        }
        SpacesHolder spacesHolder = new SpacesHolder(mSpacesInRowsGytogram);
        ColorWordsProcessor colorWordsProcessor = new ColorWordsProcessor(mImagePath, mRowsGystogram, spacesHolder);
        colorWordsProcessor.detectAndPaintWords();

        reloadImageView();

        ArrayList<PartImageMember> pretendents = colorWordsProcessor.getPretendents();

    }

    private void getSpacesInRowsGystogram() {
        if (mSpacesInRowsGytogram == null) {
            initSpacesInRowsGystogram();
        }

        Intent fullActivityIntent = new Intent(this, GystogramActivity.class);
        fullActivityIntent.putExtra(BUNDLE_GYSTOGRAM, mSpacesInRowsGytogram);
        fullActivityIntent.putExtra(BUNDLE_GYSTOGRAM_TYPE, 2);
        startActivity(fullActivityIntent);
    }

    private void substract30() {
        if (mRowsGystogram == null) {
            initRowsGystogram();
        }

        int max = Integer.MIN_VALUE;
        for (GystMember member : mRowsGystogram) {
            if (member.count > max) {
                max = member.count;
            }
        }

        int skip = 80;
        int substrValue = (int) (max * ROWS_GYSTOGRAM_SUBSTRACT_PERCENT);
        for (GystMember member : mRowsGystogram) {
            if (skip != 0)
            {
                skip--;
                if (member.count > 0) {
                    member.count = member.count * 5 - substrValue;
                    if (member.count < 0) {
                        member.count = 0;
                    }
                }
            } else
            if (member.count > 0) {
                member.count -= substrValue;
                if (member.count < 0) {
                    member.count = 0;
                }
            }
        }
    }

    private void showRowsGystOnFullScreen() {
        if (mRowsGystogram == null) {
            initRowsGystogram();
        }

        Intent fullActivityIntent = new Intent(this, GystogramActivity.class);
        fullActivityIntent.putExtra(BUNDLE_GYSTOGRAM, mRowsGystogram);
        fullActivityIntent.putExtra(BUNDLE_GYSTOGRAM_TYPE, 1);
        startActivity(fullActivityIntent);
    }

    private void drawRowsGystogram() {
        if (mRowsGystogram == null) {
            initRowsGystogram();
        }

        mGystView.setGystogram(mRowsGystogram);
        mGystView.setVisibility(View.VISIBLE);
    }

    private void initRowsGystogram() {
        if (!TextUtils.isEmpty(mImagePath)) {
            GystogramBuilder gystogramBuilder = new GystogramBuilder();
            mRowsGystogram = gystogramBuilder.getRowsGystogram(mImagePath);
        } else {
            Toast.makeText(this, "Image path is empty! Create new image", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSpacesInRowsGystogram() {
        if (!TextUtils.isEmpty(mImagePath) && mRowsGystogram != null && !mRowsGystogram.isEmpty()) {
            GystogramBuilder gystogramBuilder = new GystogramBuilder();
            mSpacesInRowsGytogram = gystogramBuilder.getSpacesInRowsGystogram(mImagePath, mRowsGystogram);
        } else {
            Toast.makeText(this, "Create rows gystogram!", Toast.LENGTH_SHORT).show();
        }
    }

    private void start120Binarization() {
        Binarizer binarizer = new Binarizer();
        int threshold = binarizer.binarizeBy120Method(mImagePath);
        reloadImageView();
        Toast.makeText(this, "Threshold = " + String.valueOf(threshold), Toast.LENGTH_LONG).show();
    }

    private void startBinarization(int threshold) {
        Binarizer binarizer = new Binarizer();
        binarizer.binarizeByThreshold(mImagePath, threshold);
        reloadImageView();
    }

    private void reloadImageView() {
        imageViewScreenshot.setImageURI(null);
        if (!TextUtils.isEmpty(mImagePath)) {
            Uri imageUri = Uri.fromFile(new File(mImagePath));
            imageViewScreenshot.setImageURI(imageUri);
        }
    }
}
