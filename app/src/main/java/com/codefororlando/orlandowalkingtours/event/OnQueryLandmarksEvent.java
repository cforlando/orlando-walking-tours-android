package com.codefororlando.orlandowalkingtours.event;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;

import java.util.List;

public class OnQueryLandmarksEvent {
    public final List<HistoricLandmark> landmarks;

    public OnQueryLandmarksEvent(List<HistoricLandmark> landmarks) {
        this.landmarks = landmarks;
    }
}
