package com.codefororlando.orlandowalkingtours.present.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.present.base.AppBarActivity;

public class MainActivity extends AppBarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_tours);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.main_activity;
    }
}
