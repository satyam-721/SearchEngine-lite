package com.satyam.SearchEngine.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ThreadServices implements Runnable {

    AtomicInteger round = new AtomicInteger(0);
    CrawlerService service;

    public ThreadServices(CrawlerService service){
        System.out.println("Initialised Service");
        this.service = service;
    }


    @Override
    public void run() {
        System.out.println("Thread started"+round.get());
        service.startCrawling( round.getAndIncrement() );

    }

    public void createCrawlThreads(){
        ThreadServices runnable = new ThreadServices(service);

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

        thread1.start();
//        thread2.start();
    }
}
