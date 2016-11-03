package com.ethanco.halo.turbo.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description 线程池
 * Created by EthanCo on 2016/7/28.
 */
public class ThreadPool {
    private ExecutorService executorPool;

    public ThreadPool() {
        executorPool = Executors.newCachedThreadPool();
    }

    public ThreadPool(ExecutorService executorPool) {
        this.executorPool = executorPool;
    }

    public void execute(Runnable runnable) {
        executorPool.execute(runnable);
    }
}
