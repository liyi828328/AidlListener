package com.myaidl.server.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.myaidl.server.Utils;

public class BinderPoolService extends Service {

    private BinderPoolManager mBinder;

    public BinderPoolService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new BinderPoolManager(getApplicationContext());
            mBinder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Log.d(Utils.TAG, "server binder died");
                    mBinder = null;
                }
            }, 0);
        }
        return mBinder;
    }
}
