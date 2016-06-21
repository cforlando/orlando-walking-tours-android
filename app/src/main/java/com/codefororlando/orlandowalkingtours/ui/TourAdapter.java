package com.codefororlando.orlandowalkingtours.ui;

import android.support.annotation.NonNull;
import android.view.View;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.event.Bus;
import com.codefororlando.orlandowalkingtours.present.base.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class TourAdapter extends BaseRecyclerViewAdapter<TourViewHolder>
        implements TourViewHolder.OnTourItemListener {
    public static class EditTourEvent {
        public final long tourId;

        public EditTourEvent(long tourId) {
            this.tourId = tourId;
        }
    }

    public static class DeleteTourEvent {
        public final long tourId;

        public DeleteTourEvent(long tourId) {
            this.tourId = tourId;
        }
    }

    private List<Tour> mTourData = new ArrayList<>(0);

    public TourAdapter(Bus bus) {
        super(bus, R.layout.tour_item);
    }

    public void setTours(@NonNull List<Tour> tours) {
        mTourData = tours;
        notifyDataSetChanged();
    }

    // TourViewHolder.OnTourItemListener

    private long getTourId(int position) {
        return mTourData.get(position).id;
    }

    @Override
    public void onItemPress(int position) {
        bus.publish(new EditTourEvent(getTourId(position)));
    }

    @Override
    public void deleteItem(int position) {
        // Save tour ID before deleting data
        long tourId = getTourId(position);
        mTourData.remove(position);
        notifyItemRemoved(position);
        bus.publish(new DeleteTourEvent(tourId));
    }

    // Adapter

    @Override
    protected TourViewHolder newViewHolder(View view) {
        return new TourViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(TourViewHolder holder, int position) {
        holder.bind(mTourData.get(position));
    }

    @Override
    public int getItemCount() {
        return mTourData.size();
    }
}
