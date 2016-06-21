package com.codefororlando.orlandowalkingtours.data.repository;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;

import java.util.List;

public interface LandmarkRepository {
    List<HistoricLandmark> getLandmarks();

    void queryLandmarks();

    HistoricLandmark getLandmark(long id);
}
