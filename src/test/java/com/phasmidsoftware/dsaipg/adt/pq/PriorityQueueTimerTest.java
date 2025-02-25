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
        Integer[] intsToGive = generateRandomArray(-10000, 10000, 16000);
        for (int j = 0; j < 2; j++) {
            boolean floyd = j==0 ? false : true;
            String message = j==0 ? "Not Floyd" : "Floyd";

            final double binaryWarmUpMeanTime = timer.repeat(20,
                    true, () -> null,
                    arr -> {
                        PriorityQueue<Integer> pq = new PriorityQueue<>(4095, true,
                                Comparator.comparing(Integer::intValue), floyd);
                        for (int i = 0; i < 16000; i++) {
                            pq.give(intsToGive[i]);
                        }
                        for (int i = 0; i < 4000; i++) {
                            try {
                                pq.take();
                            } catch (PQException e) {
                                e.printStackTrace();
                            }
                        }
                        return pq;
                    },
                    null,
                    null);

            final double binaryMeanTime = timer.repeat(20,
                    false, () -> null,
                    arr -> {
                        PriorityQueue<Integer> pq2 = new PriorityQueue<>(4095, true,
                                Comparator.comparing(Integer::intValue), floyd);
                        for (int i = 0; i < 16000; i++) {
                            pq2.give(intsToGive[i]);
                        }
                        Integer highestSpilled = Integer.MIN_VALUE;
                        for (int i = 0; i < 4000; i++) {
                            try {
                                Integer result = pq2.take();
                                if (result > highestSpilled) {
                                    highestSpilled = result;
                                }
                            } catch (PQException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.print("Highest Priority Spilled=" + highestSpilled + " ");
                        return pq2;
                    },
                    null,
                    null);
            System.out.println(message + " binary heap time=" + binaryMeanTime);

            final double quarternaryWarmUpMeanTime = timer.repeat(20,
            true, () -> null,
            arr -> {
                PriorityQueue4Ary<Integer> pq = new PriorityQueue4Ary<>(4095, true,
                        Comparator.comparing(Integer::intValue), floyd);
                for (int i = 0; i < 16000; i++) {
                    pq.give(intsToGive[i]);
                }
                for (int i = 0; i < 4000; i++) {
                    try {
                        pq.take();
                    } catch (PQException e) {
                        e.printStackTrace();
                    }
                }
                return pq;
            },
            null,
            null);

            final double quarternaryMeanTime = timer.repeat(20,
                    false, () -> null,
                    arr -> {
                        PriorityQueue4Ary<Integer> pq4 = new PriorityQueue4Ary<>(4095, true,
                                Comparator.comparing(Integer::intValue), floyd);
                        for (int i = 0; i < 16000; i++) {
                            pq4.give(intsToGive[i]);
                        }
                        Integer highestSpilled = Integer.MIN_VALUE;
                        for (int i = 0; i < 4000; i++) {
                            try {
                                Integer result = pq4.take();
                                if (result > highestSpilled) {
                                    highestSpilled = result;
                                }
                            } catch (PQException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.print("Highest Priority Spilled=" + highestSpilled + " ");
                        return pq4;
                    },
                    null,
                    null);
            System.out.println(message + " quarternary heap time=" + quarternaryMeanTime);
        }
    }
}
