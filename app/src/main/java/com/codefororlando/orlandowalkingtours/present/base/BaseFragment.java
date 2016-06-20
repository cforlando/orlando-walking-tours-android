package com.codefororlando.orlandowalkingtours.present.base;

import android.support.v4.app.Fragment;

import com.codefororlando.orlandowalkingtours.BuildConfig;
import com.codefororlando.orlandowalkingtours.BusProvider;
import com.codefororlando.orlandowalkingtours.event.RxBus;
import com.codefororlando.orlandowalkingtours.log.ClassTagLogger;
import com.codefororlando.orlandowalkingtours.log.Logger;

import rx.Subscription;
import rx.functions.Action1;

public class BaseFragment extends Fragment {
    protected RxBus bus;
    private Subscription busSubscription;

    protected void busSubscribe() {
        bus = BusProvider.get();
        busSubscription = bus.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                onEvent(o);
            }
        });
    }

    protected void onEvent(Object event) {
    }

    protected void busUnsubscribe() {
        busSubscription.unsubscribe();
    }

    // Logging

    private final Logger logger = newLogger();

    // Allows for overriding, injection not likely possible
    protected Logger newLogger() {
        return new ClassTagLogger(this, BuildConfig.DEBUG);
    }

    protected void logD(String s) {
        logger.debug(s);
    }

    protected void logD(String format, Object... objects) {
        logger.debug(format, objects);
    }

    protected void logI(String s) {
        logger.info(s);
    }

    protected void logE(String s, Throwable throwable) {
        logger.error(s, throwable);
    }
}
