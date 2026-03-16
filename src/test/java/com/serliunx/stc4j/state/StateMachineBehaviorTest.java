package com.serliunx.stc4j.state;

import com.serliunx.stc4j.state.handler.StateHandlerWrapper;
import com.serliunx.stc4j.state.machine.DefaultConcurrentStateMachine;
import com.serliunx.stc4j.state.machine.StateMachine;
import com.serliunx.stc4j.state.machine.StateMachineBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 状态机测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/16
 */
public class StateMachineBehaviorTest {

    @Test
    public void testStandardMachineResetReturnsToInitialState() throws Exception {

        try (StateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B", "C"})
                .withInitial("B")
                .async(false)
                .build()) {
            assertEquals("B", machine.current());
            machine.switchNext(false);
            assertEquals("C", machine.current());

            machine.reset(false);

            assertEquals("B", machine.current());
        }
    }

    @Test
    public void testConcurrentMachineResetReturnsToInitialState() throws Exception {

        try (StateMachine<String> machine = StateMachineBuilder.from(new String[]{"A", "B", "C"})
                .withInitial("B")
                .async(false)
                .concurrent()
                .build()) {
            assertEquals("B", machine.current());
            machine.switchNext(false);
            assertEquals("C", machine.current());

            machine.reset(false);

            assertEquals("B", machine.current());
        }
    }

    @Test
    public void testExchangeHandlersUseTypedTransitionKey() throws Exception {
        NamedState s1 = new NamedState(1);
        NamedState s2 = new NamedState(2);
        NamedState s3 = new NamedState(3);
        NamedState s4 = new NamedState(4);
        List<String> invocations = new ArrayList<>();

        try (StateMachine<NamedState> machine = StateMachineBuilder.from(Arrays.asList(s1, s2, s3, s4))
                .withInitial(s1)
                .async(false)
                .exchange(s1, s2, params -> invocations.add("12"))
                .exchange(s3, s4, params -> invocations.add("34"))
                .build()) {
            assertTrue(machine.switchTo(s2));
            assertEquals(Collections.singletonList("12"), invocations);
        }
    }

    @Test
    public void testConcurrentSwitchHandlersObserveActualTransition() throws Exception {
        List<String> transitions = new CopyOnWriteArrayList<>();
        Map<String, List<StateHandlerWrapper<String>>> entryHandlers = new HashMap<>();
        entryHandlers.put("B", Collections.singletonList(new StateHandlerWrapper<>(
                params -> transitions.add(params.getFrom() + "->" + params.getTo()), null, false)));
        entryHandlers.put("C", Collections.singletonList(new StateHandlerWrapper<>(
                params -> transitions.add(params.getFrom() + "->" + params.getTo()), null, false)));

        BlockingConcurrentStateMachine<String> machine = new BlockingConcurrentStateMachine<>(
                Arrays.asList("A", "B", "C"),
                entryHandlers,
                "A",
                "C"
        );

        try {
            Thread thread = new Thread(() -> machine.switchTo("C"));
            thread.start();

            assertTrue(machine.awaitBlocked());
            assertTrue(machine.switchTo("B"));

            machine.releaseBlocked();
            thread.join(3000);

            assertEquals(Arrays.asList("A->B", "B->C"), transitions);
            assertEquals("C", machine.current());
        } finally {
            machine.releaseBlocked();
            machine.close();
        }
    }

    private static final class BlockingConcurrentStateMachine<S> extends DefaultConcurrentStateMachine<S> {

        private final S blockedTarget;
        private final CountDownLatch blocked = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);
        private volatile boolean blockOnce = true;

        private BlockingConcurrentStateMachine(List<S> stateList,
                                               Map<S, List<StateHandlerWrapper<S>>> entryHandlers,
                                               S initialState,
                                               S blockedTarget) {
            super(stateList,
                    entryHandlers,
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    null,
                    false,
                    initialState);
            this.blockedTarget = blockedTarget;
        }

        @Override
        protected Transition exchangeToTarget(int target) {
            if (blockOnce && Objects.equals(get(target), blockedTarget)) {
                blockOnce = false;
                blocked.countDown();
                try {
                    release.await(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
            }
            return super.exchangeToTarget(target);
        }

        private boolean awaitBlocked() throws InterruptedException {
            return blocked.await(3, TimeUnit.SECONDS);
        }

        private void releaseBlocked() {
            release.countDown();
        }
    }

    private static final class NamedState {

        private final int id;

        private NamedState(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NamedState)) {
                return false;
            }
            return id == ((NamedState) obj).id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "STATE";
        }
    }
}
