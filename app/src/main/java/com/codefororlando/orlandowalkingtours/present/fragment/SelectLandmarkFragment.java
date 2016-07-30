package com.codefororlando.orlandowalkingtours.present.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.codefororlando.orlandowalkingtours.BusProvider;
import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.RepositoryProvider;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistance;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistanceSelect;
import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepository;
import com.codefororlando.orlandowalkingtours.event.OnCancelSelectLandmarkEvent;
import com.codefororlando.orlandowalkingtours.event.OnLocationChangeEvent;
import com.codefororlando.orlandowalkingtours.event.OnQueryLandmarksEvent;
import com.codefororlando.orlandowalkingtours.event.OnSelectLandmarkEvent;
import com.codefororlando.orlandowalkingtours.present.activity.LandmarkDetailActivity;
import com.codefororlando.orlandowalkingtours.present.base.DoneCancelBarLocationFragment;
import com.codefororlando.orlandowalkingtours.present.base.RetainFragment;
import com.codefororlando.orlandowalkingtours.rx.LoadLandmarksAction;
import com.codefororlando.orlandowalkingtours.ui.LandmarkSelectAdapter;
import com.codefororlando.orlandowalkingtours.util.PermissionUtil;
import com.codefororlando.orlandowalkingtours.util.ScreenKeyboardUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class SelectLandmarkFragment extends DoneCancelBarLocationFragment {
    public static final String CALLER_KEY = "CALLER_KEY";
    public static final String TOUR_ID_KEY = "TOUR_ID_KEY";

    private static final String LAYOUT_MANAGER_STATE_KEY = "LAYOUT_MANAGER_STATE_KEY";

    public static SelectLandmarkFragment newInstance(Serializable caller, long tourId) {
        SelectLandmarkFragment fragment = new SelectLandmarkFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CALLER_KEY, caller);
        bundle.putLong(TOUR_ID_KEY, tourId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(android.R.id.list)
    RecyclerView landmarkRecyclerView;
    @BindView(R.id.action_bar)
    View actionBar;
    @BindView(R.id.search)
    SearchView searchView;
    @BindView(R.id.toggle_view_action)
    View toggleViewAction;

    private DataFragment dataFragment;

    private LandmarkSelectAdapter mLandmarkAdapter;

    // Keeps recycler view scroll position on config change
    private Parcelable layoutManagerState;

    // Lifecycle/event

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataFragment = RetainFragment.getOrAdd(this, DataFragment.class, getArguments());

        // Restore list viewing position
        if (savedInstanceState != null) {
            layoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        wireActionBar();
        return view;
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

        } else if (event instanceof OnTourLoadEvent) {
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

    private void wireActionBar() {
        // Bottom sheet shows/hides on swipe down/up
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(actionBar);
        behavior.setHideable(true);
        landmarkRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                } else {
                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ScreenKeyboardUtil.hideScreenKeyboard(getActivity());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                dataFragment.onSearch(s.trim());
                return false;
            }
        });

        updateMapViewAction();
    }

    private void updateMapViewAction() {
        int visibility = PermissionUtil.get().hasLocationPermission() ? View.VISIBLE : View.GONE;
        toggleViewAction.setVisibility(visibility);
    }

    @OnClick(R.id.toggle_view_action)
    public void onToggleView() {
        /*
         * TODO Request location permission if not granted first.
         *      Toggle between RecyclerView and MapView in layout
         *      Toggle the icon and contentDescription of this action as well
         *      Show landmarks and allow selection of landmarks not yet selected.
         *      keeping done/cancel and action bar in both views.
         */
        logD("Be kind and implement map view of landmarks");
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
        mLandmarkAdapter.setTourStopIds(dataFragment.getTourStopIds());
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

    private static class OnTourLoadEvent {
    }

    private static class OnLandmarkLoadEvent {
    }

    private static class PositionId {
        private static final PositionId NO_POSITION_ID = new PositionId(-1, 0);

        public final int position;
        public final long id;

        public PositionId(int position, long id) {
            this.position = position;
            this.id = id;
        }
    }

    public static class DataFragment extends RetainFragment {
        private List<HistoricLandmarkDistanceSelect> mLandmarks = new ArrayList<>(0);

        private PositionId mSelectedPositionId = PositionId.NO_POSITION_ID;

        private static final String IS_ALPHA_SORT_KEY = "IS_ALPHA_SORT_KEY";
        private SharedPreferences activityPreferences;

        private final AtomicReference<Location> lastLocationAr = new AtomicReference<>();

        private Collection<Long> mTourStopIds = Collections.emptySet();

        private final AtomicReference<String> lastQuery = new AtomicReference<>("");

        private final PublishSubject<String> querySubject = PublishSubject.create();

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            activityPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            busSubscribe();

            loadTour(getArguments().getLong(TOUR_ID_KEY, 0));

            loadLandmarks(lastQuery.get());

            querySubject
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .debounce(150, TimeUnit.MILLISECONDS)
                    .filter(new Func1<String, Boolean>() {
                        @Override
                        public Boolean call(String s) {
                            synchronized (lastQuery) {
                                if (s == null) {
                                    s = "";
                                }

                                String last = lastQuery.getAndSet(s);
                                return !last.equals(s);
                            }
                        }
                    })
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            loadLandmarks(s);
                        }
                    });
        }

        @Override
        protected void onEvent(Object event) {
            if (event instanceof OnQueryLandmarksEvent) {
                loadLandmarks(lastQuery.get());

            } else if (event instanceof OnLocationChangeEvent) {
                Location location = ((OnLocationChangeEvent) event).location;
                lastLocationAr.set(location);

                if (!isAlphaSort()) {
                    // Easier than creating more methods to sort by distance and republishing
                    loadLandmarks(lastQuery.get());
                }
            }
        }

        @Override
        public void onDestroy() {
            busUnsubscribe();
            querySubject.onCompleted();
            super.onDestroy();
        }

        public List<HistoricLandmarkDistanceSelect> getLandmarkData() {
            return Collections.unmodifiableList(mLandmarks);
        }

        public Collection<Long> getTourStopIds() {
            return Collections.unmodifiableCollection(mTourStopIds);
        }

        public void setSelection(int adapterPosition, long landmarkId) {
            mSelectedPositionId = new PositionId(adapterPosition, landmarkId);
        }

        public int getSelectedAdapterPosition() {
            return mSelectedPositionId.position;
        }

        public long getSelectedLandmarkId() {
            return mSelectedPositionId.id;
        }

        public void onSearch(String query) {
            querySubject.onNext(query);
        }

        private boolean isAlphaSort() {
            return activityPreferences.getBoolean(IS_ALPHA_SORT_KEY, false);
        }

        private void loadTour(final long tourId) {
            if (tourId <= 0) {
                return;
            }

            Observable.create(new Observable.OnSubscribe<Collection<Long>>() {
                @Override
                public void call(Subscriber<? super Collection<Long>> subscriber) {
                    Tour tour = RepositoryProvider.getTour().get(tourId);
                    Collection<Long> stopIds = new HashSet<>();
                    if (tour != null) {
                        stopIds.addAll(tour.getTourStopIds());
                    }
                    subscriber.onNext(stopIds);
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Collection<Long>>() {
                        @Override
                        public void call(Collection<Long> stopIds) {
                            mTourStopIds = stopIds;
                            bus.publish(new OnTourLoadEvent());
                        }
                    });
        }

        private void loadLandmarks(final String query) {
            Location location = lastLocationAr.get();
            boolean isAlphaSort = isAlphaSort() || location == null;
            Comparator<HistoricLandmarkDistance> comparator = isAlphaSort
                    ? HistoricLandmarkDistance.NAME_COMPARATOR
                    : new HistoricLandmarkDistance.SquareDistanceComparator(location);
            LandmarkRepository repository = RepositoryProvider.getLandmark();
            LoadLandmarksAction action =
                    new LoadLandmarksAction(repository, location, comparator, query);

            Observable.create(action)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<HistoricLandmarkDistanceSelect>>() {
                        @Override
                        public void call(List<HistoricLandmarkDistanceSelect> landmarks) {
                            // Query has changed, ignore results
                            if (!query.equals(lastQuery.get())) {
                                return;
                            }

                            restoreSelectedLandmark(landmarks);

                            mLandmarks = landmarks;

                            bus.publish(new OnLandmarkLoadEvent());
                        }
                    });
        }

        /**
         * Restores the previous selection data if still consistent or resets selection state
         */
        private void restoreSelectedLandmark(List<HistoricLandmarkDistanceSelect> landmarks) {
            int position = mSelectedPositionId.position;
            if (position >= 0 && position < landmarks.size()) {
                HistoricLandmarkDistanceSelect landmarkDistanceSelect = landmarks.get(position);
                // Previously selected is still in the same position
                if (landmarkDistanceSelect.landmark.id == mSelectedPositionId.id) {
                    landmarkDistanceSelect.isSelected = true;
                }
                // A different landmark is in the previously selected position
                else {
                    mSelectedPositionId = PositionId.NO_POSITION_ID;
                }

            }
            // Previously selected position is now out of bounds
            else {
                mSelectedPositionId = PositionId.NO_POSITION_ID;
            }
        }
    }
}
