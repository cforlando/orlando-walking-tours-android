package com.codefororlando.orlandowalkingtours.present.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codefororlando.orlandowalkingtours.R;

import butterknife.BindView;
import butterknife.ButterKnife;

abstract public class AppBarActivity extends BaseActivity {
    @BindView(R.id.app_bar)
    Toolbar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());

        ButterKnife.bind(this);

        setSupportActionBar(actionBar);
    }

    protected void showActionBarHome() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    protected void setNavigationHomeBackPress() {
        actionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    abstract protected int getLayoutResId();
}
