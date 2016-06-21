package com.codefororlando.orlandowalkingtours.present.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codefororlando.orlandowalkingtours.BusProvider;
import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.RepositoryProvider;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkSelect;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepository;
import com.codefororlando.orlandowalkingtours.event.OnCancelSelectLandmarkEvent;
import com.codefororlando.orlandowalkingtours.event.OnQueryLandmarksEvent;
import com.codefororlando.orlandowalkingtours.event.OnSelectLandmarkEvent;
import com.codefororlando.orlandowalkingtours.present.activity.LandmarkDetailActivity;
import com.codefororlando.orlandowalkingtours.present.base.DoneCancelBarFragment;
import com.codefororlando.orlandowalkingtours.present.base.RetainFragment;
import com.codefororlando.orlandowalkingtours.ui.LandmarkSelectAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SelectLandmarkFragment extends DoneCancelBarFragment {
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
        private LandmarkRepository landmarkRepository;

        private List<HistoricLandmarkSelect> mLandmarks = new ArrayList<>(0);

        private int mSelectedAdapterPosition = -1;
        private long mSelectedLandmarkId;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            busSubscribe();

            landmarkRepository = RepositoryProvider.getLandmark();

            loadLandmarks();
        }

        @Override
        protected void onEvent(Object event) {
            if (event instanceof OnQueryLandmarksEvent) {
                loadLandmarks();
            }
        }

        @Override
        public void onDestroy() {
            busUnsubscribe();
            super.onDestroy();
        }


        public List<HistoricLandmarkSelect> getLandmarkData() {
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

        private void loadLandmarks() {
            Observable.just(0)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Integer, List<HistoricLandmarkSelect>>() {
                        @Override
                        public List<HistoricLandmarkSelect> call(Integer i) {
                            List<HistoricLandmark> landmarks = landmarkRepository.getLandmarks();
                            List<HistoricLandmarkSelect> landmarkSelects =
                                    new ArrayList<>(landmarks.size());
                            for (HistoricLandmark landmark : landmarks) {
                                landmarkSelects.add(new HistoricLandmarkSelect(landmark));
                            }
                            // TODO Sort by distance if location is known
                            Collections.sort(landmarkSelects, HistoricLandmarkSelect.NAME_COMPARATOR);
                            return landmarkSelects;
                        }
                    })
                    .subscribe(new Action1<List<HistoricLandmarkSelect>>() {
                        @Override
                        public void call(List<HistoricLandmarkSelect> landmarks) {
                            mLandmarks = landmarks;
                            bus.publish(new OnLandmarkLoadEvent());
                        }
                    });
        }
    }
}
