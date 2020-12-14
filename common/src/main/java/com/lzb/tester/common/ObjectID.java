package com.lzb.tester.common;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ObjectID {

    private static char[] serialNo = {'1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z'};

    public static String randomString(int digits) {
        Random random = new Random();
        char[] arr = new char[digits];
        for (int i = 0; i < digits; i++) {
            arr[i] = serialNo[random.nextInt(serialNo.length)];
        }
        return new String(arr).toLowerCase();
    }

    public static String randomString() {
        return randomString(20);
    }

    public static int randomInt() {
        Random random = new Random();
        int prefix = 0;
        do {
            prefix = random.nextInt(10);
        } while (prefix == 0);
        return (int) ((random.nextDouble() + prefix) * (5 << 20));
    }

    public static long randomLong(){
        Random random = new Random();
        int prefix = 0;
        do {
            prefix = random.nextInt(10);
        } while (prefix == 0);
        return (long) ((random.nextDouble() + prefix) * (100 << 20));
    }
}
