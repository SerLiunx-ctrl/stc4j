package com.serliunx.stc4j.state.machine;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 普通状态机单元测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/16
 */
public class StateMachineEventTest {

    @Test
    public void testStateMachineInvokesLeaveEntryAndExchangeHandlersInOrder() throws Exception {
        List<String> invocations = new CopyOnWriteArrayList<>();
        StateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B"})
                .async(false)
                .whenLeave("A", params -> invocations.add("leave:" + params.getFrom() + "->" + params.getTo()))
                .whenEntry("B", params -> invocations.add("entry:" + params.getFrom() + "->" + params.getTo()))
                .exchange("A", "B", params -> invocations.add("exchange:" + params.getFrom() + "->" + params.getTo()))
                .build();

        try {
            assertTrue(machine.switchTo("B"));
            assertEquals(Arrays.asList("leave:A->B", "entry:A->B", "exchange:A->B"), invocations);
        } finally {
            machine.close();
        }
    }

    @Test
    public void testPublishRunsRegisteredConsumerSynchronouslyWhenAsyncDisabled() throws Exception {
        StateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B"})
                .async(false)
                .whenHappened("go", sm -> sm.switchTo("B", false))
                .build();

        try {
            machine.publish("go");
            assertEquals("B", machine.current());
        } finally {
            machine.close();
        }
    }

    @Test
    public void testPublishRunsRegisteredConsumerAsynchronouslyWhenAsyncEnabled() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "publish-executor"));
        CountDownLatch latch = new CountDownLatch(1);
        List<String> threads = new CopyOnWriteArrayList<>();
        StateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B"})
                .executor(executor)
                .async(true)
                .whenHappened("go", sm -> {
                    threads.add(Thread.currentThread().getName());
                    sm.switchTo("B", false);
                    latch.countDown();
                })
                .build();

        try {
            machine.publish("go");
            assertTrue(latch.await(3, TimeUnit.SECONDS));
            assertEquals("B", machine.current());
            assertEquals(1, threads.size());
            assertTrue(threads.get(0).startsWith("publish-executor"));
        } finally {
            machine.close();
        }
    }

    @Test
    public void testHandlerLevelAsyncOverridesGlobalSyncConfiguration() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "handler-executor"));
        CountDownLatch latch = new CountDownLatch(1);
        List<String> threads = new CopyOnWriteArrayList<>();
        StateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B"})
                .async(false)
                .whenEntry("B", params -> {
                    threads.add(Thread.currentThread().getName());
                    latch.countDown();
                }, true, executor)
                .build();

        try {
            assertTrue(machine.switchTo("B"));
            assertTrue(latch.await(3, TimeUnit.SECONDS));
            assertEquals(1, threads.size());
            assertTrue(threads.get(0).startsWith("handler-executor"));
        } finally {
            machine.close();
        }
    }

    @Test
    public void testCompareAndSetUpdatesConcurrentStateMachineAndTriggersHandlers() throws Exception {
        List<String> invocations = new CopyOnWriteArrayList<>();
        ConcurrentStateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B", "C"})
                .async(false)
                .concurrent()
                .whenEntry("B", params -> invocations.add(params.getFrom() + "->" + params.getTo()))
                .build();

        try {
            assertTrue(machine.compareAndSet("A", "B"));
            assertEquals("B", machine.current());
            assertFalse(machine.compareAndSet("A", "C"));
            assertEquals(Arrays.asList("A->B"), invocations);
        } finally {
            machine.close();
        }
    }

    @Test
    public void testConcurrentStateMachineReserveKeepsCurrentStateAndResetTargetStable() throws Exception {
        ConcurrentStateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B", "C"})
                .withInitial("B")
                .async(false)
                .concurrent()
                .build();

        try {
            assertEquals("B", machine.current());
            assertTrue(machine.switchTo("C", false));
            assertEquals("C", machine.current());

            machine.reserve();

            assertEquals("C", machine.current());
            assertTrue(machine.compareAndSet("C", "A", false));
            assertEquals("A", machine.current());

            machine.reset(false);

            assertEquals("B", machine.current());
        } finally {
            machine.close();
        }
    }

    @Test
    public void testCloseShutsDownProvidedExecutorService() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        StateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B"})
                .executor(executor)
                .async(true)
                .build();

        machine.close();

        assertTrue(executor.isShutdown());
    }
}
