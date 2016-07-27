package com.codefororlando.orlandowalkingtours.present.base;

import com.codefororlando.orlandowalkingtours.R;

import butterknife.OnClick;

// Requires layout to contain actions with IDs 'done' and 'cancel'
abstract public class DoneCancelBarFragment extends ButterKnifeFragment {
    @OnClick(R.id.done)
    protected void onDone() {
    }

    @OnClick(R.id.cancel)
    protected void onCancel() {
    }
}
