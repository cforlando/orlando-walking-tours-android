package com.codefororlando.orlandowalkingtours.present.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codefororlando.orlandowalkingtours.event.OnEditTourDoneEvent;
import com.codefororlando.orlandowalkingtours.present.base.BaseActivity;
import com.codefororlando.orlandowalkingtours.present.fragment.TourEditFragment;

/**
 * Host for standalone tour editing
 */
public class TourEditActivity extends BaseActivity {
    public static Intent getIntent(Context context) {
        return getIntent(context, 0);
    }

    public static Intent getIntent(Context context, long tourId) {
        return new Intent(context, TourEditActivity.class)
                .putExtra(TourEditFragment.TOUR_ID_KEY, tourId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            long tourId = getIntent().getLongExtra(TourEditFragment.TOUR_ID_KEY, 0);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, TourEditFragment.newInstance(tourId))
                    .commit();
        }

        busSubscribe();
    }

    @Override
    protected void onEvent(Object event) {
        if (event instanceof OnEditTourDoneEvent) {
            /*
             * Finish regardless if done or cancel.
             * No need to pop fragment since finishing.
             */
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        busUnsubscribe();
        super.onDestroy();
    }
}
