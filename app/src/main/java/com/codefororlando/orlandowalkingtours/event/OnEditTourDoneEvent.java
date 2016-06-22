package com.codefororlando.orlandowalkingtours.event;

// Tour editing is done/canceled
public class OnEditTourDoneEvent {
    public final boolean isCancel;

    public OnEditTourDoneEvent(boolean isCancel) {
        this.isCancel = isCancel;
    }
}
