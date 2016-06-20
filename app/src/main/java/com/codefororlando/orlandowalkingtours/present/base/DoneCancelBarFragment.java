package com.codefororlando.orlandowalkingtours.present.base;

import com.codefororlando.orlandowalkingtours.R;

import butterknife.OnClick;

abstract public class DoneCancelBarFragment extends ButterKnifeFragment {
    @OnClick(R.id.done)
    protected void onDone() {
    }

    @OnClick(R.id.cancel)
    protected void onCancel() {
    }
}
