package com.serliunx.stc4j.collection;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * {@link DefaultRandomObjectPool} 单元测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/20
 */
public class DefaultRandomObjectPoolTest {

    @Test
    public void testGetReturnsElementAtIndexProvidedByRandom() {
        RandomObjectPool<String> pool = new DefaultRandomObjectPool<>();
        pool.addAll(Arrays.asList("A", "B", "C"));

        assertEquals("C", pool.get(new FixedRandom(2)));
        assertEquals("A", pool.get(new FixedRandom(0)));
    }

    @Test
    public void testGetWithoutRandomReturnsExistingElement() {
        RandomObjectPool<String> pool = new DefaultRandomObjectPool<>();
        pool.add("only");

        assertEquals("only", pool.get());
    }

    @Test
    public void testGetRejectsNullRandomAndEmptyPool() {
        RandomObjectPool<String> pool = new DefaultRandomObjectPool<>();

        assertThrows(IllegalArgumentException.class, () -> pool.get(null));
        assertThrows(IllegalArgumentException.class, () -> pool.get(new Random(1)));
    }

    @Test
    public void testCollectionOperationsDelegateToUnderlyingStorage() {
        RandomObjectPool<String> pool = new DefaultRandomObjectPool<>();

        assertTrue(pool.isEmpty());
        assertTrue(pool.add("A"));
        assertTrue(pool.addAll(Arrays.asList("B", "C")));
        assertEquals(3, pool.size());
        assertTrue(pool.contains("B"));
        assertTrue(pool.containsAll(Arrays.asList("A", "C")));
        assertArrayEquals(new Object[]{"A", "B", "C"}, pool.toArray());

        assertTrue(pool.remove("B"));
        assertFalse(pool.contains("B"));
        assertTrue(pool.retainAll(Arrays.asList("A", "X")));
        assertEquals(Collections.singletonList("A"), Arrays.asList(pool.toArray(new String[0])));
        assertTrue(pool.removeAll(Collections.singleton("A")));
        assertTrue(pool.isEmpty());
    }

    @Test
    public void testDuplicateElementsAreTreatedAsIndependentEntries() {
        RandomObjectPool<String> pool = new DefaultRandomObjectPool<>();
        pool.addAll(Arrays.asList("A", "A", "B"));

        assertEquals("A", pool.get(new FixedRandom(0)));
        assertEquals("A", pool.get(new FixedRandom(1)));
        assertEquals("B", pool.get(new FixedRandom(2)));
        assertEquals(3, pool.size());
    }

    @Test
    public void testRandomDistributionReflectsDuplicateEntryCounts() {
        RandomObjectPool<String> pool = new DefaultRandomObjectPool<>();
        pool.addAll(Arrays.asList("A", "A", "B"));

        Map<String, Integer> counts = new HashMap<>();
        Random random = new Random(12345L);
        int iterations = 12000;
        for (int i = 0; i < iterations; i++) {
            String value = pool.get(random);
            counts.merge(value, 1, Integer::sum);
        }

        int aCount = counts.getOrDefault("A", 0);
        int bCount = counts.getOrDefault("B", 0);

        assertTrue(aCount > 7200);
        assertTrue(aCount < 8800);
        assertTrue(bCount > 3200);
        assertTrue(bCount < 4800);
    }

    @Test
    public void testClearEmptiesPool() {
        RandomObjectPool<String> pool = new DefaultRandomObjectPool<>();
        pool.addAll(Arrays.asList("A", "B"));

        pool.clear();

        assertTrue(pool.isEmpty());
        assertEquals(0, pool.size());
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
