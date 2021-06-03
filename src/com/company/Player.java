package com.company;

import java.util.concurrent.ThreadLocalRandom;

public class Player implements Runnable{
    TradeCenter center;
    Farm farm;
    Integer iron = 0;
    Integer wood = 0;
    Integer stone = 0;
    int max_quantity = 100;
    public int level = 1;
    int name;
    int countdown = 5;
    int cooldown = 5;
    int cost = 100;
    int penalty = 50;

    Player(Farm farm,TradeCenter center){
        this.name = ThreadLocalRandom.current().nextInt((int) Double.POSITIVE_INFINITY);
        this.farm = farm;
        this.center = center;

    }

    synchronized void consume() throws InterruptedException {
        while (true) {
            while (farm.iron_l.size() == 0 || farm.wood_l.size() == 0 || farm.stone_l.size() == 0) {
                notifyAll();
                wait();
            }
            iron += farm.iron_l.poll();
            wood += farm.wood_l.poll();
            stone += farm.stone_l.poll();
            System.out.println("Player:"+name + " has: " + iron + " iron, " + stone + " stone and " + wood + " wood");
            level_up();
            win();
            countdown--;
            cooldown--;
            if(countdown == 0){
                steal();
            }
            if(cooldown == 0){
                trade();
            }

            Thread.sleep(1000);

        }
    }

    public void level_up(){
        if (iron >= max_quantity && wood >= max_quantity && stone >= max_quantity) {
            iron -= cost;
            wood -= cost;
            stone -= cost;
            level += 1;
            max_quantity += 100;
            cost += 100;
            penalty += 25;
            System.out.println("Player:"+name+" has leveled up to level " + level);
        }
    }

    public void win(){
        if (level >= 5){
            System.out.println("Player:"+name+" WON");
            System.exit(0);
        }
    }

