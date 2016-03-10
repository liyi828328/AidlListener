package com.myaidl.server;

import com.myaidl.server.model.Book;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ---------------------------------------------------------------
 * Author: LiYi
 * Create: 16-3-3 10:39
 * ---------------------------------------------------------------
 * Describe:
 * ---------------------------------------------------------------
 * Changes:
 * ---------------------------------------------------------------
 * 16-3-3 10 : Create by LiYi
 * ---------------------------------------------------------------
 */
public class BookManager {

    private static BookManager mBookManager;
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private BookManager() {

        //初始化一些书籍
        Book androidBook = new Book("1", "Android开发艺术探索");
        Book iosBook = new Book("2", "IOS开发入门");
        Book cBook = new Book("3", "C++");

        mBookList.add(androidBook);
        mBookList.add(iosBook);
        mBookList.add(cBook);
    }

    private static synchronized void syncInit() {
        if (mBookManager == null) {
            mBookManager = new BookManager();
        }
    }

    public static BookManager getInstance() {
        if (mBookManager == null) {
            syncInit();
        }
        return mBookManager;
    }

    public List<Book> getBookList() {
        return mBookList;
    }

    public void addBook(Book book) {
        if (book == null) {
            return;
        }
        mBookList.add(book);
    }
}