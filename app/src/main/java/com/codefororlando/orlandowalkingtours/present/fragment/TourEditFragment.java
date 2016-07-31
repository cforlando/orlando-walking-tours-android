package com.codefororlando.orlandowalkingtours.present.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.codefororlando.orlandowalkingtours.BusProvider;
import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.RepositoryProvider;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistance;
import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepository;
import com.codefororlando.orlandowalkingtours.data.repository.TourRepository;
import com.codefororlando.orlandowalkingtours.event.OnEditTourDoneEvent;
import com.codefororlando.orlandowalkingtours.event.OnLocationChangeEvent;
import com.codefororlando.orlandowalkingtours.event.OnPermissionGrantEvent;
import com.codefororlando.orlandowalkingtours.event.OnSelectLandmarkEvent;
import com.codefororlando.orlandowalkingtours.present.activity.LandmarkDetailActivity;
import com.codefororlando.orlandowalkingtours.present.activity.SelectLandmarkActivity;
import com.codefororlando.orlandowalkingtours.present.base.DoneCancelBarLocationFragment;
import com.codefororlando.orlandowalkingtours.present.base.RetainFragment;
import com.codefororlando.orlandowalkingtours.rx.OnSaveTourAction;
import com.codefororlando.orlandowalkingtours.rx.SaveTourAction;
import com.codefororlando.orlandowalkingtours.ui.TourStopAdapter;
import com.codefororlando.orlandowalkingtours.util.PermissionUtil;
import com.codefororlando.orlandowalkingtours.util.ScreenKeyboardUtil;

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

