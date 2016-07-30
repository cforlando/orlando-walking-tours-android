package com.codefororlando.orlandowalkingtours.present.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class RetainFragment extends BaseFragment {
    public static <F extends RetainFragment> F getOrAdd(AppCompatActivity activity, Class<F> cls) {
        return getOrAdd(activity, activity.getSupportFragmentManager(), cls);
    }

    public static <F extends RetainFragment> F getOrAdd(Fragment fragment, Class<F> cls) {
        return getOrAdd(fragment, cls, null);
    }

    public static <F extends RetainFragment> F getOrAdd(Fragment fragment,
                                                        Class<F> cls,
                                                        Bundle arguments) {
        return getOrAdd(
                fragment.getActivity(),
                fragment.getFragmentManager(),
                cls.getName(),
                arguments
        );
    }

    public static <F extends RetainFragment> F getOrAdd(Context context,
                                                        FragmentManager fragmentManager,
                                                        Class<F> cls) {
        return getOrAdd(context, fragmentManager, cls.getName(), null);
    }

    @SuppressWarnings("unchecked")
    public static <F extends RetainFragment> F getOrAdd(Context context,
                                                        FragmentManager fragmentManager,
                                                        String fragmentName,
                                                        Bundle arguments) {
        F fragment = (F) fragmentManager.findFragmentByTag(fragmentName);
        if (fragment == null) {
            fragment = (F) Fragment.instantiate(context, fragmentName, arguments);
            fragmentManager.beginTransaction()
                    .add(fragment, fragmentName)
                    .commit();
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
