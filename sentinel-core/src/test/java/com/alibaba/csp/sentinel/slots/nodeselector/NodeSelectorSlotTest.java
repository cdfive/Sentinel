/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.slots.nodeselector;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.CtEntryTestUtil;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextTestUtil;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.EntranceNode;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slotchain.StringResourceWrapper;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author jialiang.linjl
 * @author Eric Zhao
 * @author cdfive
 */
public class NodeSelectorSlotTest {

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
        NodeSelectorSlot slot = mock(NodeSelectorSlot.class);
        Whitebox.setInternalState(slot, "map", new HashMap<>());

        Context context = ContextUtil.enter("serviceA");
        ResourceWrapper resourceWrapper = new StringResourceWrapper("nodeA", EntryType.IN);

        Entry entry = mock(Entry.class);
        context.setCurEntry(entry);
        when(entry.getCurNode()).thenReturn(null);

        doCallRealMethod().when(slot).entry(context, resourceWrapper, null, 1, false);
        slot.entry(context, resourceWrapper, null, 1, false);

        verify(slot).entry(context, resourceWrapper, null, 1, false);
        // Verify fireEntry method has been called only once
        // Use matchers here since the third parameter is a new defaultNode NodeSelectorSlot created
        verify(slot).fireEntry(eq(context), eq(resourceWrapper), any(), eq(1), eq(false));
        verifyNoMoreInteractions(slot);
    }

    @Test
    public void testEntry() throws Throwable {
        NodeSelectorSlot slot = new NodeSelectorSlot();

        Context context = ContextUtil.enter("serviceA");
        ResourceWrapper resourceWrapper = new StringResourceWrapper("nodeA", EntryType.IN);
        // Set curEntry for context
        CtEntryTestUtil.buildCtEntry(resourceWrapper, null, context);

        assertNull(context.getCurNode());
        assertEquals(0, ((DefaultNode) context.getLastNode()).getChildList().size());

        slot.entry(context, resourceWrapper, null, 1, false);

        assertNotNull(context.getCurNode());
        assertEquals(1, ((DefaultNode) context.getLastNode()).getChildList().size());
    }

    @Test
    public void testSingleEntrance() throws Exception {
        final String contextName = "entry_SingleEntrance";
        ContextUtil.enter(contextName);

        EntranceNode entranceNode = null;
        for (Node node : Constants.ROOT.getChildList()) {
            entranceNode = (EntranceNode)node;
            if (entranceNode.getId().getName().equals(contextName)) {
                break;
            } else {
                System.out.println("Single entry: " + entranceNode.getId().getName());
            }
        }
        assertNotNull(entranceNode);
        assertTrue(entranceNode.getId().getName().equalsIgnoreCase(contextName));
        final String resName = "nodeA";
        Entry nodeA = SphU.entry(resName);

        assertNotNull(ContextUtil.getContext().getCurNode());
        assertEquals(resName, ((DefaultNode)ContextUtil.getContext().getCurNode()).getId().getName());
        boolean hasNode = false;
        for (Node node : entranceNode.getChildList()) {
            if (((DefaultNode)node).getId().getName().equals(resName)) {
                hasNode = true;
            }
        }
        assertTrue(hasNode);

        if (nodeA != null) {
            nodeA.exit();
        }
        ContextUtil.exit();
    }

    @Test
    public void testMultipleEntrance() throws Exception {
        final String firstEntry = "entry_multiple_one";
        final String anotherEntry = "entry_multiple_another";
        final String resName = "nodeA";

        Node firstNode, anotherNode;
        ContextUtil.enter(firstEntry);
        Entry nodeA = SphU.entry(resName);
        firstNode = ContextUtil.getContext().getCurNode();
        if (nodeA != null) {
            nodeA.exit();
        }
        ContextUtil.exit();

        ContextUtil.enter(anotherEntry);
        nodeA = SphU.entry(resName);
        anotherNode = ContextUtil.getContext().getCurNode();
        if (nodeA != null) {
            nodeA.exit();
        }

        assertNotSame(firstNode, anotherNode);

        for (Node node : Constants.ROOT.getChildList()) {
            EntranceNode firstEntrance = (EntranceNode)node;
            if (firstEntrance.getId().getName().equals(firstEntry)) {
                assertEquals(1, firstEntrance.getChildList().size());
                for (Node child : firstEntrance.getChildList()) {
                    assertEquals(resName, ((DefaultNode)child).getId().getName());
                }
            } else if (firstEntrance.getId().getName().equals(anotherEntry)) {
                assertEquals(1, firstEntrance.getChildList().size());
                for (Node child : firstEntrance.getChildList()) {
                    assertEquals(resName, ((DefaultNode)child).getId().getName());
                }
            } else {
                System.out.println("Multiple entries: " + firstEntrance.getId().getName());
            }
        }
        ContextUtil.exit();
    }

    //@Test
    public void testMultipleLayer() throws Exception {
        // TODO: fix this
        ContextUtil.enter("entry1", "appA");

        Entry nodeA = SphU.entry("nodeA");
        assertSame(ContextUtil.getContext().getCurEntry(), nodeA);

        DefaultNode dnA = (DefaultNode)nodeA.getCurNode();
        assertNotNull(dnA);
        assertSame("nodeA", dnA.getId().getName());

        Entry nodeB = SphU.entry("nodeB");
        assertSame(ContextUtil.getContext().getCurEntry(), nodeB);
        DefaultNode dnB = (DefaultNode)nodeB.getCurNode();
        assertNotNull(dnB);
        assertTrue(dnA.getChildList().contains(dnB));

        Entry nodeC = SphU.entry("nodeC");
        assertSame(ContextUtil.getContext().getCurEntry(), nodeC);
        DefaultNode dnC = (DefaultNode)nodeC.getCurNode();
        assertNotNull(dnC);
        assertTrue(dnB.getChildList().contains(dnC));

        if (nodeC != null) {
            nodeC.exit();
        }
        assertSame(ContextUtil.getContext().getCurEntry(), nodeB);

        if (nodeB != null) {
            nodeB.exit();
        }
        assertSame(ContextUtil.getContext().getCurEntry(), nodeA);

        if (nodeA != null) {
            nodeA.exit();
        }
        assertNull(ContextUtil.getContext().getCurEntry());
        ContextUtil.exit();

    }

}
