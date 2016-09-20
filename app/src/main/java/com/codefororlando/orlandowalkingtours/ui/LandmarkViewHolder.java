package com.codefororlando.orlandowalkingtours.ui;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistance;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandmarkViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.street_address)
    TextView streetAddress;
    @BindView(R.id.distance)
    TextView distance;

    public LandmarkViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    protected void bind(HistoricLandmarkDistance landmarkDistance) {
        HistoricLandmark landmark = landmarkDistance.landmark;
        name.setText(landmark.name);
        streetAddress.setText(landmark.streetAddress);

        setDistance(landmarkDistance);
    }

    private void setDistance(HistoricLandmarkDistance landmarkDistance) {
        String distanceText = landmarkDistance.getDistanceText();
        boolean hasDistance = !TextUtils.isEmpty(distanceText);
        if (hasDistance) {
            distance.setText(distanceText);
        }
        distance.setVisibility(hasDistance ? View.VISIBLE : View.GONE);
    }
}
