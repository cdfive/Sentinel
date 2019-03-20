package com.alibaba.csp.sentinel.slots.block.system;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemSlot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

/**
 * @author cdfive
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SystemRuleManager.class)
public class SystemSlotTest {

    @Test
    public void testFireEntry() throws Throwable {
        SystemSlot slot = mock(SystemSlot.class);

        Context context = mock(Context.class);
        ResourceWrapper resourceWrapper = mock(ResourceWrapper.class);
        DefaultNode node = mock(DefaultNode.class);

        doCallRealMethod().when(slot).entry(context, resourceWrapper, node, 1, false);
        slot.entry(context, resourceWrapper, node, 1, false);

        verify(slot).entry(context, resourceWrapper, node, 1, false);
        // Verify fireEntry method has been called, and only once
        verify(slot).fireEntry(context, resourceWrapper, node, 1, false);
        verifyNoMoreInteractions(slot);
    }

    @Test
    public void testFireExit() throws Throwable {
        SystemSlot slot = mock(SystemSlot.class);

        Context context = mock(Context.class);
        ResourceWrapper resourceWrapper = mock(ResourceWrapper.class);

        doCallRealMethod().when(slot).exit(context, resourceWrapper, 1);
        slot.exit(context, resourceWrapper, 1);

        verify(slot).exit(context, resourceWrapper, 1);
        // Verify fireExit method has been called, and only once
        verify(slot).fireExit(context, resourceWrapper, 1);
        verifyNoMoreInteractions(slot);
    }

    @Test
    public void testEntry() throws Throwable {
        PowerMockito.mockStatic(SystemRuleManager.class);

        SystemSlot slot = mock(SystemSlot.class);

        Context context = mock(Context.class);
        ResourceWrapper resourceWrapper = mock(ResourceWrapper.class);
        DefaultNode node = mock(DefaultNode.class);

        doCallRealMethod().when(slot).entry(context, resourceWrapper, node, 1, false);
        slot.entry(context, resourceWrapper, node, 1, false);

        // Verify SystemRuleManager.checkSystem(...) method has been called, and only once
        PowerMockito.verifyStatic(SystemRuleManager.class);
        SystemRuleManager.checkSystem(resourceWrapper);
        PowerMockito.verifyNoMoreInteractions(SystemRuleManager.class);
    }
}
