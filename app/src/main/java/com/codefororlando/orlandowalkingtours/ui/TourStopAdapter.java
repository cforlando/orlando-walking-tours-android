package com.codefororlando.orlandowalkingtours.ui;

import android.support.annotation.NonNull;
import android.view.View;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepository;
import com.codefororlando.orlandowalkingtours.event.Bus;
import com.codefororlando.orlandowalkingtours.present.base.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class TourStopAdapter extends BaseRecyclerViewAdapter<TourStopViewHolder>
        implements TourStopViewHolder.OnTourStopItemListener {
    public static class ShowTourStopInfoEvent {
        public final int adapterPosition;

        public ShowTourStopInfoEvent(int adapterPosition) {
            this.adapterPosition = adapterPosition;
        }
    }

    public static class DeleteTourStopEvent {
        public final int adapterPosition;

        public DeleteTourStopEvent(int adapterPosition) {
            this.adapterPosition = adapterPosition;
        }
    }

    private final LandmarkRepository landmarkRepository;

    private List<Long> mTourStopData = new ArrayList<>(0);

    public TourStopAdapter(Bus bus, LandmarkRepository landmarkRepository) {
        super(bus, R.layout.tour_stop_item);
        this.landmarkRepository = landmarkRepository;
    }

    public void setTourStopIds(@NonNull List<Long> tourStopIds) {
        mTourStopData = tourStopIds;
        notifyDataSetChanged();
    }

    // OnTourStopItemListener

    @Override
    public void onItemPress(int position) {
        bus.publish(new ShowTourStopInfoEvent(position));
    }

    @Override
    public void deleteItem(int position) {
        bus.publish(new DeleteTourStopEvent(position));
    }

    // Adapter

    @Override
    protected TourStopViewHolder newViewHolder(View view) {
        return new TourStopViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(TourStopViewHolder holder, int position) {
        long landmarkId = mTourStopData.get(position);
        HistoricLandmark landmark = landmarkRepository.getLandmark(landmarkId);
        holder.bind(landmark);
    }

    @Override
    public int getItemCount() {
        return mTourStopData.size();
    }
}
