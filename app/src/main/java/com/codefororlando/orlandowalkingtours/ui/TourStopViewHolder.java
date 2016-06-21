package com.codefororlando.orlandowalkingtours.ui;

import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.codefororlando.orlandowalkingtours.R;

import butterknife.OnClick;

public class TourStopViewHolder extends LandmarkViewHolder
        implements PopupMenu.OnMenuItemClickListener {
    public interface OnTourStopItemListener extends OnAdapterPositionPressListener {
        void deleteItem(int position);
    }

    private final OnTourStopItemListener itemListener;

    public TourStopViewHolder(View itemView, final OnTourStopItemListener listener) {
        super(itemView);

        itemListener = listener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemPress(getAdapterPosition());
            }
        });
    }

    @OnClick(R.id.more)
    public void onMorePress(View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.tour_stop_action, popup.getMenu());
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
