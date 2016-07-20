package com.codefororlando.orlandowalkingtours.present.base;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codefororlando.orlandowalkingtours.R;

import butterknife.BindView;
import butterknife.ButterKnife;

// Requires layout contain Toolbar with ID 'app_bar'
abstract public class AppBarActivity extends BaseActivity {
    @BindView(R.id.app_bar)
    Toolbar actionBar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

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
}
