package com.myaidl.server.aidl;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.myaidl.server.IBinderPool;
import com.myaidl.server.Utils;

/**
 * ---------------------------------------------------------------
 * Author: LiYi
 * Create: 16-3-10 14:59
 * ---------------------------------------------------------------
 * Describe:
 * ---------------------------------------------------------------
 * Changes:
 * ---------------------------------------------------------------
 * 16-3-10 14 : Create by LiYi
 * ---------------------------------------------------------------
 */
public class BinderPoolManager extends IBinderPool.Stub {

    private Context mContext;
    private BookManagerBinder mBookManagerBinder;
    private UserManagerBinder mUserManagerBinder;

    public BinderPoolManager(Context context) {
        this.mContext = context;
        mBookManagerBinder = new BookManagerBinder(mContext);
        mUserManagerBinder = new UserManagerBinder();
    }

    @Override
    public IBinder queryBinder(int binderCode) throws RemoteException {
        Log.d(Utils.TAG, "service queryBinder , binder code : " + binderCode);
        IBinder binder = null;
        switch (binderCode) {
            case Utils.BOOK_BINDER_CODE:
                binder = mBookManagerBinder;
                break;
            case Utils.USER_BINDER_CODE:
                binder = mUserManagerBinder;
                break;
            default:
                break;
        }
        return binder;
    }
}
