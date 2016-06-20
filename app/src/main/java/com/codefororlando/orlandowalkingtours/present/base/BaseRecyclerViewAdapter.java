package com.codefororlando.orlandowalkingtours.present.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefororlando.orlandowalkingtours.event.Bus;

abstract public class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    // Publish events rather than callback for flexible coupling
    protected final Bus bus;

    // All recycler views will inflate at least one layout
    private final int itemLayoutResId;

    public BaseRecyclerViewAdapter(Bus bus, int itemLayoutResId) {
        this.bus = bus;
        this.itemLayoutResId = itemLayoutResId;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(itemLayoutResId, parent, false);
        return newViewHolder(view);
    }

    abstract protected VH newViewHolder(View view);
}
