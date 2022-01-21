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

    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        int[] res = new int[2];
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                res[0] = i;
                res[1] = map.get(target - nums[i]);
                break;
            }
            map.put(nums[i], i);
        }
        return res;
    }

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        for (int i = m+1; i < m+n; i++) {
            nums1[i] = nums2[i-m-1];
        }
        Arrays.sort(nums1);
    }

    public static void main(String[] args) {
        int[] nums = {3,2,3};
        int[] ints = new testThread().twoSum(nums, 6);
        System.out.println(ints);
    }
}
