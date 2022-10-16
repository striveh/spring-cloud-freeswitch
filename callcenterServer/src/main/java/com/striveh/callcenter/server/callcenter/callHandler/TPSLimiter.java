package com.striveh.callcenter.server.callcenter.callHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TPSLimiter {

    private Semaphore semaphore = null;

    public TPSLimiter(int maxOps) {
        if (maxOps < 1) {
            throw new IllegalArgumentException("maxOps must be greater than zero");
        }
        this.semaphore = new Semaphore(maxOps,true);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() ->
                // 每秒释放给定数目的许可，将其返回到信号量
                semaphore.release(maxOps), 1000, 1500, TimeUnit.MILLISECONDS);
    }

    /**
     * 调用接口之前先调用此方法，当超过最大ops时该方法会阻塞
     */
    public void await() {
        semaphore.acquireUninterruptibly(1);
    }


    static AtomicInteger at = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        TPSLimiter limiter = new TPSLimiter(30);

        new Thread(() -> {
            while (true) {
                // 每秒输出一次AtomicInteger的最新值
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(at.get());
            }
        }).start();

        // 启动100个线程对AtomicInteger进行累加，为了方便就没有使用线程池
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                while (true) {
                    // 每次累加操作前先调用await方法，超过设定的ops时会阻塞线程
                    limiter.await();
                    at.incrementAndGet();
                }
            }).start();
        }
    }
}