package com.myaidl.server;

import com.myaidl.server.model.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}