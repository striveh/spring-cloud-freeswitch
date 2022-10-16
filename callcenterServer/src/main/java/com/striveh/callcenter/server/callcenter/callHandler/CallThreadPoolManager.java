package com.striveh.callcenter.server.callcenter.callHandler;

import link.thingscloud.freeswitch.esl.InboundClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.*;

public class CallThreadPoolManager {

    protected Logger log = LogManager.getLogger(this.getClass());

    // 线程池维护线程的最少数量
    private final static int CORE_POOL_SIZE = 2;
    // 线程池维护线程的最大数量
    private final static int MAX_POOL_SIZE = 10;
    // 线程池维护线程所允许的空闲时间
    private final static int KEEP_ALIVE_TIME = 0;
    // 线程池所使用的缓冲队列大小
    private final static int WORK_QUEUE_SIZE = 50;

    private InboundClient inboundClient;

    public CallThreadPoolManager(InboundClient inboundClient) {
        this.inboundClient = inboundClient;
    }

    Queue<String> callQueue = new LinkedBlockingDeque<>();

    final RejectedExecutionHandler handler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            callQueue.offer(((CallThread)r).getCallStr());
            log.info("将{}加入缓存队列。。。",((CallThread)r).getCallStr());
        }
    };

    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue(WORK_QUEUE_SIZE), this.handler);

    public void addCall(String callStr){
        log.info("将{}加入线程池。。。",callStr);
        threadPool.execute(new CallThread(inboundClient,callStr));
    }

    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    final ScheduledFuture scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
            if (!callQueue.isEmpty()){
                while (threadPool.getQueue().size()<WORK_QUEUE_SIZE){
                    String callStr = callQueue.poll();
                    if (StringUtils.isNotEmpty(callStr)){
                        log.info("将缓存队列中{}加入线程池。。。",callStr);
                        threadPool.execute(new CallThread(inboundClient,callStr));
                    }else {
                        break;
                    }
                }
            }
        }
    },0,1,TimeUnit.SECONDS);

    public Queue<String> getCallQueue() {
        return callQueue;
    }

    public void shutdown(){
        scheduledFuture.cancel(false);
        scheduler.shutdown();
        threadPool.shutdown();
    }
}
