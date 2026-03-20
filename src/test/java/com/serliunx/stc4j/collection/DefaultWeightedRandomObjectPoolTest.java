package com.serliunx.stc4j.collection;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * {@link DefaultWeightedRandomObjectPool} 单元测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/20
 */
public class DefaultWeightedRandomObjectPoolTest {

    @Test
    public void testGetReturnsObjectByWeightRange() {
        WeightedRandomObjectPool<String> pool = new DefaultWeightedRandomObjectPool<>();
        pool.add("A", 1);
        pool.add("B", 3);
        pool.add("C", 2);

        assertEquals("A", pool.get(new FixedRandom(0)));
        assertEquals("B", pool.get(new FixedRandom(1)));
        assertEquals("B", pool.get(new FixedRandom(3)));
        assertEquals("C", pool.get(new FixedRandom(4)));
        assertEquals("C", pool.get(new FixedRandom(5)));
    }

    @Test
    public void testDefaultAddUsesWeightOneAndTracksTotalWeight() {
        WeightedRandomObjectPool<String> pool = new DefaultWeightedRandomObjectPool<>();

        assertTrue(pool.add("A"));
        assertTrue(pool.add("A", 2));
        assertTrue(pool.add("B"));

        assertEquals(3, pool.size());
        assertEquals(3, pool.getWeight("A"));
        assertEquals(1, pool.getWeight("B"));
        assertEquals(4, pool.totalWeight());
    }

    @Test
    public void testGetRejectsInvalidInputAndEmptyPool() {
        WeightedRandomObjectPool<String> pool = new DefaultWeightedRandomObjectPool<>();

        assertThrows(IllegalArgumentException.class, () -> pool.add("A", 0));
        assertThrows(IllegalArgumentException.class, () -> pool.get(null));
        assertThrows(IllegalArgumentException.class, () -> pool.get(new Random(1)));
    }

    @Test
    public void testCollectionOperationsSynchronizeWeights() {
        WeightedRandomObjectPool<String> pool = new DefaultWeightedRandomObjectPool<>();
        pool.add("A", 1);
        pool.add("B", 2);
        pool.add("C", 3);

        assertTrue(pool.containsAll(Arrays.asList("A", "B")));
        assertArrayEquals(new Object[]{"A", "B", "C"}, pool.toArray());

        assertTrue(pool.remove("B"));
        assertEquals(4, pool.totalWeight());
        assertEquals(0, pool.getWeight("B"));

        assertTrue(pool.addAll(Arrays.asList("D", "E")));
        assertEquals(6, pool.totalWeight());

        assertTrue(pool.retainAll(Arrays.asList("A", "D")));
        assertEquals(2, pool.totalWeight());
        assertEquals(Arrays.asList("A", "D"), Arrays.asList(pool.toArray(new String[0])));

        assertTrue(pool.removeAll(Collections.singleton("A")));
        assertEquals(1, pool.totalWeight());
        assertEquals(1, pool.size());
    }

    @Test
    public void testIteratorRemoveUpdatesTotalWeight() {
        WeightedRandomObjectPool<String> pool = new DefaultWeightedRandomObjectPool<>();
        pool.add("A", 2);
        pool.add("B", 3);

        Iterator<String> iterator = pool.iterator();
        assertEquals("A", iterator.next());
        iterator.remove();

        assertEquals(1, pool.size());
        assertEquals(3, pool.totalWeight());
        assertFalse(pool.contains("A"));
        assertTrue(pool.contains("B"));
    }

    @Test
    public void testDuplicateElementsAccumulateWeightAcrossEntries() {
        WeightedRandomObjectPool<String> pool = new DefaultWeightedRandomObjectPool<>();
        pool.add("A", 1);
        pool.add("A", 4);
        pool.add("B", 2);

        assertEquals("A", pool.get(new FixedRandom(0)));
        assertEquals("A", pool.get(new FixedRandom(4)));
        assertEquals("B", pool.get(new FixedRandom(5)));
        assertEquals(5, pool.getWeight("A"));
        assertEquals(7, pool.totalWeight());
    }

    @Test
    public void testRandomDistributionReflectsWeights() {
        WeightedRandomObjectPool<String> pool = new DefaultWeightedRandomObjectPool<>();
        pool.add("A", 1);
        pool.add("B", 3);
        pool.add("C", 6);

        Map<String, Integer> counts = new HashMap<>();
        Random random = new Random(67890L);
        int iterations = 20000;
        for (int i = 0; i < iterations; i++) {
            String value = pool.get(random);
            counts.merge(value, 1, Integer::sum);
        }

        int aCount = counts.getOrDefault("A", 0);
        int bCount = counts.getOrDefault("B", 0);
        int cCount = counts.getOrDefault("C", 0);

        assertTrue(aCount > 1400);
        assertTrue(aCount < 2600);
        assertTrue(bCount > 4800);
        assertTrue(bCount < 7200);
        assertTrue(cCount > 9800);
        assertTrue(cCount < 12200);
    }

    private static final class FixedRandom extends Random {

        private final int value;

        private FixedRandom(int value) {
            this.value = value;
        }

        @Override
        public int nextInt(int bound) {
            return value;
        }
    }
}
