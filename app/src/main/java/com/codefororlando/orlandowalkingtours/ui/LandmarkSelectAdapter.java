package com.codefororlando.orlandowalkingtours.ui;

import android.support.annotation.NonNull;
import android.view.View;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistanceSelect;
import com.codefororlando.orlandowalkingtours.event.Bus;
import com.codefororlando.orlandowalkingtours.present.base.BaseRecyclerViewAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LandmarkSelectAdapter extends BaseRecyclerViewAdapter<LandmarkSelectViewHolder>
        implements LandmarkSelectViewHolder.OnLandmarkItemListener {
    public static class ShowLandmarkInfoEvent {
        public final long landmarkId;

        public ShowLandmarkInfoEvent(long landmarkId) {
            this.landmarkId = landmarkId;
        }
    }

    public static class SelectLandmarkEvent {
        public final boolean select;
        public final int adapterPosition;
        public final long landmarkId;

        public SelectLandmarkEvent(boolean select, int adapterPosition, long landmarkId) {
            this.select = select;
            this.adapterPosition = adapterPosition;
            this.landmarkId = landmarkId;
        }
    }

    private List<HistoricLandmarkDistanceSelect> mLandmarkData = Collections.emptyList();
    private Collection<Long> mTourStopIds = Collections.emptySet();

    public LandmarkSelectAdapter(Bus bus) {
        super(bus, R.layout.landmark_item);
    }

    public void setTourStopIds(Collection<Long> tourStopIds) {
        mTourStopIds = tourStopIds;
        notifyDataSetChanged();
    }

    public void setLandmarks(@NonNull List<HistoricLandmarkDistanceSelect> landmarks) {
        mLandmarkData = landmarks;
        notifyDataSetChanged();
    }

    // OnLandmarkItemListener

    @Override
    public void showInfo(int position) {
        bus.publish(new ShowLandmarkInfoEvent(mLandmarkData.get(position).landmark.id));
    }

    public void selectItem(int position, boolean select) {
        mLandmarkData.get(position).isSelected = select;
        notifyItemChanged(position);
    }

    @Override
    public void onItemPress(int position) {
        HistoricLandmarkDistanceSelect landmarkSelect = mLandmarkData.get(position);
        boolean select = !landmarkSelect.isSelected;
        bus.publish(new SelectLandmarkEvent(select, position, landmarkSelect.landmark.id));
    }

    // Adapter

    @Override
    protected LandmarkSelectViewHolder newViewHolder(View view) {
        return new LandmarkSelectViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(LandmarkSelectViewHolder holder, int position) {
        HistoricLandmarkDistanceSelect landmarkDistanceSelect = mLandmarkData.get(position);
        boolean isStop = mTourStopIds.contains(landmarkDistanceSelect.landmark.id);
        holder.bind(landmarkDistanceSelect, isStop);
    }

    @Override
    public int getItemCount() {
        return mLandmarkData.size();
    }
}
