package com.codefororlando.orlandowalkingtours.present.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.RepositoryProvider;
import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.data.repository.TourRepository;
import com.codefororlando.orlandowalkingtours.event.OnTourDeleteEvent;
import com.codefororlando.orlandowalkingtours.event.OnTourSaveEvent;
import com.codefororlando.orlandowalkingtours.present.activity.TourEditActivity;
import com.codefororlando.orlandowalkingtours.present.base.ButterKnifeFragment;
import com.codefororlando.orlandowalkingtours.present.base.RetainFragment;
import com.codefororlando.orlandowalkingtours.ui.TourAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ToursFragment extends ButterKnifeFragment {
    @BindView(android.R.id.list)
    RecyclerView tourRecyclerView;

    private DataFragment dataFragment;

    private TourAdapter mTourAdapter;

    // Lifecycle/event

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataFragment = RetainFragment.getOrAdd(this, DataFragment.class);
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
        if (event instanceof TourAdapter.EditTourEvent) {
            editTour(((TourAdapter.EditTourEvent) event).tourId);
        } else if (event instanceof TourAdapter.DeleteTourEvent) {
            deleteTour(((TourAdapter.DeleteTourEvent) event).tourId);
        } else if (event instanceof OnTourDataLoadEvent) {
            updateTourView();
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
        return R.layout.tours_fragment;
    }

    private void updateUi() {
        updateTourView();
    }

    private void updateTourView() {
        if (mTourAdapter == null) {
            mTourAdapter = new TourAdapter(bus);
            tourRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            tourRecyclerView.setAdapter(mTourAdapter);
        }
        mTourAdapter.setTours(dataFragment.getTourData());
    }

    @OnClick(R.id.new_tour)
    void onNewTour() {
        newTour();
    }

    protected void editTour(long tourId) {
        startActivity(TourEditActivity.getIntent(getActivity(), tourId));
    }

    protected void deleteTour(long tourId) {
        dataFragment.deleteTour(tourId);
    }

    protected void newTour() {
        startActivity(TourEditActivity.getIntent(getActivity()));
    }

    // Data

    // Internal event between UI and data fragments
    private static class OnTourDataLoadEvent {
    }

    public static class DataFragment extends RetainFragment {
        private final AtomicReference<List<Tour>> toursAr = new AtomicReference<>();
        private TourRepository tourRepository;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            busSubscribe();

            toursAr.set(new ArrayList<Tour>());

            tourRepository = RepositoryProvider.getTour();

            queryTours();
        }

        public List<Tour> getTourData() {
            return toursAr.get();
        }

        @Override
        protected void onEvent(Object event) {
            // Requery whenever tour data changes
            if (event instanceof OnTourSaveEvent) {
                queryTours();
            }
        }

        private void queryTours() {
            Observable.just(0)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Integer, List<Tour>>() {
                        @Override
                        public List<Tour> call(Integer integer) {
                            return tourRepository.getTours();
                        }
                    })
                    .subscribe(new Action1<List<Tour>>() {
                        @Override
                        public void call(List<Tour> tours) {
                            toursAr.set(tours);
                            bus.publish(new OnTourDataLoadEvent());
                        }
                    });
        }

        public void deleteTour(long tourId) {
            Observable.just(tourId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Long, Long>() {
                        @Override
                        public Long call(Long id) {
                            return tourRepository.delete(id);
                        }
                    })
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long id) {
                            bus.publish(new OnTourDeleteEvent(id));
                        }
                    });
        }
    }
}