public class TourEditFragment extends DoneCancelBarLocationFragment
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

    private TourStopAdapter tourStopAdapter;

    private DataFragment dataFragment;

    private Snackbar mLocationPermissionSnackbar;

    private int mItemViewBackgroundResId;

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

        if (!PermissionUtil.get().hasLocationPermission()) {
            // Don't request permissions if user has previously denied
            dataFragment.suppressPermissionRequest =
                    PermissionUtil.get().hasDeniedLocationPermissionRequest(getActivity());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        tourStopAdapter = new TourStopAdapter(bus);
        tourStopRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tourStopRecyclerView.setAdapter(tourStopAdapter);
        getDragDropHelper().attachToRecyclerView(tourStopRecyclerView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        bindLocationService();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    @Override
    protected void onEvent(Object event) {
        if (event instanceof OnLocationChangeEvent) {
            tourStopAdapter.setLocation(((OnLocationChangeEvent) event).location);

        } else if (event instanceof OnSelectLandmarkEvent) {
            OnSelectLandmarkEvent selectLandmarkEvent = (OnSelectLandmarkEvent) event;
            // Select request originated here
            if (getClass().equals(selectLandmarkEvent.caller)) {
                long landmarkId = selectLandmarkEvent.landmarkId;
                dataFragment.addStop(landmarkId);
                tourStopAdapter.notifyItemInserted(tourStopAdapter.getItemCount() - 1);
            }

        } else if (event instanceof TourStopAdapter.ShowTourStopInfoEvent) {
            showTourStopInfo(((TourStopAdapter.ShowTourStopInfoEvent) event).adapterPosition);

        } else if (event instanceof TourStopAdapter.DeleteTourStopEvent) {
            int position = ((TourStopAdapter.DeleteTourStopEvent) event).adapterPosition;
            dataFragment.deleteTourStop(position);
            tourStopAdapter.notifyItemRemoved(position);

        } else if (event instanceof OnTourLoadEvent) {
            nameEdit.setText(dataFragment.getInitialTour().name);
            updateTourStopView();

            showLocationPermissionRequestUi();

        } else if (event instanceof OnPermissionGrantEvent) {
            String permission = ((OnPermissionGrantEvent) event).permission;
            if (PermissionUtil.get().isLocationPermission(permission)) {
                hideLocationPermissionRequestUi();

                startLocationPublish();
            }
        }
    }

    @Override
    public void onStop() {
        unbindLocationService();

        super.onStop();
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

    ItemTouchHelper getDragDropHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    // Drag is started, change view indicating drag
                    ScreenKeyboardUtil.hideScreenKeyboard(getActivity());
                    viewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.accent));
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition(),
                        to = target.getAdapterPosition();
                dataFragment.moveStop(from, to);
                tourStopAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                // Load once
                if (mItemViewBackgroundResId == 0) {
                    int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
                    TypedArray typedArray = recyclerView.getContext().obtainStyledAttributes(attrs);
                    mItemViewBackgroundResId = typedArray.getResourceId(0, 0);
                    typedArray.recycle();
                }
                /*
                 * Padding save/restore is due to bug on 19-
                 * http://stackoverflow.com/questions/10095196/whered-padding-go-when-setting-background-drawable
                 */
                View view = viewHolder.itemView;
                int pL = view.getPaddingLeft();
                int pT = view.getPaddingTop();
                int pR = view.getPaddingRight();
                int pB = view.getPaddingBottom();

                view.setBackgroundResource(mItemViewBackgroundResId);

                view.setPadding(pL, pT, pR, pB);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }
        });
    }

    @OnClick(R.id.add_stop)
    public void onAddStop() {
        addStop();
    }

    private void addStop() {
        startActivity(SelectLandmarkActivity.getIntent(getActivity(), getClass(), getTourId()));
    }

    private void updateUi() {
        updateTourStopView();
        showLocationPermissionRequestUi();
    }

    private void updateTourStopView() {
        tourStopAdapter.setTourStopIds(dataFragment.getTourStops());
    }

    /**
     * Presents request for location permission if makes sense
     */
    private void showLocationPermissionRequestUi() {
        // Permission is already granted
        if (PermissionUtil.get().hasLocationPermission()) {
            return;
        }

        // Request has already been made, don't make again
        if (dataFragment.suppressPermissionRequest) {
            return;
        }

        // Don't request when there are no stops since distances aren't visible
        if (dataFragment.getTourStops().isEmpty()) {
            return;
        }

        // Views are not ready
        if (tourStopRecyclerView == null) {
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

        // Don't nag about permissions again while on this screen
        dataFragment.suppressPermissionRequest = true;

        mLocationPermissionSnackbar.dismiss();
        mLocationPermissionSnackbar = null;
    }

    private void requestLocationPermission() {
        boolean isRequestPermissionShown =
                PermissionUtil.get().showLocationPermissionFragment(getFragmentManager(), this);
        if (isRequestPermissionShown) {
            hideLocationPermissionRequestUi();
        }
    }

    // PermissionRequestFragment.OnPermissionRequestCompleteListener

    @Override
    public void onPermissionRequestComplete() {
        PermissionUtil.get().removeRequestLocationPermissionFragment(getFragmentManager());
    }

    // Methods

    private void showTourStopInfo(int dataIndex) {
        long landmarkId = dataFragment.getTourStops().get(dataIndex).landmark.id;
        startActivity(LandmarkDetailActivity.getIntent(getActivity(), landmarkId));
    }

    // Done/cancel

    private void publishEvent(boolean isCancel) {
        BusProvider.get().publish(new OnEditTourDoneEvent(isCancel));
    }

    @Override
    protected void onDone() {
        String name = nameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name) && dataFragment.getTourStops().size() > 0) {
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

    // Tours are loaded and ready for presenting
    private static class OnTourLoadEvent {
    }

    // Stores data (across config changes)
    public static class DataFragment extends RetainFragment {
        public boolean suppressPermissionRequest;

        private long tourId;

        private TourRepository tourRepository;

        private final AtomicReference<Tour> initialTour = new AtomicReference<>();

        private List<HistoricLandmarkDistance> mTourStops = new LinkedList<>();

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

                            LandmarkRepository repository = RepositoryProvider.getLandmark();
                            List<HistoricLandmarkDistance> stops = new LinkedList<>();
                            for (Long landmarkId : tour.getTourStopIds()) {
                                HistoricLandmark landmark = repository.getLandmark(landmarkId);
                                HistoricLandmarkDistance landmarkDistance =
                                        new HistoricLandmarkDistance(landmark);
                                stops.add(landmarkDistance);
                            }
                            mTourStops = stops;
                            publishLoad();
                        }
                    });
        }

        public boolean saveTour(long tourId, String name) {
            boolean isDefined = !TextUtils.isEmpty(name) || mTourStops.size() > 0;
            Tour initialTour = getInitialTour();
            if (initialTour == null && !isDefined) {
                return false;
            }

            Tour tour = new Tour(tourId, name);
            Observable.create(new SaveTourAction(tour, mTourStops, tourRepository))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new OnSaveTourAction(bus));
            return true;
        }

        public void addStop(long landmarkId) {
            HistoricLandmark landmark = RepositoryProvider.getLandmark().getLandmark(landmarkId);
            mTourStops.add(new HistoricLandmarkDistance(landmark));
        }

        public void deleteTourStop(int index) {
            mTourStops.remove(index);
        }

        public void moveStop(int from, int to) {
            HistoricLandmarkDistance prev = mTourStops.remove(from);
            mTourStops.add(to, prev);
        }

        @NonNull
        public List<HistoricLandmarkDistance> getTourStops() {
            return Collections.unmodifiableList(mTourStops);
        }
    }
}
