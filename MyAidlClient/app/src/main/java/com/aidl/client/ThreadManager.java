package com.aidl.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ---------------------------------------------------------------
 * Author: LiYi
 * Create: 2015/7/17 13:46
 * ---------------------------------------------------------------
 * Describe: 线程池
 * ---------------------------------------------------------------
 * Changes:
 * ---------------------------------------------------------------
 * 2015/7/17 13 : Create by LiYi
 * ---------------------------------------------------------------
 */
public class ThreadManager {

    private static ThreadManager mThreadManager;
    private ThreadPoolExecutor mThreadPoolExecutor;
    /**
     * 核心工作线程的个数
     */
    private final int CORE_POOL_SIZE = 5;
    /**
     * 核心工作线程空闲后存活的时间
     */
    private final long CORE_POOL_SIZE_KEEP_TIME = 120L;
    /**
     * 存储任务的队列
     */
    private final LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
    /**
     * 数据库线程异常捕获，主要负责runtimeExecption的捕获
     */
    private final WorkTaskUnExecptionHandler mExecptionHandler = new WorkTaskUnExecptionHandler();

    private ThreadManager() {
        mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, Integer.MAX_VALUE,
                0L, TimeUnit.MILLISECONDS,
                taskQueue, new WorkTaskFactory());
        mThreadPoolExecutor.setKeepAliveTime(CORE_POOL_SIZE_KEEP_TIME, TimeUnit.SECONDS);
    }

    /**
     * 初始化
     */
    private static synchronized void syncInit() {
        if (mThreadManager == null) {
            mThreadManager = new ThreadManager();
        }
    }

    /**
     * 获取线程池管理对象
     *
     * @return
     */
    public static ThreadManager getInstance() {
        if (mThreadManager == null) {
            syncInit();
        }
        return mThreadManager;
    }

    /**
     * 执行Runnable
     *
     * @param run
     */
    public void execute(Runnable run) {
        if (run == null || mThreadPoolExecutor == null) {
            return;
        }
        try {
            mThreadPoolExecutor.execute(run);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消Runnable
     *
     * @param run
     */
    public void cancel(Runnable run) {
        if (run == null || mThreadPoolExecutor == null) {
            return;
        }
        BlockingQueue<Runnable> queue = mThreadPoolExecutor.getQueue();
        if (queue != null && queue.size() > 0) {
            queue.remove(run);
        }
    }

    /**
     * 取消全部线程
     */
    public void cancelAll() {
        if (mThreadPoolExecutor == null) {
            return;
        }
        BlockingQueue<Runnable> queue = mThreadPoolExecutor.getQueue();
        if (queue != null && queue.size() > 0) {
            queue.clear();
        }
    }

    /**
     * 关闭线程池
     */
    public void shutDown() {
        if (mThreadPoolExecutor == null) {
            return;
        }
        mThreadPoolExecutor.shutdown();
        mThreadPoolExecutor = null;
    }

    /**
     * 数据库线程池的工厂类
     */
    class WorkTaskFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return getWorkThread(r);
        }

        private Thread getWorkThread(Runnable r) {
            Thread task = new Thread(r);
            task.setPriority(Thread.NORM_PRIORITY);
            task.setUncaughtExceptionHandler(mExecptionHandler);
            return task;
        }
    }

    /**
     * 线程异常捕获类；
     */
    class WorkTaskUnExecptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            // TODO 发现异常的处理
            ex.printStackTrace();
        }
    }
}
