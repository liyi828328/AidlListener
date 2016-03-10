package com.aidl.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.myaidl.server.IBookManager;
import com.myaidl.server.IOnNewBookArrivedListener;
import com.myaidl.server.model.Book;

import java.util.List;

public class ClientActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    private IBookManager mIBookManager;
    private TextView queryBookContent;
    private ThreadManager mThreadManager;
    private static final int QUERY_BOOK_LIST_WHAT = 1;
    private static final int BOOK_ARRIVED_WHAT = 2;
    private StringBuffer mBookBuffer = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mThreadManager = ThreadManager.getInstance();
        bindView();
        bindMyService();
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case QUERY_BOOK_LIST_WHAT:
                List<Book> bookList = (List<Book>) msg.obj;
                if (bookList == null || bookList.size() == 0) {
                    break;
                }
                for (Book b : bookList) {
                    mBookBuffer.append(b.toString()).append("\n");
                }
                queryBookContent.setText(mBookBuffer.toString());
                break;
            case BOOK_ARRIVED_WHAT:
                Book newBook = (Book) msg.obj;
                mBookBuffer.append(newBook.toString()).append("\n");
                queryBookContent.setText(mBookBuffer.toString());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onMenuHome() {

    }

    private void bindView() {
        findViewById(R.id.query_booklist).setOnClickListener(this);
        findViewById(R.id.insert_book).setOnClickListener(this);
        queryBookContent = (TextView) findViewById(R.id.booklist_content);
    }

    private void bindMyService() {
        queryBookContent.setText("服务绑定中...");
        Intent serviceIntent = new Intent();
        serviceIntent.setPackage("com.myaidl.server");
        serviceIntent.setAction("com.myaidl.server.BIND");
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(Utils.TAG, "onServiceConnected");
            try {
                service.linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {
                        Log.d(Utils.TAG, "client binder died");
                        bindMyService();
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mIBookManager = IBookManager.Stub.asInterface(service);
            try {
                mIBookManager.registerListener(iOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            queryBookContent.setText("服务已绑定");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Utils.TAG, "onServiceDisconnected");
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mIBookManager == null) {
            bindMyService();
            queryBookContent.setText("服务未绑定");
            return;
        }
        if (id == R.id.query_booklist) {
            mThreadManager.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<Book> books = mIBookManager.getBookList();
                        Message msg = Message.obtain();
                        msg.what = QUERY_BOOK_LIST_WHAT;
                        msg.obj = books;
                        mHandler.sendMessage(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (id == R.id.insert_book) {
            Log.d(Utils.TAG, "insert book");
            try {
                mIBookManager.addBook(new Book("4", "新插入的书籍"));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private IOnNewBookArrivedListener iOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {

        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            Log.d(Utils.TAG, "onNewBookArrived book: " + newBook.toString());
            Message msg = Message.obtain();
            msg.obj = newBook;
            msg.what = BOOK_ARRIVED_WHAT;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mIBookManager != null && mIBookManager.asBinder().isBinderAlive()) {
                mIBookManager.unregisterListener(iOnNewBookArrivedListener);
                Log.d(Utils.TAG, "onDestroy unregisterListener");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
