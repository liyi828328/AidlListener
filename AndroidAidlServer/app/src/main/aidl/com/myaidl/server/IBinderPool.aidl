// IBinderPool.aidl
package com.myaidl.server;

interface IBinderPool {
    IBinder queryBinder(in int binderCode);
}
