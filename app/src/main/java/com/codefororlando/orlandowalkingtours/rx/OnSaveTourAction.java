package com.codefororlando.orlandowalkingtours.rx;

import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.event.OnTourSaveEvent;
import com.codefororlando.orlandowalkingtours.event.RxBus;

public class OnSaveTourAction extends BusAction1<Tour> {
    public OnSaveTourAction(RxBus rxBus) {
        super(rxBus);
    }

    @Override
    public void call(Tour tour) {
        if (tour != null) {
            bus.publish(new OnTourSaveEvent(tour));
        }
    }
}
