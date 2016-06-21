package com.codefororlando.orlandowalkingtours.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandmarkViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.street_address)
    TextView streetAddress;

    public LandmarkViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    protected void bind(HistoricLandmark landmark) {
        name.setText(landmark.name);
        streetAddress.setText(landmark.streetAddress);
    }
}
