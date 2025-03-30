package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class PriorityQueue4Ary<K> implements Iterable<K> {
    public PriorityQueue4Ary(boolean max, Object[] quarternaryHeap, int first, int last, Comparator<K> comparator, boolean floyd) {
        this.max = max;
        this.first = first;
        this.comparator = comparator;
        this.last = last;
        //noinspection unchecked
        this.quarternaryHeap = (K[]) quarternaryHeap;
        this.floyd = floyd;
    }

    public PriorityQueue4Ary(int n, int first, boolean max, Comparator<K> comparator, boolean floyd) {
        this(max, new Object[n + first], first, 0, comparator, floyd);
    }

    public PriorityQueue4Ary(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        this(n, 1, max, comparator, floyd);
    }

    public PriorityQueue4Ary(int n, boolean max, Comparator<K> comparator) {
        this(n, 1, max, comparator, false);
    }

    public PriorityQueue4Ary(int n, Comparator<K> comparator) {
        this(n, 1, true, comparator, true);
    }

    public boolean isEmpty() {
        return last == 0;
    }

    public int size() {
        return last;
    }

    public void give(K key) {
        if (last == quarternaryHeap.length - first)
            last--; // if we are already at capacity, then we arbitrarily trash the least eligible element
        quarternaryHeap[++last + first - 1] = key; // insert the key into the 4ary heap just after the last element
        swimUp(last + first - 1); // reorder the 4ary heap
    }

    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        if (floyd) return doTake(this::snake);
        else return doTake(this::sink);
    }

    
    K doTake(Consumer<Integer> f) {
        K result = quarternaryHeap[first]; // get the root element (the largest or smallest, according to field max)
        swap(first, last-- + first - 1); // swap the root element with the last element
        f.accept(first); // invoke the function f so that it is ordered again
        quarternaryHeap[last + first] = null; // prevent loitering
        return result;
    }

    void sink(@SuppressWarnings("SameParameterValue") int k) {
        doHeapify(k, (a, b) -> !unordered(a, b));
    }

    void snake(@SuppressWarnings("SameParameterValue") int k) {
        swimUp(doHeapify(k, (a, b) -> false));
    }

    void swimUp(int k) {
        int i = k;
        while (i > first && unordered(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    boolean unordered(int i, int j) {
        return (comparator.compare(quarternaryHeap[i], quarternaryHeap[j]) > 0) ^ max;
    }

    public Iterator<K> iterator() {
        Collection<K> copy = new ArrayList<>(Arrays.asList(Arrays.copyOf(quarternaryHeap, last + first)));
        Iterator<K> result = copy.iterator();
        if (first > 0) result.next();
        return result;
    }

    private int doHeapify(int k, BiPredicate<Integer, Integer> p) {
        int i = k;
        while (firstChild(i) <= last + first - 1) {
            int minOrMaxChild = firstChild(i);            
            for (int j=minOrMaxChild+1; j<=minOrMaxChild+3; j++) {
                if (j>last+first-1) break;
                else if (unordered(minOrMaxChild, j)) {
                    minOrMaxChild = j;
                }
            }
            if (p.test(i, minOrMaxChild)) break;
            swap(i, minOrMaxChild);
            i = minOrMaxChild;
        }
        return i;
    }

    private void swap(int i, int j) {
        K tmp = quarternaryHeap[i];
        quarternaryHeap[i] = quarternaryHeap[j];
        quarternaryHeap[j] = tmp;
    }

    private int parent(int k) {
        return (k - 1 - first) / 4 + first;
    }

    private int firstChild(int k) {
        return (k + 1 - first) * 4 + first - 3;
    }

    @SuppressWarnings("unused")
    private K peek(int k) {
        return quarternaryHeap[k];
    }

    @SuppressWarnings("unused")
    private boolean getMax() {
        return max;
    }

    private final boolean max;
    private final int first;
    private final Comparator<K> comparator;
    private final K[] quarternaryHeap; // quarternaryHeap[i] is ith element of quarternary heap (first element is reserved)
    private int last; // number of elements in the quarternary heap
    private final boolean floyd; //Determine whether floyd's snake method is on or off inside the take method
 
 }