package com.alibaba.csp.sentinel.slots.statistic;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.Env;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextTestUtil;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slotchain.StringResourceWrapper;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author cdfive
 */
public class StatisticSlotTest {

    @Before
    public void setUp() {
        ClusterBuilderSlot.getClusterNodeMap().clear();
        ContextTestUtil.cleanUpContext();
    }

    @After
    public void cleanUp() {
        ClusterBuilderSlot.getClusterNodeMap().clear();
        ContextTestUtil.cleanUpContext();
    }

    @Test
    public void testFireEntry() throws Throwable {
        StatisticSlot slot = mock(StatisticSlot.class);

        Context context = ContextUtil.enter("serviceA");
        ResourceWrapper resourceWrapper = new StringResourceWrapper("nodeA", EntryType.IN);
        DefaultNode defaultNode = Env.nodeBuilder.buildTreeNode(resourceWrapper, Env.nodeBuilder.buildClusterNode());

        Entry entry = mock(Entry.class);
        context.setCurEntry(entry);
        when(entry.getCurNode()).thenReturn(defaultNode);

        doCallRealMethod().when(slot).entry(context, resourceWrapper, defaultNode, 1, false);
        slot.entry(context, resourceWrapper, defaultNode, 1, false);

        verify(slot).entry(context, resourceWrapper, defaultNode, 1, false);
        // Verify fireEntry method has been called only once
        verify(slot).fireEntry(context, resourceWrapper, defaultNode, 1, false);
        verifyNoMoreInteractions(slot);
    }
}
