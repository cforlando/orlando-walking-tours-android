package com.codefororlando.orlandowalkingtours.event;

import java.io.Serializable;

public class OnSelectLandmarkEvent {
    public final Serializable caller;
    public final long landmarkId;

    public OnSelectLandmarkEvent(Serializable caller, long landmarkId) {
        this.caller = caller;
        this.landmarkId = landmarkId;
    }
}
