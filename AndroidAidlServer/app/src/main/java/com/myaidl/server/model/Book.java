package com.myaidl.server.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ---------------------------------------------------------------
 * Author: LiYi
 * Create: 16-3-3 10:20
 * ---------------------------------------------------------------
 * Describe:
 * ---------------------------------------------------------------
 * Changes:
 * ---------------------------------------------------------------
 * 16-3-3 10 : Create by LiYi
 * ---------------------------------------------------------------
 */
public class Book implements Parcelable {

    private String bookId;
    private String bookName;

    public Book() {

    }

    public Book(String bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    protected Book(Parcel in) {
        bookId = in.readString();
        bookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookId);
        dest.writeString(bookName);
    }

    @Override
    public String toString() {
        return "[ bookId: " + bookId + " name: " + bookName + "]";
    }
}
