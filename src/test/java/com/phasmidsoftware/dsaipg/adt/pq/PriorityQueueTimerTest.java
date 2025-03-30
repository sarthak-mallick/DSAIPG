package com.phasmidsoftware.dsaipg.adt.pq;

import org.junit.Test;

import java.util.*;
import com.phasmidsoftware.dsaipg.util.Timer;

public class PriorityQueueTimerTest {

    public static Integer[] generateRandomArray(int min, int max, int length) {
        Random rand = new Random();
        Integer[] arr = new Integer[length];
        for (int i = 0; i < length; i++) {
            arr[i] = min + rand.nextInt(max - min);
        }
        return arr;
    }

    @Test
    public void timePriorityQueue() {
        Timer timer = new Timer();
        for (int j = 0; j < 2; j++) {
            boolean floyd = j==0 ? false : true;
            String message = j==0 ? "Not Floyd" : "Floyd";

            for(int k=0; k<=5; k++) {
                Integer[] intsToGive = generateRandomArray(-100000, 100000, 160000);
                int heapSize = (int) (5125 * Math.pow(2, k));
                double binaryMeanTime = this.meanTimeBinary(heapSize, timer, floyd, intsToGive, true);
                binaryMeanTime = this.meanTimeBinary(heapSize, timer, floyd, intsToGive, false);
                System.out.println(message + " heap size=" + heapSize + " binary heap time=" + binaryMeanTime);
            }

            for(int k=0; k<=5; k++) {
                Integer[] intsToGive = generateRandomArray(-100000, 100000, 160000);
                int heapSize = (int) (5125 * Math.pow(2, k));
                double quarternaryMeanTime = this.meanTime4Ary(heapSize, timer, floyd, intsToGive, true);
                quarternaryMeanTime = this.meanTime4Ary(heapSize, timer, floyd, intsToGive, false);
                System.out.println(message + " heap size=" + heapSize + " quarternary heap time=" + quarternaryMeanTime);
            }
        }
    }

    private double meanTimeBinary (int heapSize, Timer timer, boolean floyd, Integer[] intsToGive, boolean warmup) {
        final double meanTime = timer.repeat(20,
        warmup, () -> null,
        arr -> {
            PriorityQueue<Integer> pq2 = new PriorityQueue<>(heapSize, true,
                    Comparator.comparing(Integer::intValue), floyd);
            for (int i = 1; i <= 160000; i++) {
                try {
                    // inter-leaved insertion and deletion
                    if (i % 4 != 0) pq2.give(intsToGive[i]);
                    else pq2.take();
                } catch (PQException e) {
                    e.printStackTrace();
                }
            }
            return pq2;
        },
        null,
        null);

        return meanTime;
    }

    private double meanTime4Ary (int heapSize, Timer timer, boolean floyd, Integer[] intsToGive, boolean warmup) {
        final double meanTime = timer.repeat(20,
        warmup, () -> null,
        arr -> {
            PriorityQueue4Ary<Integer> pq4 = new PriorityQueue4Ary<>(heapSize, true,
                    Comparator.comparing(Integer::intValue), floyd);
            for (int i = 1; i <= 160000; i++) {
                try {
                    // inter-leaved insertion and deletion
                    if (i % 4 != 0) pq4.give(intsToGive[i]);
                    else pq4.take();
                } catch (PQException e) {
                    e.printStackTrace();
                }
            }
            return pq4;
        },
        null,
        null);

        return meanTime;
    }
}
