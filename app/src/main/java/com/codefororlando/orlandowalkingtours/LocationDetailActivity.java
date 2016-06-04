package com.codefororlando.orlandowalkingtours;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.codefororlando.orlandowalkingtours.models.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.walkingtours.R;

public class LocationDetailActivity extends AppCompatActivity {

    HistoricLandmark landmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);


        landmark = (HistoricLandmark) getIntent().getSerializableExtra("HLOCATION");


        TextView name = (TextView) findViewById(R.id.txt_locdetailname);
        name.setText(landmark.getName());

        TextView description = (TextView) findViewById(R.id.txt_locdetaildescription);
        description.setText(landmark.getDescription());



    }
}
