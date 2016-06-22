package com.codefororlando.orlandowalkingtours.present.fragment;

import android.widget.TextView;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.present.base.ButterKnifeFragment;

import butterknife.BindView;

public class LandmarkDetailFragment extends ButterKnifeFragment {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.street_address)
    TextView streetAddress;
    @BindView(R.id.description)
    TextView description;

    private HistoricLandmark mLandmark;

    public void setLandmark(HistoricLandmark landmark) {
        mLandmark = landmark;
        updateUi();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.landmark_detail_fragment;
    }

    private void updateUi() {
        if (name == null ||
                mLandmark == null) {
            return;
        }

        HistoricLandmark landmark = mLandmark;
        name.setText(landmark.name);
        streetAddress.setText(landmark.streetAddress);
        description.setText(landmark.description);
    }
}
