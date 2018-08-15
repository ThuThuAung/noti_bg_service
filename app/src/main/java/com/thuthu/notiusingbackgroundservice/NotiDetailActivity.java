package com.thuthu.notiusingbackgroundservice;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.thuthu.notiusingbackgroundservice.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Thu Thu on 13/12/16.
 */
public class NotiDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    String title, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        // setup data
        title = getIntent().getStringExtra("Title");
        body = getIntent().getStringExtra("Body");

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }


}
