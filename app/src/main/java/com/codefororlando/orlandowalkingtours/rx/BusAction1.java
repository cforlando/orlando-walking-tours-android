package com.codefororlando.orlandowalkingtours.rx;

import com.codefororlando.orlandowalkingtours.event.RxBus;

import rx.functions.Action1;

abstract public class BusAction1<T> implements Action1<T> {
    protected final RxBus bus;

    public BusAction1(RxBus rxBus) {
        bus = rxBus;
    }
}
