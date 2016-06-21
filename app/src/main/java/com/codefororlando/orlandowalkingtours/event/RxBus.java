package com.codefororlando.orlandowalkingtours.event;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus implements Bus<Subscription, Action1<Object>> {
    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    @Override
    public Subscription subscribe(Action1<Object> subscriber) {
        return bus.subscribe(subscriber);
    }

    @Override
    public void publish(Object event) {
        bus.onNext(event);
    }
}
