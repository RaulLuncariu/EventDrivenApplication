package com.company;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public interface IronProducer {
     LinkedBlockingQueue<Integer> iron_l = new LinkedBlockingQueue<>();
     int max_quantity = 31;

     default void produce() throws InterruptedException {
        int quantity = ThreadLocalRandom.current().nextInt(max_quantity);
        iron_l.add(quantity);

    }
}

