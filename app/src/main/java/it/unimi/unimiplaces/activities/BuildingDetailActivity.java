package it.unimi.unimiplaces.activities;

import android.os.Bundle;

import it.unimi.unimiplaces.R;

public class BuildingDetailActivity extends AppDetailSectionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);
        this.setUpDetailActivity("Title");
    }
}