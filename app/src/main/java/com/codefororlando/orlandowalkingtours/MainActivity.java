package com.codefororlando.orlandowalkingtours;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.codefororlando.orlandowalkingtours.managers.HistoricLandmarkManager;
import com.codefororlando.orlandowalkingtours.models.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.utilities.DevelopmentUtilities;
import com.codefororlando.orlandowalkingtours.walkingtours.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private HistoricLandmarkManager historicLandmarkManager;
    LocalBroadcastManager broadcastManager;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        ctx = this;
        Button showMap = (Button) findViewById(R.id.btn_showmap);
        if(showMap!=null){
            showMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, MapsActivity.class);
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                }
            });
        }


        historicLandmarkManager = new HistoricLandmarkManager(this);
        historicLandmarkManager.pullHistoricLandmarks();
    }

    private void initialize(){
        //Initializes the broadcast manager. Use this you have an async method running in the background and want to alert the activity when it is done
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(HistoricLandmarkManager.PULL_SUCCESS);
        broadcastManager.registerReceiver(mBroadcastReceiver, filter);
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case HistoricLandmarkManager.PULL_SUCCESS:
                {
                    ArrayList<HistoricLandmark> results = historicLandmarkManager.getHistoricLandmarks();
                    DevelopmentUtilities.logV("Got results, size -> " + results.size());
                }break;
            }
        }
    };
}
