// IUserManager.aidl
package com.myaidl.server;

// 用户管理

interface IUserManager {

    /**
    *   登录
    **/
   void login(in String userName,in String pwd);
    /**
    *   登出
    **/
   boolean logout(in String userName);

}
