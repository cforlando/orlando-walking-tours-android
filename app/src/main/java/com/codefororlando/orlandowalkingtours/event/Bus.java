package com.codefororlando.orlandowalkingtours.event;

public interface Bus<T, U> {
    T subscribe(U subscriber);

    void publish(Object event);
}
