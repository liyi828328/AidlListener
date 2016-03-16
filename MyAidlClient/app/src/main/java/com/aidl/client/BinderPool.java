package com.aidl.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.myaidl.server.IBinderPool;

import java.util.concurrent.CountDownLatch;

/**
 * ---------------------------------------------------------------
 * Author: LiYi
 * Create: 16-3-10 17:11
 * ---------------------------------------------------------------
 * Describe:
 * ---------------------------------------------------------------
 * Changes:
 * binder池
 * ---------------------------------------------------------------
 * 16-3-10 17 : Create by LiYi
 * ---------------------------------------------------------------
 */
public class BinderPool {

    private static volatile BinderPool mBinderPool;
    private CountDownLatch mCountDownLatch;
    private Context mContext;
    private IBinderPool mIBinderPool;

    private BinderPool(Context context) {
        this.mContext = context.getApplicationContext();
        mCountDownLatch = new CountDownLatch(1);
        connectBinderPoolService();
    }

    /**
     * 获取实例
     *
     * @param context
     * @return
     */
    public static BinderPool getInstance(Context context) {
        if (mBinderPool == null) {
            synchronized (BinderPool.class) {
                if (mBinderPool == null) {
                    mBinderPool = new BinderPool(context);
                }
            }
        }
        return mBinderPool;
    }

    /**
     * 绑定服务
     */
    private synchronized void connectBinderPoolService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setPackage("com.myaidl.server");
        serviceIntent.setAction("com.myaidl.server.binderPool.BIND");
        mContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBinderPool = IBinderPool.Stub.asInterface(service);
            Log.d(Utils.TAG, "binder pool connected");
            try {
                mIBinderPool.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            if (mIBinderPool != null) {
                mIBinderPool.asBinder().unlinkToDeath(this, 0);
                mIBinderPool = null;
            }
            connectBinderPoolService();
        }
    };

    /**
     * 根据code来获取相应的binder对象
     *
     * @param binderCode
     * @return
     */
    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        if (mIBinderPool != null) {
            try {
                binder = mIBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }
}
