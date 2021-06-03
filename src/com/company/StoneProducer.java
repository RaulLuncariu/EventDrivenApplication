package com.company;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public interface StoneProducer {
    LinkedBlockingQueue<Integer> stone_l = new LinkedBlockingQueue<>();
    int max_quantity = 31;

    default void produce() throws InterruptedException {
        int quantity = ThreadLocalRandom.current().nextInt(max_quantity);
        stone_l.add(quantity);

    }
}
