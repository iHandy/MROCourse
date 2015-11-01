package com.ucoz.handyby.mrocourse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.ucoz.handyby.mrocourse.Views.RowsGystogramView;
import com.ucoz.handyby.mrocourse.processors.GystMember;

import java.io.File;
import java.util.ArrayList;

public class GystogramActivity extends AppCompatActivity implements IConstants {

    RowsGystogramView mGystView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gystogram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        mGystView = (RowsGystogramView) findViewById(R.id.gyst_view);

        Intent parentIntent = getIntent();
        if (parentIntent != null) {
            Bundle parentBandle = parentIntent.getExtras();
            if (parentBandle != null) {
                ArrayList<GystMember> gystogram = (ArrayList) parentBandle.getSerializable(BUNDLE_GYSTOGRAM);

                if (gystogram != null) {
                    mGystView.setGystogram(gystogram);
                }
            }
        }

    }

}
