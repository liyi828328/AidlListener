package com.aidl.client;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.myaidl.server.IBookManager;
import com.myaidl.server.IUserManager;
import com.myaidl.server.model.Book;

import java.util.List;

public class BinderPoolActivity extends BaseActivity implements View.OnClickListener {

    private static final int BIND_SUCCESS = 1;
    private BinderPool mBinderPool;
    private Context mContext;
    private IUserManager mUserManager;
    private IBookManager mBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);
        mContext = getApplicationContext();
        bindService();
        initView();
    }

    private void initView() {
        findViewById(R.id.user_login).setOnClickListener(this);
        findViewById(R.id.user_logout).setOnClickListener(this);
        findViewById(R.id.add_book).setOnClickListener(this);
        findViewById(R.id.get_book_list).setOnClickListener(this);
    }

    private void bindService() {
        mThreadManager.execute(binderServiceRunnable);
    }

    private Runnable binderServiceRunnable = new Runnable() {
        @Override
        public void run() {
            mBinderPool = BinderPool.getInstance(mContext);
            Message msg = Message.obtain();
            msg.what = BIND_SUCCESS;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case BIND_SUCCESS:
                Toast.makeText(mContext, "bind success", Toast.LENGTH_SHORT).show();
                IBinder binder1 = mBinderPool.queryBinder(Utils.USER_BINDER_CODE);
                mUserManager = IUserManager.Stub.asInterface(binder1);
                IBinder binder2 = mBinderPool.queryBinder(Utils.BOOK_BINDER_CODE);
                mBookManager = IBookManager.Stub.asInterface(binder2);
                break;
        }
    }

    @Override
    protected void onMenuHome() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.user_login) {
            try {
                mUserManager.login("123", "123");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.user_logout) {
            try {
                mUserManager.logout("123");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.add_book) {
            try {
                mBookManager.addBook(new Book("123", "addBook"));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.get_book_list) {
            try {
                List<Book> bookList = mBookManager.getBookList();
                Log.d(Utils.TAG, "get book list size: " + bookList.size());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
