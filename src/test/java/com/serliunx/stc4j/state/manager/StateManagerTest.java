package com.serliunx.stc4j.state.manager;

import com.serliunx.stc4j.state.exception.StateException;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Tests for state managers.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/16
 */
public class StateManagerTest {

    @Test
    public void testReserveKeepsCurrentStateAndResetTargetStableForStandardStateManager() {
        StandardStateManager<String> manager = new StandardStateManager<>(new String[]{"A", "B", "C", "D"});

        assertTrue(manager.switchTo("C"));
        assertEquals("C", manager.current());

        manager.reserve();

        assertEquals("C", manager.current());

        manager.reset();

        assertEquals("A", manager.current());
    }

    @Test
    public void testReservePreservesOriginalDefaultForUnidirectionalManager() {
        DefaultUnidirectionalStateManager<String> manager =
                new DefaultUnidirectionalStateManager<>(new String[]{"A", "B", "C"});

        assertTrue(manager.switchTo("B"));
        manager.reserve();

        assertEquals("B", manager.current());
        assertTrue(manager.switchTo("A"));
        assertEquals("A", manager.current());
        assertFalse(manager.switchTo("C"));
    }

    @Test
    public void testStandardStateManagerBasicOperationsAndHelpers() {
        StandardStateManager<String> manager = new StandardStateManager<>(new String[]{"A", "B", "C"});
        AtomicInteger counter = new AtomicInteger();

        assertEquals("A", manager.current());
        assertEquals(3, manager.size());
        assertTrue(manager.isSwitchable());
        assertTrue(manager.is("A"));

        manager.computeIfMatch("A", counter::incrementAndGet);
        manager.computeIfMatch("B", counter::incrementAndGet);

        assertEquals(1, counter.get());
        assertTrue(manager.switchToIfMatch("A", "C"));
        assertEquals("C", manager.current());
        assertFalse(manager.switchToIfMatch("A", "B"));
        assertFalse(manager.switchTo("missing"));
        assertFalse(manager.switchTo("C"));

        manager.reset();

        assertEquals("A", manager.current());
    }

    @Test
    public void testDefaultUnidirectionalStateManagerSupportsForwardSwitchingAndWrapToDefault() {
        DefaultUnidirectionalStateManager<String> manager =
                new DefaultUnidirectionalStateManager<>(new String[]{"A", "B", "C"});

        assertEquals("A", manager.getAndSwitchNext());
        assertEquals("B", manager.current());
        assertEquals("C", manager.switchNextAndGet());
        assertEquals("C", manager.current());
        assertFalse(manager.switchTo("B"));
        assertTrue(manager.switchTo("A"));
        assertEquals("A", manager.current());

        manager.switchNext();

        assertEquals("B", manager.current());
    }

    @Test
    public void testDefaultBidirectionalStateManagerSupportsBackwardOperations() {
        DefaultBidirectionalStateManager<String> manager =
                new DefaultBidirectionalStateManager<>(new String[]{"A", "B", "C"});

        assertEquals("A", manager.getAndSwitchPrev());
        assertEquals("C", manager.current());
        assertEquals("B", manager.switchPrevAndGet());
        assertEquals("B", manager.current());
        assertTrue(manager.switchTo("A"));
        assertEquals("A", manager.current());
    }

    @Test
    public void testBreakageUnidirectionalStateManagerThrowsAtLastStateWhenEnabled() {
        BreakageUnidirectionalStateManager<String> manager =
                new BreakageUnidirectionalStateManager<>(new String[]{"A", "B"}, true);

        manager.switchNext();

        assertEquals("B", manager.current());
        assertFalse(manager.isSwitchable());
        assertFalse(manager.isCircle());
        assertThrows(StateException.class, manager::switchNext);
        assertThrows(StateException.class, manager::getAndSwitchNext);
        assertThrows(StateException.class, manager::switchNextAndGet);
        assertThrows(StateException.class, () -> manager.switchTo("A"));
        assertThrows(UnsupportedOperationException.class, manager::reset);
    }

    @Test
    public void testBreakageUnidirectionalStateManagerReturnsGracefullyWhenThrowDisabled() {
        BreakageUnidirectionalStateManager<String> manager =
                new BreakageUnidirectionalStateManager<>(new String[]{"A", "B"}, false);

        manager.switchNext();

        assertEquals("B", manager.current());
        manager.switchNext();
        assertEquals("B", manager.current());
        assertNull(manager.getAndSwitchNext());
        assertNull(manager.switchNextAndGet());
        assertFalse(manager.switchTo("A"));
        assertEquals("B", manager.current());
    }
}
