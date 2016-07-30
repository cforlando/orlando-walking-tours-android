package com.codefororlando.orlandowalkingtours.present.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codefororlando.orlandowalkingtours.BusProvider;
import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.RepositoryProvider;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistance;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistanceSelect;
import com.codefororlando.orlandowalkingtours.event.OnCancelSelectLandmarkEvent;
import com.codefororlando.orlandowalkingtours.event.OnLocationChangeEvent;
import com.codefororlando.orlandowalkingtours.event.OnQueryLandmarksEvent;
import com.codefororlando.orlandowalkingtours.event.OnSelectLandmarkEvent;
import com.codefororlando.orlandowalkingtours.present.activity.LandmarkDetailActivity;
import com.codefororlando.orlandowalkingtours.present.base.DoneCancelBarLocationFragment;
import com.codefororlando.orlandowalkingtours.present.base.RetainFragment;
import com.codefororlando.orlandowalkingtours.rx.LoadLandmarksAction;
import com.codefororlando.orlandowalkingtours.ui.LandmarkSelectAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SelectLandmarkFragment extends DoneCancelBarLocationFragment {
    public static final String CALLER_KEY = "CALLER_KEY";

    private static final String LAYOUT_MANAGER_STATE_KEY = "LAYOUT_MANAGER_STATE_KEY";

    public static SelectLandmarkFragment newInstance(Serializable caller) {
        SelectLandmarkFragment fragment = new SelectLandmarkFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CALLER_KEY, caller);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(android.R.id.list)
    RecyclerView landmarkRecyclerView;

    private DataFragment dataFragment;

    private LandmarkSelectAdapter mLandmarkAdapter;

    // Keeps recycler view scroll position on config change
    private Parcelable layoutManagerState;

    // Lifecycle/event

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataFragment = RetainFragment.getOrAdd(this, DataFragment.class);

        if (savedInstanceState != null) {
            layoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE_KEY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        busSubscribe();
        bindLocationService();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    @Override
    protected void onEvent(Object event) {
        if (event instanceof LandmarkSelectAdapter.SelectLandmarkEvent) {
            updateSelection((LandmarkSelectAdapter.SelectLandmarkEvent) event);

        } else if (event instanceof LandmarkSelectAdapter.ShowLandmarkInfoEvent) {
            showLandmarkInfo(((LandmarkSelectAdapter.ShowLandmarkInfoEvent) event).landmarkId);

        } else if (event instanceof OnLandmarkLoadEvent) {
            updateLandmarkView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable parcelable = landmarkRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LAYOUT_MANAGER_STATE_KEY, parcelable);
    }

    @Override
    public void onStop() {
        unbindLocationService();
        busUnsubscribe();
        super.onStop();
    }

    // UI/action

    @Override
    protected int getLayoutResId() {
        return R.layout.select_landmark_fragment;
    }

    private void updateUi() {
        updateLandmarkView();
    }

    private void updateLandmarkView() {
        if (mLandmarkAdapter == null) {
            mLandmarkAdapter = new LandmarkSelectAdapter(bus);
            landmarkRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            if (layoutManagerState != null) {
                landmarkRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerState);
            }
            landmarkRecyclerView.setAdapter(mLandmarkAdapter);
        }
        mLandmarkAdapter.setLandmarks(dataFragment.getLandmarkData());
    }

    private void updateSelection(LandmarkSelectAdapter.SelectLandmarkEvent event) {
        // Deselect previous
        int selectedAdapterPosition = dataFragment.getSelectedAdapterPosition();
        if (selectedAdapterPosition >= 0) {
            mLandmarkAdapter.selectItem(selectedAdapterPosition, false);
        }

        // Select
        int position = event.adapterPosition;
        if (event.select) {
            dataFragment.setSelection(position, event.landmarkId);
            mLandmarkAdapter.selectItem(position, true);
        } else {
            dataFragment.setSelection(-1, 0);
        }
    }

    // Methods

    private void showLandmarkInfo(long landmarkId) {
        startActivity(LandmarkDetailActivity.getIntent(getActivity(), landmarkId));
    }

    // Done/cancel

    @Override
    protected void onDone() {
        long landmarkId = dataFragment.getSelectedLandmarkId();
        if (landmarkId > 0) {
            BusProvider.get().publish(new OnSelectLandmarkEvent(getCaller(), landmarkId));
        } else {
            onCancel();
        }
    }

    @Override
    protected void onCancel() {
        BusProvider.get().publish(new OnCancelSelectLandmarkEvent());
    }

    // Data

    private Serializable getCaller() {
        return getArguments().getSerializable(CALLER_KEY);
    }

    private static class OnLandmarkLoadEvent {
    }

    public static class DataFragment extends RetainFragment {
        private List<HistoricLandmarkDistanceSelect> mLandmarks = new ArrayList<>(0);

        private int mSelectedAdapterPosition = -1;
        private long mSelectedLandmarkId;

        private static final String IS_ALPHA_SORT_KEY = "IS_ALPHA_SORT_KEY";
        private SharedPreferences activityPreferences;

        private final AtomicReference<Location> lastLocationAr = new AtomicReference<>();

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            activityPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            busSubscribe();

            loadLandmarks();
        }

        @Override
        protected void onEvent(Object event) {
            if (event instanceof OnQueryLandmarksEvent) {
                loadLandmarks();

            } else if (event instanceof OnLocationChangeEvent) {
                Location location = ((OnLocationChangeEvent) event).location;
                lastLocationAr.set(location);

                if (!isAlphaSort()) {
                    // Easier than creating more methods to sort by distance and republishing
                    loadLandmarks();
                }
            }
        }

        @Override
        public void onDestroy() {
            busUnsubscribe();
            super.onDestroy();
        }

        public List<HistoricLandmarkDistanceSelect> getLandmarkData() {
            return Collections.unmodifiableList(mLandmarks);
        }

        public void setSelection(int adapterPosition, long landmarkId) {
            mSelectedAdapterPosition = adapterPosition;
            mSelectedLandmarkId = landmarkId;
        }

        public int getSelectedAdapterPosition() {
            return mSelectedAdapterPosition;
        }

        public long getSelectedLandmarkId() {
            return mSelectedLandmarkId;
        }

        private boolean isAlphaSort() {
            return activityPreferences.getBoolean(IS_ALPHA_SORT_KEY, false);
        }

        private void loadLandmarks() {
            Location location = lastLocationAr.get();
            boolean isAlphaSort = isAlphaSort() || location == null;
            Comparator<HistoricLandmarkDistance> comparator = isAlphaSort
                    ? HistoricLandmarkDistance.NAME_COMPARATOR
                    : new HistoricLandmarkDistance.SquareDistanceComparator(location);
            LoadLandmarksAction action =
                    new LoadLandmarksAction(RepositoryProvider.getLandmark(), location, comparator);

            Observable.create(action)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<HistoricLandmarkDistanceSelect>>() {
                        @Override
                        public void call(List<HistoricLandmarkDistanceSelect> landmarks) {
                            mLandmarks = landmarks;
                            bus.publish(new OnLandmarkLoadEvent());
                        }
                    });
        }
    }
}
