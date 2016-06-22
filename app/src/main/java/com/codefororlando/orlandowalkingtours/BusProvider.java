package com.codefororlando.orlandowalkingtours;

import com.codefororlando.orlandowalkingtours.event.RxBus;

// Do not use IoC framework, keep simple for future contributors
public class BusProvider {
    private static RxBus sBus;

    public static void initialize() {
        sBus = new RxBus();
    }

    public static RxBus get() {
        return sBus;
    }
}
