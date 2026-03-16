package com.serliunx.stc4j.state.support;

import com.serliunx.stc4j.state.machine.ConcurrentStateMachine;
import com.serliunx.stc4j.state.machine.StateMachine;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 单元测试 {@link StateMachines}.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/16
 */
public class StateMachinesTest {

    @Test
    public void testDefaultStateMachineFactoryBuildsUsableMachine() throws Exception {
        StateMachine<String> machine = StateMachines.defaultStateMachine(new String[]{"A", "B"});

        try {
            assertEquals("A", machine.current());
            machine.switchPrev(false);
            assertEquals("B", machine.current());
        } finally {
            machine.close();
        }
    }

    @Test
    public void testConcurrentStateMachineFactoryBuildsUsableMachine() throws Exception {
        ConcurrentStateMachine<String> machine = StateMachines.concurrentStateMachine(Arrays.asList("A", "B"));

        try {
            assertEquals("A", machine.current());
            assertTrue(machine.compareAndSet("A", "B", false));
            assertEquals("B", machine.current());
        } finally {
            machine.close();
        }
    }
}
