package com.cslg.disk.example.test.service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class testThread {
    private static ExecutorService executors = Executors.newCachedThreadPool();

    public boolean containsDuplicate(int[] nums) {
        Arrays.sort(nums);
        int size = nums.length / 100;
        if (size > 10) {
            AtomicInteger flag = new AtomicInteger();
            for (int i = 1; i <= size; i++) {
                if (flag.get() == 1) {
                    executors.shutdownNow();
                    return true;
                }
                int start = (i-1)*10;
                int end = i*10;
                Task task = new Task(size,flag,nums,start,end,i);
                System.out.println("我是线程"+i);
                executors.submit(task);
            }
            if (executors.isShutdown()) {
                return true;
            }
            return false;
        }else {
            for (int i = 0; i < nums.length-1; i++) {
                if (nums[i] == nums[i+1]) {
                    return true;
                }
            }
            return false;
        }

    }

    class Task implements Runnable{
        private int size;
        private AtomicInteger flag;
        private int[] nums;

        private int id;
        public AtomicInteger getFlag() {
            return flag;
        }

        private int start;
        private int end;

        public Task(int size, AtomicInteger flag, int[] nums,int start,int end,int id) {
            this.size = size;
            this.flag = flag;
            this.nums = nums;
            this.start = start;
            this.end = end;
            this.id = id;
        }

        @Override
        public void run() {
            for (int i = start; i < end-1; i++) {
                if (executors.isShutdown()) {
                    break;
                }
                if (nums[i] == nums[i+1]) {
                    flag.set(1);
                    System.out.println("线程"+id+"中止");
                    executors.shutdownNow();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] nums = new int[20000];
        for (int i = 0; i < 20000; i++) {
            int random = (int) Math.random()*(5-1+1)+1;
            nums[i] = random;
        }
        boolean b = new testThread().containsDuplicate(nums);
        System.out.println(b);
    }
}
