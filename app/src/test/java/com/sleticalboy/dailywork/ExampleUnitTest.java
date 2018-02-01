package com.sleticalboy.dailywork;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private final Object lock = new Object();

    private static int counter = 100;

    @Test
    public void addition_isCorrect() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void lock1() {
        synchronized (lock) {
            while (counter >= 0) {
                counter--;
                System.out.println(Thread.currentThread().getName() + " " + counter);
            }
            System.out.println(this);
        }
    }

    public void lock2() {
        synchronized (lock) {
            System.out.println(Thread.currentThread().getName() + " " + counter);
            System.out.println(this);
        }
    }
}