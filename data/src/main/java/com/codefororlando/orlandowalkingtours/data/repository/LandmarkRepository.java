package com.codefororlando.orlandowalkingtours.data.repository;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;

import java.util.List;

public interface LandmarkRepository {
    void load();

    List<HistoricLandmark> getLandmarks(String query);

    HistoricLandmark getLandmark(long id);
}
