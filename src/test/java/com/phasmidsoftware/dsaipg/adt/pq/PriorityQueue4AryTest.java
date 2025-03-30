package com.phasmidsoftware.dsaipg.adt.pq;

import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import org.junit.Test;

import java.util.Comparator;
import java.util.Iterator;

import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class PriorityQueue4AryTest {

    @Test
    public void testUnordered1() {
        String[] quarternaryHeap = new String[3];
        quarternaryHeap[1] = "A";
        quarternaryHeap[2] = "B";
        boolean max = false;
        Iterable<String> pq = new PriorityQueue4Ary<>(max, quarternaryHeap, 1, 2, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(max, tester.invokePrivate("unordered", 1, 2));
    }

    @Test
    public void testUnordered2() {
        String[] quarternaryHeap = new String[3];
        quarternaryHeap[1] = "A";
        quarternaryHeap[2] = "B";
        boolean max = true;
        Iterable<String> pq = new PriorityQueue4Ary<>(max, quarternaryHeap, 1, 2, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(max, tester.invokePrivate("unordered", 1, 2));
    }

    @Test
    public void testSwimUp0() {
        String[] quarternaryHeap = new String[3];
        String a = "A";
        String b = "B";
        quarternaryHeap[0] = a;
        quarternaryHeap[1] = b;
        // Create PQ which uses the 0th index.
        Iterable<String> pq = new PriorityQueue4Ary<>(true, quarternaryHeap, 0, 2, Comparator.comparing(String::toString), true);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(a, tester.invokePrivate("peek", 0));
        tester.invokePrivate("swimUp", 1);
        assertEquals(b, tester.invokePrivate("peek", 0));
    }

    @Test
    public void testSwimUp1() {
        String[] quarternaryHeap = new String[3];
        String a = "A";
        String b = "B";
        quarternaryHeap[1] = a;
        quarternaryHeap[2] = b;
        // Create PQ which does not use the 0th index.
        Iterable<String> pq = new PriorityQueue4Ary<>(true, quarternaryHeap, 1, 2, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(a, tester.invokePrivate("peek", 1));
        tester.invokePrivate("swimUp", 2);
        assertEquals(b, tester.invokePrivate("peek", 1));
    }

    @Test
    public void testSwimUp3() {
        String[] quarternaryHeap = new String[5];
        quarternaryHeap[1] = "D";
        quarternaryHeap[2] = "C";
        quarternaryHeap[3] = "E";
        quarternaryHeap[4] = "B";
        // Create PQ as a min-heap.
        Iterable<String> pq = new PriorityQueue4Ary<>(false, quarternaryHeap, 1, 4, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        tester.invokePrivate("swimUp", 4); // Swim "B" upward.
        assertEquals("B", tester.invokePrivate("peek", 1)); // Peek at root.
    }

    @Test
    public void testSink0() {
        String[] quarternaryHeap = new String[4];
        String a = "A";
        String b = "B";
        String c = "C";
        quarternaryHeap[0] = a;
        quarternaryHeap[1] = b;
        quarternaryHeap[2] = c;
        Iterable<String> pq = new PriorityQueue4Ary<>(true, quarternaryHeap, 0, 3, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        tester.invokePrivate("sink", 0);
        assertEquals(c, tester.invokePrivate("peek", 0));
        assertEquals(a, tester.invokePrivate("peek", 2));
    }

    @Test
    public void testSink1() {
        String[] quarternaryHeap = new String[4];
        String a = "A";
        String b = "B";
        String c = "C";
        quarternaryHeap[1] = a;
        quarternaryHeap[2] = b;
        quarternaryHeap[3] = c;
        Iterable<String> pq = new PriorityQueue4Ary<>(true, quarternaryHeap, 1, 3, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        tester.invokePrivate("sink", 1);
        assertEquals(c, tester.invokePrivate("peek", 1));
        assertEquals(a, tester.invokePrivate("peek", 3));
    }

    @Test
    public void testGive1() {
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(10, Comparator.comparing(String::toString));
        String key = "A";
        pq.give(key);
        assertEquals(1, pq.size());
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(key, tester.invokePrivate("peek", 1));
    }

    @Test
    public void testGive2() {
        // Test that we can comfortably give more elements than the the PQ has capacity for
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(1, Comparator.comparing(String::toString));
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        String key = "A";
        pq.give(null); // This will never survive so it might as well be null
        assertEquals(1, pq.size());
        assertNull(tester.invokePrivate("peek", 1));
        pq.give(key);
        assertEquals(1, pq.size());
        assertEquals(key, tester.invokePrivate("peek", 1));
    }

    @Test
    public void testTake1() throws PQException {
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(10, Comparator.comparing(String::toString));
        String key = "A";
        pq.give(key);
        assertEquals(key, pq.take());
        assertTrue(pq.isEmpty());
    }

    @Test
    public void testTake2() throws PQException {
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(10, Comparator.comparing(String::toString));
        String a = "A";
        String b = "B";
        pq.give(a);
        pq.give(b);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(a, tester.invokePrivate("peek", 2));
        assertEquals(b, tester.invokePrivate("peek", 1));
        assertEquals(b, pq.take());
        assertEquals(a, pq.take());
        assertTrue(pq.isEmpty());

    }

    @Test(expected = PQException.class)
    public void testTake3() throws PQException {
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(10, Comparator.comparing(String::toString));
        pq.give("A");
        pq.take();
        pq.take();
    }

    @Test
    public void isEmpty() {
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(10, false, Comparator.comparing(String::toString));
        assertTrue(pq.isEmpty());
    }

    @Test
    public void size() throws PQException {
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(10, false, Comparator.comparing(String::toString));
        assertEquals(0, pq.size());
        pq.give("A");
        assertEquals(1, pq.size());
        pq.take();
        assertEquals(0, pq.size());
    }

    @Test
    public void doTake01() throws PQException {
        String[] quarternaryHeap = new String[3];
        quarternaryHeap[0] = "A";
        quarternaryHeap[1] = "B";
        quarternaryHeap[2] = "C";
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(false, quarternaryHeap, 0, 3, Comparator.comparing(String::toString), false);
        pq.doTake(pq::snake);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals("B", tester.invokePrivate("peek", 0));
    }

    @Test
    public void doTake02() throws PQException {
        String[] quarternaryHeap = new String[3];
        quarternaryHeap[0] = "C";
        quarternaryHeap[1] = "A";
        quarternaryHeap[2] = "B";
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(true, quarternaryHeap, 0, 3, Comparator.comparing(String::toString), false);
        pq.doTake(pq::sink);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals("B", tester.invokePrivate("peek", 0));
    }

    @Test
    public void doTake11() throws PQException {
        String[] quarternaryHeap = new String[4];
        quarternaryHeap[1] = "A";
        quarternaryHeap[2] = "B";
        quarternaryHeap[3] = "C";
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(false, quarternaryHeap, 1, 3, Comparator.comparing(String::toString), false);
        pq.doTake(pq::snake);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals("B", tester.invokePrivate("peek", 1));
    }

    @Test
    public void doTake12() throws PQException {
        String[] quarternaryHeap = new String[4];
        quarternaryHeap[1] = "C";
        quarternaryHeap[2] = "A";
        quarternaryHeap[3] = "B";
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(true, quarternaryHeap, 1, 3, Comparator.comparing(String::toString), false);
        pq.doTake(pq::sink);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals("B", tester.invokePrivate("peek", 1));
    }

    @Test
    public void iterator0() {
        String[] quarternaryHeap = new String[3];
        quarternaryHeap[0] = "C";
        quarternaryHeap[1] = "B";
        quarternaryHeap[2] = "D";
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(true, quarternaryHeap, 0, 3, Comparator.comparing(String::toString), false);
        assertEquals(3, pq.size());
        Iterator<String> iterator = pq.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(quarternaryHeap[0], iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(quarternaryHeap[1], iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(quarternaryHeap[2], iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(3, pq.size());
    }

    @Test
    public void iterator1() {
        String[] quarternaryHeap = new String[4];
        quarternaryHeap[1] = "C";
        quarternaryHeap[2] = "B";
        quarternaryHeap[3] = "D";
        PriorityQueue4Ary<String> pq = new PriorityQueue4Ary<>(true, quarternaryHeap, 1, 3, Comparator.comparing(String::toString), false);
        assertEquals(3, pq.size());
        Iterator<String> iterator = pq.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(quarternaryHeap[1], iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(quarternaryHeap[2], iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(quarternaryHeap[3], iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(3, pq.size());
    }

    @Test
    public void testGetMax() {
        Iterable<String> pq = new PriorityQueue4Ary<>(10, false, Comparator.comparing(String::toString));
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(false, tester.invokePrivate("getMax"));
    }
}