package com.company;


public class Farm implements IronProducer,StoneProducer,WoodProducer,Runnable{
    int max_quantity = 20;

    public synchronized void produce() throws InterruptedException {
        while(true) {
            IronProducer.super.produce();
            StoneProducer.super.produce();
            WoodProducer.super.produce();
            if(stone_l.size() < max_quantity || wood_l.size() < max_quantity || iron_l.size() < max_quantity) {
                continue;
            }

            notifyAll();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            produce();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
