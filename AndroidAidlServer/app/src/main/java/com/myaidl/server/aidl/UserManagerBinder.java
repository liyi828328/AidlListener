package com.myaidl.server.aidl;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.myaidl.server.IUserManager;
import com.myaidl.server.Utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ---------------------------------------------------------------
 * Author: LiYi
 * Create: 16-3-10 10:57
 * ---------------------------------------------------------------
 * Describe:
 * ---------------------------------------------------------------
 * Changes:
 * ---------------------------------------------------------------
 * 16-3-10 10 : Create by LiYi
 * ---------------------------------------------------------------
 */
public class UserManagerBinder extends IUserManager.Stub {

    private ConcurrentHashMap<String, String> userList = new ConcurrentHashMap<>();

    @Override
    public void login(String userName, String pwd) throws RemoteException {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            return;
        }
        userList.put(userName, pwd);
        Log.d(Utils.TAG, "user login userList: " + userList.size());
    }

    @Override
    public boolean logout(String userName) throws RemoteException {
        if (userList.contains(userName)) {
            userList.remove(userName);
            Log.d(Utils.TAG, "user logout userList: " + userList.size());
            return true;
        }
        return false;
    }
}
