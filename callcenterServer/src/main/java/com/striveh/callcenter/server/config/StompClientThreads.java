package com.striveh.callcenter.server.config;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StompClientThreads {

  public static String TOKEN = "eyJ1c2VyTmFtZSI6Ind1ZGl4aWFvYmFvemkiLCJhbGciOiJSUzI1NiJ9.eyJleHAiOjE1Njc1OTE1NDYsInVzZXJOYW1lIjoid3VkaXhpYW9iYW96aSIsImlhdCI6MTU2NDk5OTU0Nn0.dEJzjgwwZCL6qh3NtluVSo0uZZZdUEzrNF2pLsUxprVOSE-pzaUVlOw2EmntXd4IpFs3qI0IwA4F51VOFIX65lc1RoX93AFeb44CYt9JpXKcGtGYWQr2D4nsNMaS7je8abtastBC8QIInCYtC7s8tvaAQRzYTvCZmSM8vtgu06g";

  public static String REQ_URL = "https://xxx/callcenter/ws";

  public static WebSocketStompClient stompClient;

    public static MyStompSessionHandler sessionHandler;

  public static void main(String[] args) throws InterruptedException {

    long clientThreadNum = 100;

    List<Transport> transports = new ArrayList<>(1);
    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
    WebSocketClient transport = new SockJsClient(transports);
    stompClient = new WebSocketStompClient(transport);

    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    sessionHandler = new MyStompSessionHandler();
    ExecutorService executorService = new ThreadPoolExecutor(50, 1000, 60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(500));

    // 每条线程处理链接数
    long singeDelNum = 100;
    while (clientThreadNum > 0) {
      long delNum = clientThreadNum > singeDelNum ? singeDelNum : clientThreadNum;
      try {
        executorService.submit(new DealThread(delNum));
      }
      catch (Exception e) {
        Thread.sleep(5000);
        continue;
      }
      clientThreadNum -= singeDelNum;
    }

    // 定时打印counter
    new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(5000);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("counter: " + sessionHandler.getCounter());

      }
    }).start();

    while (true) {
      Thread.sleep(Integer.MAX_VALUE);
    }

  }

  public static class DealThread implements Runnable {

    private long dealNum;

    public DealThread(long dealNum) {
      this.dealNum = dealNum;
    }

    @Override
    public void run() {
      int waitTime = 1;
      while (dealNum > 0) {

        try {
          Thread.sleep(waitTime);

          StompHeaders stompHeaders = new StompHeaders();
          stompHeaders.put("token", Arrays.asList(TOKEN));
          stompHeaders.put("projectId", Arrays.asList("fm"));

          // 设置心跳
           stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
           stompHeaders.setHeartbeat(new long[] { 10000, 10000 });

          ListenableFuture<StompSession> connect = stompClient.connect(new URI(REQ_URL), null, stompHeaders,
              sessionHandler);

          StompSession stompSession = connect.get();
          if (stompSession == null) {
            waitTime += 3000;
            continue;
          }
        }
        catch (Exception e) {
          System.out.println("this si errer ,retry:" + waitTime);
          e.printStackTrace();
          waitTime += 3000;
          continue;
        }

        // 走到后面说明连接成功,置回正常等待时间,处理下一位
        if (waitTime > 3000) {
          waitTime = 1;
        }
        dealNum--;
      }
      System.out.println(Thread.currentThread().getName() + " is die");
    }
  }
}