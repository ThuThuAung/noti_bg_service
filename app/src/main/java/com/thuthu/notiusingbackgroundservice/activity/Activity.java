package com.thuthu.notiusingbackgroundservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.thuthu.notiusingbackgroundservice.bg_service.Androidservice;
import com.thuthu.notiusingbackgroundservice.R;

public class Activity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkNoti();
        }

    private void checkNoti() {
        startService(new Intent(this, Androidservice.class));
    }


}
