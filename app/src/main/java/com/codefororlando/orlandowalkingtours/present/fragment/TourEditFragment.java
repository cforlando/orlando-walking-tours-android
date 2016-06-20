package com.codefororlando.orlandowalkingtours.present.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.codefororlando.orlandowalkingtours.BusProvider;
import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.RepositoryProvider;
import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.data.repository.TourRepository;
import com.codefororlando.orlandowalkingtours.event.OnEditTourDoneEvent;
import com.codefororlando.orlandowalkingtours.present.base.DoneCancelBarFragment;
import com.codefororlando.orlandowalkingtours.present.base.RetainFragment;
import com.codefororlando.orlandowalkingtours.rx.SaveTourAction;
import com.codefororlando.orlandowalkingtours.rx.SaveTourFunc;

import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TourEditFragment extends DoneCancelBarFragment {
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

    private DataFragment dataFragment;

    // Lifecycle/event

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataFragment = RetainFragment.getOrAdd(this, DataFragment.class);
        if (savedInstanceState == null) {
            dataFragment.setTourId(getTourId());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        busSubscribe();
    }

    @Override
    protected void onEvent(Object event) {
        if (event instanceof OnTourLoadEvent) {
            nameEdit.setText(dataFragment.getInitialTour().name);
        }
    }

    @Override
    public void onStop() {
        busUnsubscribe();
        super.onStop();
    }

    // UI/action

    @Override
    protected int getLayoutResId() {
        return R.layout.tour_edit_fragment;
    }

    // Done/cancel

    private void publishEvent(boolean isCancel) {
        BusProvider.get().publish(new OnEditTourDoneEvent(isCancel));
    }

    @Override
    protected void onDone() {
        String name = nameEdit.getText().toString().trim();

        if (!TextUtils.isEmpty(name)) {
            dataFragment.saveTour(getTourId(), name);
        }

        publishEvent(false);
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
        private long tourId;

        private TourRepository tourRepository;

        private final AtomicReference<Tour> initialTour = new AtomicReference<>();

        public void setTourId(long id) {
            tourId = id;
            logD("set Tour %d", tourId);
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
                            return tourRepository.getTour(id);
                        }
                    })
                    .subscribe(new Action1<Tour>() {
                        @Override
                        public void call(Tour tour) {
                            initialTour.set(tour);
                            bus.publish(new OnTourLoadEvent());
                        }
                    });
        }

        public void saveTour(long tourId, String name) {
            Tour initialTour = getInitialTour(),
                    tour = new Tour(tourId, name);
            if (initialTour == null ||
                    !name.equals(initialTour.name)) {
                Observable.just(tour)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new SaveTourFunc(tourRepository))
                        .subscribe(new SaveTourAction(bus));
            }
        }
    }
}
