package com.aidl.client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.binder_pool_activity).setOnClickListener(this);
        findViewById(R.id.book_service_activity).setOnClickListener(this);
    }

    @Override
    protected void handleMessage(Message msg) {

    }

    @Override
    protected void onMenuHome() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.binder_pool_activity) {
            startActivity(new Intent(this, BinderPoolActivity.class));
        } else if (id == R.id.book_service_activity) {
            startActivity(new Intent(this, BookServiceActivity.class));
        }
    }
}
