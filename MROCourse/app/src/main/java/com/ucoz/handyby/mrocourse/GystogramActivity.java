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
import com.ucoz.handyby.mrocourse.Views.SpacesInRowsGystogramView;
import com.ucoz.handyby.mrocourse.processors.GystMember;

import java.io.File;
import java.util.ArrayList;

public class GystogramActivity extends AppCompatActivity implements IConstants {

    RowsGystogramView mGystView;
    SpacesInRowsGystogramView mGystView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_gystogram);



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        Intent parentIntent = getIntent();
        if (parentIntent != null) {
            Bundle parentBandle = parentIntent.getExtras();
            if (parentBandle != null) {
                ArrayList<GystMember> gystogram = (ArrayList) parentBandle.getSerializable(BUNDLE_GYSTOGRAM);

                int type = parentBandle.getInt(BUNDLE_GYSTOGRAM_TYPE);

                if (type == 1) {
                    setContentView(R.layout.activity_gystogram);
                } else {
                    setContentView(R.layout.activity_gystogram2);
                }

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                try {
                    mGystView = (RowsGystogramView) findViewById(R.id.gyst_view);

                    if (gystogram != null) {
                        mGystView.setGystogram(gystogram);
                    }
                } catch (ClassCastException e) {
                    mGystView2 = (SpacesInRowsGystogramView) findViewById(R.id.gyst_view);

                    if (gystogram != null) {
                        mGystView2.setGystogram(gystogram);
                    }
                }
            }
        }

    }

}
