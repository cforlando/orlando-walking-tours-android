package com.codefororlando.orlandowalkingtours.present.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.codefororlando.orlandowalkingtours.BusProvider;
import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.RepositoryProvider;
import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.data.repository.TourRepository;
import com.codefororlando.orlandowalkingtours.event.OnEditTourDoneEvent;
import com.codefororlando.orlandowalkingtours.event.OnPermissionGrantEvent;
import com.codefororlando.orlandowalkingtours.event.OnSelectLandmarkEvent;
import com.codefororlando.orlandowalkingtours.present.activity.LandmarkDetailActivity;
import com.codefororlando.orlandowalkingtours.present.activity.SelectLandmarkActivity;
import com.codefororlando.orlandowalkingtours.present.base.DoneCancelBarFragment;
import com.codefororlando.orlandowalkingtours.present.base.RetainFragment;
import com.codefororlando.orlandowalkingtours.rx.SaveTourAction;
import com.codefororlando.orlandowalkingtours.rx.SaveTourFunc;
import com.codefororlando.orlandowalkingtours.ui.TourStopAdapter;
import com.codefororlando.orlandowalkingtours.util.PermissionUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TourEditFragment extends DoneCancelBarFragment
        implements PermissionRequestFragment.OnPermissionRequestCompleteListener {
    public static final String TOUR_ID_KEY = "TOUR_ID_KEY";

    public static TourEditFragment newInstance(long tourId) {
        TourEditFragment fragment = new TourEditFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(TOUR_ID_KEY, tourId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.name)
    EditText nameEdit;
    @BindView(android.R.id.list)
    RecyclerView tourStopRecyclerView;

    private TourStopAdapter mTourStopAdapter;

    private DataFragment dataFragment;

    private Snackbar mLocationPermissionSnackbar;

    // Lifecycle/event

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataFragment = RetainFragment.getOrAdd(this, DataFragment.class);
        if (savedInstanceState == null) {
            dataFragment.setTourId(getTourId());
        }

        /*
         * Must listen for select landmark event outside of onStop.
         * Requires less code than wiring up startActivityForResult.
         */
        busSubscribe();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!PermissionUtil.get().hasLocationPermission()) {
            // Don't request permissions if user has previously denied
            dataFragment.suppressPermissionRequest =
                    PermissionUtil.get().hasDeniedLocationPermissionRequest(getActivity());

            showLocationPermissionRequestUi();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    @Override
    protected void onEvent(Object event) {
        if (event instanceof OnSelectLandmarkEvent) {
            OnSelectLandmarkEvent selectLandmarkEvent = (OnSelectLandmarkEvent) event;
            // Select request originated here
            if (getClass().equals(selectLandmarkEvent.caller)) {
                long landmarkId = selectLandmarkEvent.landmarkId;
                dataFragment.addStop(landmarkId);
                mTourStopAdapter.notifyItemInserted(mTourStopAdapter.getItemCount() - 1);
            }

        } else if (event instanceof TourStopAdapter.ShowTourStopInfoEvent) {
            showTourStopInfo(((TourStopAdapter.ShowTourStopInfoEvent) event).adapterPosition);

        } else if (event instanceof TourStopAdapter.DeleteTourStopEvent) {
            int position = ((TourStopAdapter.DeleteTourStopEvent) event).adapterPosition;
            dataFragment.deleteTourStop(position);
            mTourStopAdapter.notifyItemRemoved(position);

        } else if (event instanceof OnTourLoadEvent) {
            nameEdit.setText(dataFragment.getInitialTour().name);
            updateTourStopView();

        } else if (event instanceof OnPermissionGrantEvent) {
            String permission = ((OnPermissionGrantEvent) event).permission;
            if (PermissionUtil.get().isLocationPermission(permission)) {
                hideLocationPermissionRequestUi();
            }
        }
    }

    @Override
    public void onDestroy() {
        busUnsubscribe();
        super.onDestroy();
    }

    // UI/action

    @Override
    protected int getLayoutResId() {
        return R.layout.tour_edit_fragment;
    }

    @OnClick(R.id.add_stop)
    public void onAddStop() {
        addStop();
    }

    private void addStop() {
        startActivity(SelectLandmarkActivity.getIntent(getActivity(), getClass()));
    }

    private void updateUi() {
        updateTourStopView();
    }

    private void updateTourStopView() {
        if (mTourStopAdapter == null) {
            mTourStopAdapter = new TourStopAdapter(bus, RepositoryProvider.getLandmark());
            tourStopRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            tourStopRecyclerView.setAdapter(mTourStopAdapter);
        }
        mTourStopAdapter.setTourStopIds(dataFragment.getStopIds());
    }

    private void showLocationPermissionRequestUi() {
        // Request has already been made, don't make again
        if (dataFragment.suppressPermissionRequest) {
            return;
        }

        if (mLocationPermissionSnackbar == null) {
            int stringResId = R.string.request_location;
            int duration = Snackbar.LENGTH_INDEFINITE;
            mLocationPermissionSnackbar = Snackbar.make(getView(), stringResId, duration);
            mLocationPermissionSnackbar.setAction(
                    android.R.string.yes,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestLocationPermission();
                        }
                    }
            );
        }
        mLocationPermissionSnackbar.show();
    }

    private void hideLocationPermissionRequestUi() {
        if (mLocationPermissionSnackbar == null) {
            return;
        }

        dataFragment.suppressPermissionRequest = true;

        mLocationPermissionSnackbar.dismiss();
        mLocationPermissionSnackbar = null;
    }

    // Permission

    private void requestLocationPermission() {
        boolean isRequestPermission =
                PermissionUtil.get().showLocationPermissionFragment(getFragmentManager(), this);
        if (isRequestPermission) {
            hideLocationPermissionRequestUi();
        } else {
            showLocationPermissionRequestUi();
        }
    }

    // PermissionRequestFragment.OnPermissionRequestCompleteListener

    @Override
    public void onPermissionRequestComplete() {
        PermissionUtil.get().removeRequestLocationPermissionFragment(getFragmentManager());
    }

    // Methods

    private void showTourStopInfo(int dataIndex) {
        long landmarkId = dataFragment.getStopIds().get(dataIndex);
        startActivity(LandmarkDetailActivity.getIntent(getActivity(), landmarkId));
    }

    // Done/cancel

    private void publishEvent(boolean isCancel) {
        BusProvider.get().publish(new OnEditTourDoneEvent(isCancel));
    }

    @Override
    protected void onDone() {
        String name = nameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name) && dataFragment.getStopIds().size() > 0) {
            nameEdit.requestFocus();
            nameEdit.setError(getString(R.string.define_name_to_save));
        } else {
            dataFragment.saveTour(getTourId(), name);
            publishEvent(true);
        }
    }

    @Override
    protected void onCancel() {
        publishEvent(true);
    }

    // Data

    private long getTourId() {
        return getArguments().getLong(TOUR_ID_KEY);
    }

    private static class OnTourLoadEvent {
    }

    public static class DataFragment extends RetainFragment {
        public boolean suppressPermissionRequest;

        private long tourId;

        private TourRepository tourRepository;

        private final AtomicReference<Tour> initialTour = new AtomicReference<>();

        private List<Long> mStopsLandmarkId = new LinkedList<>();

        public void setTourId(long id) {
            tourId = id;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            busSubscribe();

            tourRepository = RepositoryProvider.getTour();
            if (tourId > 0) {
                loadTour(tourId);
            }
        }

        private void publishLoad() {
            bus.publish(new OnTourLoadEvent());
        }

        @Override
        public void onDestroy() {
            busUnsubscribe();
            super.onDestroy();
        }

        public Tour getInitialTour() {
            return initialTour.get();
        }

        private void loadTour(long tourId) {
            Observable.just(tourId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Long, Tour>() {
                        @Override
                        public Tour call(Long id) {
                            return tourRepository.get(id);
                        }
                    })
                    .subscribe(new Action1<Tour>() {
                        @Override
                        public void call(Tour tour) {
                            initialTour.set(tour);
                            List<Long> stopsLandmarkId = new LinkedList<>();
                            for (Long landmarkId : tour.getTourStopIds()) {
                                stopsLandmarkId.add(landmarkId);
                            }
                            mStopsLandmarkId = stopsLandmarkId;
                            publishLoad();
                        }
                    });
        }

        public boolean saveTour(long tourId, String name) {
            boolean isDefined = !TextUtils.isEmpty(name) || mStopsLandmarkId.size() > 0;
            Tour initialTour = getInitialTour();
            if (initialTour == null && !isDefined) {
                return false;
            }

            Observable.just(new Tour(tourId, name, mStopsLandmarkId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new SaveTourFunc(tourRepository))
                    .subscribe(new SaveTourAction(bus));
            return true;
        }

        public void addStop(long landmarkId) {
            mStopsLandmarkId.add(landmarkId);
        }

        public void deleteTourStop(int index) {
            mStopsLandmarkId.remove(index);
        }

        public List<Long> getStopIds() {
            return Collections.unmodifiableList(mStopsLandmarkId);
        }
    }
}
