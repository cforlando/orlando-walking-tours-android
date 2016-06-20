package com.codefororlando.orlandowalkingtours.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.data.model.Tour;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TourViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.stop_count)
    TextView count;

    public TourViewHolder(View itemView, final OnAdapterPositionSelectListener listener) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemSelect(getAdapterPosition());
            }
        });
    }

    public void bind(Tour tour) {
        name.setText(tour.name);

        int stopCount = tour.getTourStops().size();
        if (stopCount > 0) {
            count.setText(String.valueOf(count));
        }
        count.setVisibility(stopCount == 0 ? View.GONE : View.VISIBLE);
    }
}
