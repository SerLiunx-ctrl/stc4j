package com.serliunx.stc4j.state.machine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * 状态机压力测试 by Codex
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/17
 */
public class StateMachineStressTest {

    @Test
    public void testConcurrentReserveAndResetKeepInitialStateStable() throws Exception {

        try (ConcurrentStateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B", "C", "D"})
                .withInitial("C")
                .async(false)
                .concurrent()
                .build()) {
            runConcurrently(Arrays.asList(
                    () -> repeat(400, machine::reserve),
                    () -> repeat(400, machine::reserve),
                    () -> repeat(300, () -> {
                        machine.reset(false);
                        assertEquals("C", machine.current());
                    }),
                    () -> repeat(300, () -> {
                        machine.reset(false);
                        assertEquals("C", machine.current());
                    })
            ));
        }
    }

    @Test
    public void testConcurrentReserveAndCompareAndSetKeepTargetStateStable() throws Exception {

        try (ConcurrentStateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B", "C", "D"})
                .withInitial("A")
                .async(false)
                .concurrent()
                .build()) {
            runConcurrently(Arrays.asList(
                    () -> repeat(500, machine::reserve),
                    () -> repeat(500, machine::reserve),
                    () -> repeat(400, () -> {
                        while (true) {
                            String current = machine.current();
                            String target = nextByValue(current);
                            if (machine.compareAndSet(current, target, false)) {
                                assertEquals(target, machine.current());
                                break;
                            }
                        }
                    })
            ));
        }
    }

    @Test
    public void testConcurrentReserveAndSwitchNextKeepReturnedStateStable() throws Exception {

        try (ConcurrentStateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B", "C", "D"})
                .withInitial("B")
                .async(false)
                .concurrent()
                .build()) {
            runConcurrently(Arrays.asList(
                    () -> repeat(500, machine::reserve),
                    () -> repeat(500, machine::reserve),
                    () -> repeat(400, () -> {
                        String next = machine.switchNextAndGet(false);
                        assertEquals(next, machine.current());
                        String previous = machine.switchPrevAndGet(false);
                        assertEquals(previous, machine.current());
                    })
            ));
        }
    }

    private static String nextByValue(String state) {
        switch (state) {
            case "A":
                return "B";
            case "B":
                return "C";
            case "C":
                return "D";
            case "D":
                return "A";
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    private static void repeat(int times, CheckedRunnable runnable) throws Exception {
        for (int i = 0; i < times; i++) {
            runnable.run();
        }
    }

    private static void runConcurrently(List<CheckedRunnable> tasks) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>(tasks.size());
        try {
            for (CheckedRunnable task : tasks) {
                futures.add(executor.submit(() -> {
                    if (!start.await(3, TimeUnit.SECONDS)) {
                        throw new AssertionError("Timed out waiting for test start");
                    }
                    task.run();
                    return null;
                }));
            }

            start.countDown();

            for (Future<?> future : futures) {
                future.get(20, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }
}
