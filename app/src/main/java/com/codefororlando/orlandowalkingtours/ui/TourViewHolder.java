package com.codefororlando.orlandowalkingtours.ui;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.data.model.Tour;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TourViewHolder extends RecyclerView.ViewHolder
        implements PopupMenu.OnMenuItemClickListener {
    public interface OnTourItemListener extends OnAdapterPositionPressListener {
        void deleteItem(int position);
    }

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.stop_count)
    TextView count;

    private final OnTourItemListener itemListener;

    public TourViewHolder(View itemView, final OnTourItemListener listener) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        itemListener = listener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemPress(getAdapterPosition());
            }
        });
    }

    public void bind(Tour tour) {
        name.setText(tour.name);

        int stopCount = tour.getTourStopIds().size();
        if (stopCount > 0) {
            int resId = R.plurals.stop_count_format_d;
            String text = count.getResources().getQuantityString(resId, stopCount, stopCount);
            count.setText(text);
        }
        count.setVisibility(stopCount == 0 ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.more)
    public void onMorePress(View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.tour_action, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    // PopupMenu.OnMenuItemClickListener

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                itemListener.deleteItem(getAdapterPosition());
                return true;
        }
        return false;
    }
}
