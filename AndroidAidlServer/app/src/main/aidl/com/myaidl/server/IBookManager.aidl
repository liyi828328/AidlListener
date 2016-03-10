// IBookManager.aidl
package com.myaidl.server;

import com.myaidl.server.model.Book;
import com.myaidl.server.IOnNewBookArrivedListener;

interface IBookManager {

    /**
    *   获取全部数据
    **/
    List<Book> getBookList();

    /**
    *   添加书籍
    **/
    void addBook(in Book book);

    /**
    *   添加书籍变更监听
    **/
    void registerListener(IOnNewBookArrivedListener listener);

    /**
    *   解绑书籍变更监听
    **/
    void unregisterListener(IOnNewBookArrivedListener listener);
}
