package com.lzb.tester.common;

public class DeveloperTest {

    public static void main(String[] args) {
        System.out.println();
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.yield();
    }
}
