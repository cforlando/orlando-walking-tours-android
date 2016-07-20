package com.codefororlando.orlandowalkingtours.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkSelect;

import butterknife.BindView;
import butterknife.OnClick;

public class LandmarkSelectViewHolder extends LandmarkViewHolder {
    public interface OnLandmarkItemListener extends OnAdapterPositionPressListener {
        void showInfo(int position);
    }

    @BindView(R.id.distance)
    TextView distance;

    private final OnLandmarkItemListener itemListener;

    public LandmarkSelectViewHolder(View itemView, final OnLandmarkItemListener listener) {
        super(itemView);

        itemListener = listener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemPress(getAdapterPosition());
            }
        });
    }

    public void bind(HistoricLandmarkSelect landmarkSelect) {
        HistoricLandmark landmark = landmarkSelect.landmark;
        bind(landmark);

        itemView.setSelected(landmarkSelect.isSelected);

        String distanceText = landmarkSelect.getDistanceText();
        boolean hasDistance = !TextUtils.isEmpty(distanceText);
        if (hasDistance) {
            distance.setText(distanceText);
        }
        distance.setVisibility(hasDistance ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.info)
    public void onInfoPress() {
        itemListener.showInfo(getAdapterPosition());
    }
}
