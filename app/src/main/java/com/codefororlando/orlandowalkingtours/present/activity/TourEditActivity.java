package com.codefororlando.orlandowalkingtours.present.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codefororlando.orlandowalkingtours.BusProvider;
import com.codefororlando.orlandowalkingtours.event.OnEditTourDoneEvent;
import com.codefororlando.orlandowalkingtours.event.RxBus;
import com.codefororlando.orlandowalkingtours.present.fragment.TourEditFragment;
import com.codefororlando.orlandowalkingtours.present.base.BaseActivity;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Host for standalone tour editing
 */
public class TourEditActivity extends BaseActivity {
    public static Intent getEditIntent(Context context) {
        return getEditIntent(context, 0);
    }

    public static Intent getEditIntent(Context context, long tourId) {
        return new Intent(context, TourEditActivity.class)
                .putExtra(TourEditFragment.TOUR_ID_KEY, tourId);
    }

    private Subscription busSubscription;

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

        RxBus bus = BusProvider.get();
        busSubscription = bus.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof OnEditTourDoneEvent) {
                    /*
                     * Finish regardless if done or cancel.
                     * No need to pop fragment since finishing.
                     */
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        busSubscription.unsubscribe();
        super.onDestroy();
    }
}