    public void steal(){
        int chance = ThreadLocalRandom.current().nextInt(2);
        if(chance == 0){
            System.out.println("Player:"+name +" tried to steal from the farm but was caught!");
            if (iron < penalty){
                iron = 0;
            }else {
                iron -= penalty;
            }
            if (wood < penalty){
                wood = 0;
            }else {
                wood -= penalty;
            }if (stone < penalty){
                stone = 0;
            }else {
                stone -= penalty;
            }
        }else{
            System.out.println("Player:"+name +" tried to steal from the farm but was not caught!");
            iron += penalty;
            wood += penalty;
            stone += penalty;
        }
        countdown = 5;

    }
    public synchronized void trade() throws InterruptedException {
        cooldown = 5;
        int trade_amount;
        if(center.trade_iron.size() >= 1 && center.trade_wood.size() >= 1 && center.trade_stone.size() >= 1){
            int pick = ThreadLocalRandom.current().nextInt(1,4);
            switch (pick) {
                case 1 -> {
                    iron += center.trade_iron.poll();
                    System.out.println("Thread:"+name+" got some iron through the TradeCenter");
                }
                case 2 -> {
                    wood += center.trade_wood.poll();
                    System.out.println("Thread:"+name+" got some wood through the TradeCenter");
                }
                case 3 -> {
                    stone += center.trade_stone.poll();
                    System.out.println("Thread:"+name+" got some stone through the TradeCenter");
                }
                default -> System.out.println("Wrong Material!!!");
            }
        }else if(center.trade_iron.size() < 1 && center.trade_wood.size() >= 1 && center.trade_stone.size() >= 1){
            int pick = ThreadLocalRandom.current().nextInt(1,3);
            switch (pick){
                case 1 -> {
                    wood += center.trade_wood.poll();
                    System.out.println("Thread:"+name+" got some wood through the TradeCenter");
                }
                case 2 -> {
                    stone += center.trade_stone.poll();
                    System.out.println("Thread:"+name+" got some stone through the TradeCenter");
                }
                default -> System.out.println("Wrong Material1!!");
            }
        }else if(center.trade_iron.size() >= 1 && center.trade_wood.size() < 1 && center.trade_stone.size() >= 1) {
            int pick = ThreadLocalRandom.current().nextInt(1, 3);
            switch (pick) {
                case 1 -> {
                    iron += center.trade_iron.poll();
                    System.out.println("Thread:"+name+" got some iron through the TradeCenter");
                }
                case 2 -> {
                    stone += center.trade_stone.poll();
                    System.out.println("Thread:"+name+" got some stone through the TradeCenter");
                }
                default -> System.out.println("Wrong Material!1!");
            }
        }else if(center.trade_iron.size() >= 1 && center.trade_wood.size() >= 1 && center.trade_stone.size() < 1) {
            int pick = ThreadLocalRandom.current().nextInt(1, 3);
            switch (pick) {
                case 1 -> {
                    iron += center.trade_iron.poll();
                    System.out.println("Thread:"+name+" got some iron through the TradeCenter");
                }
                case 2 -> {
                    wood += center.trade_wood.poll();
                    System.out.println("Thread:"+name+" got some wood through the TradeCenter");
                }
                default -> System.out.println("Wrong Material!!1");
            }
        }else if(center.trade_iron.size() < 1 && center.trade_wood.size() < 1 && center.trade_stone.size() >= 1) {
            stone += center.trade_stone.poll();
            System.out.println("Thread:"+name+" got some stone through the TradeCenter");
        }else if(center.trade_iron.size() >= 1 && center.trade_wood.size() < 1 && center.trade_stone.size() < 1) {
            iron += center.trade_iron.poll();
            System.out.println("Thread:"+name+" got some iron through the TradeCenter");
        }else if(center.trade_iron.size() < 1 && center.trade_wood.size() >= 1 && center.trade_stone.size() < 1) {
            wood += center.trade_wood.poll();
            System.out.println("Thread:"+name+" got some wood through the TradeCenter");
        }else if(center.trade_iron.size() < 1 && center.trade_wood.size() < 1 && center.trade_stone.size() < 1) {
            wait();
        }

        if(center.trade_iron.size() >= 1 && center.trade_wood.size() >= 1 && center.trade_stone.size() >= 1){
            wait();
        }else if(center.trade_iron.size() < 1 && center.trade_wood.size() >= 1 && center.trade_stone.size() >= 1){
            float percent = ThreadLocalRandom.current().nextInt(1,71);
            trade_amount = (int) (iron*(percent/100));
            iron -= trade_amount;
            center.trade_iron.put(trade_amount);
            System.out.println("Thread:"+name+" put some iron in the TradeCenter");
            notify();
        }else if(center.trade_iron.size() >= 1 && center.trade_wood.size() < 1 && center.trade_stone.size() >= 1) {
            float percent = ThreadLocalRandom.current().nextInt(1,71);
            trade_amount = (int) (wood*(percent/100));
            wood -= trade_amount;
            center.trade_wood.put(trade_amount);
            System.out.println("Thread:"+name+" put some wood in the TradeCenter");
            notify();
        }else if(center.trade_iron.size() >= 1 && center.trade_wood.size() >= 1 && center.trade_stone.size() < 1) {
            float percent = ThreadLocalRandom.current().nextInt(1,71);
            trade_amount = (int) (stone*(percent/100));
            stone -= trade_amount;
            center.trade_stone.put(trade_amount);
            System.out.println("Thread:"+name+" put some stone in the TradeCenter");
            notify();
        }else if(center.trade_iron.size() < 1 && center.trade_wood.size() < 1 && center.trade_stone.size() >= 1) {
            int pick = ThreadLocalRandom.current().nextInt(1, 3);
            switch (pick) {
                case 1 -> {
                    float percent = ThreadLocalRandom.current().nextInt(1,71);
                    trade_amount =(int) (iron*(percent/100));
                    iron -= trade_amount;
                    center.trade_iron.put(trade_amount);
                    System.out.println("Thread:"+name+" put some iron in the TradeCenter");
                    notify();
                }
                case 2 -> {
                    float percent = ThreadLocalRandom.current().nextInt(1,71);
                    trade_amount = (int) (wood*(percent/100));
                    wood -= trade_amount;
                    center.trade_wood.put(trade_amount);
                    System.out.println("Thread:"+name+" put some wood in the TradeCenter");
                    notify();
                }
                default -> System.out.println("Wrong Material!!1");
            }
        }else if(center.trade_iron.size() >= 1 && center.trade_wood.size() < 1 && center.trade_stone.size() < 1) {
            int pick = ThreadLocalRandom.current().nextInt(1, 3);
            switch (pick) {
                case 1 -> {
                    float percent = ThreadLocalRandom.current().nextInt(1,71);
                    trade_amount = (int) (wood*(percent/100));
                    wood -= trade_amount;
                    center.trade_wood.put(trade_amount);
                    System.out.println("Thread:"+name+" put some wood in the TradeCenter");
                    notify();
                }
                case 2 -> {
                    float percent = ThreadLocalRandom.current().nextInt(1,71);
                    trade_amount =(int) (stone*(percent/100));
                    stone -= trade_amount;
                    center.trade_stone.put(trade_amount);
                    System.out.println("Thread:"+name+" put some stone in the TradeCenter");
                    notify();
                }
                default -> System.out.println("Wrong Material!!1");
            }
        }else if(center.trade_iron.size() < 1 && center.trade_wood.size() >= 1 && center.trade_stone.size() < 1) {
            int pick = ThreadLocalRandom.current().nextInt(1, 3);
            switch (pick) {
                case 1 -> {
                    float percent = ThreadLocalRandom.current().nextInt(1,71);
                    trade_amount = (int) (iron*(percent/100));
                    iron -= trade_amount;
                    center.trade_iron.put(trade_amount);
                    System.out.println("Thread:"+name+" put some iron in the TradeCenter");
                    notify();
                }
                case 2 -> {
                    float percent = ThreadLocalRandom.current().nextInt(1,71);
                    trade_amount = (int) (stone*(percent/100));
                    stone -= trade_amount;
                    center.trade_stone.put(trade_amount);
                    System.out.println("Thread:"+name+" put some stone in the TradeCenter");
                    notify();
                }
                default -> System.out.println("Wrong Material!!1");
            }
        }else if(center.trade_iron.size() < 1 && center.trade_wood.size() < 1 && center.trade_stone.size() < 1) {
            int pick = ThreadLocalRandom.current().nextInt(1,4);
            switch (pick) {
                case 1 -> {
                    float percent = ThreadLocalRandom.current().nextInt(1,71);
                    trade_amount = (int) (iron*(percent/100));
                    iron -= trade_amount;
                    center.trade_iron.put(trade_amount);
                    System.out.println("Thread:"+name+" put some iron in the TradeCenter");
                    notify();
                }
                case 2 -> {
                    float percent = ThreadLocalRandom.current().nextInt(1,71);
                    trade_amount = (int) (wood*(percent/100));
                    wood -= trade_amount;
                    center.trade_wood.put(trade_amount);
                    System.out.println("Thread:"+name+" put some wood in the TradeCenter");
                    notify();
                }
                case 3 -> {
                    float percent = ThreadLocalRandom.current().nextInt(1,71);
                    trade_amount = (int) (stone*(percent/100));
                    stone -= trade_amount;
                    center.trade_stone.put(trade_amount);
                    System.out.println("Thread:"+name+" put some stone in the TradeCenter");
                    notify();
                }
                default -> System.out.println("Wrong Material!!!");
            }
        }


    }

    @Override
    public void run() {
        try {
            System.out.println("Player:"+name+" started");
            consume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
