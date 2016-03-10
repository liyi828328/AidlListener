package com.myaidl.server.aidl;

import android.content.Context;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.myaidl.server.BookManager;
import com.myaidl.server.IBookManager;
import com.myaidl.server.IOnNewBookArrivedListener;
import com.myaidl.server.Utils;
import com.myaidl.server.model.Book;

import java.util.List;

/**
 * ---------------------------------------------------------------
 * Author: LiYi
 * Create: 16-3-3 10:27
 * ---------------------------------------------------------------
 * Describe:
 * ---------------------------------------------------------------
 * Changes:
 * ---------------------------------------------------------------
 * 16-3-3 10 : Create by LiYi
 * ---------------------------------------------------------------
 */
public class BookManagerBinder extends IBookManager.Stub {

    private BookManager mBookManager;
    private Context mContext;
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();

    public BookManagerBinder(Context context) {
        this.mContext = context;
        this.mBookManager = BookManager.getInstance();
        new Thread(new BookRunnable()).start();
    }

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        Log.d(Utils.TAG, "BookManagerBinder onTransact");
        //检查权限
        int callingPid = getCallingPid();
        int callingUid = getCallingUid();

        String callingPackageNmae = "";
        String[] packages = mContext.getPackageManager().getPackagesForUid(callingUid);
        if (packages != null && packages.length > 0) {
            for (int i = 0; i < packages.length; i++) {
                Log.d(Utils.TAG, "package name : " + packages[i]);
            }
            callingPackageNmae = packages[0];
        }
        if (!callingPackageNmae.startsWith("com.aidl")) {
            Log.d(Utils.TAG, "onTransact return false");
            return false;
        }
        return super.onTransact(code, data, reply, flags);
    }

    private void onNewBookArrived(Book book) {
        int listenerSize = mListenerList.beginBroadcast();
        for (int i = 0; i < listenerSize; i++) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            if (listener == null) {
                continue;
            }
            try {
                listener.onNewBookArrived(book);
                Log.d(Utils.TAG, "service onNewBookArrived");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mListenerList.finishBroadcast();
    }

    @Override
    public List<Book> getBookList() throws RemoteException {
        return mBookManager.getBookList();
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        if (book == null) {
            return;
        }
        mBookManager.addBook(book);
        onNewBookArrived(book);
    }

    @Override
    public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
        mListenerList.register(listener);
        Log.d(Utils.TAG, "registerListener , listener size: " + mListenerList.getRegisteredCallbackCount());
    }

    @Override
    public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
        mListenerList.unregister(listener);
        Log.d(Utils.TAG, "unregisterListener , listener size: " + mListenerList.getRegisteredCallbackCount());
    }

    private class BookRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    addBook(new Book("123", "服务中插入的书籍"));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
