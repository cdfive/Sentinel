package com.alibaba.csp.sentinel.context;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author cdfive
 */
public class ContextUtilTest {

    @Before
    public void setUp() {
        ContextTestUtil.resetContextMap();
    }

    @After
    public void cleanUp() {
        ContextTestUtil.cleanUpContext();
    }

    @Test
    public void testEnter() {
        String name = "contextA";

        Context context = ContextUtil.enter(name);
        assertNotNull(context);

        assertEquals(name, context.getName());
        assertEquals("", context.getOrigin());
        assertNull(context.getCurEntry());
        assertFalse(context.isAsync());

        DefaultNode entranceNode = context.getEntranceNode();
        assertNotNull(entranceNode);
        assertNull(entranceNode.getClusterNode());

        ResourceWrapper id = entranceNode.getId();
        assertEquals(name, id.getName());
        assertEquals(EntryType.IN, id.getType());
    }

    @Test
    public void testEnterWithoutExit() {
        Context context = ContextUtil.enter("contextA");
        assertNotNull(context);

        Context context2 = ContextUtil.enter("contextA");
        assertSame(context, context2);

        Context context3 = ContextUtil.enter("contextB");
        assertSame(context, context3);
    }

    @Test
    public void testEnterSameContextName() {
        String name = "contextA";

        Context context = ContextUtil.enter(name);
        assertNotNull(context);

        ContextUtil.exit();

        Context context2 = ContextUtil.enter(name);

        // Although enter with same context name, the two contexts are different,
        // as they are two different new objects
        assertNotSame(context, context2);
        assertNotEquals(context, context2);

        // Same context name
        assertSame(context.getName(), context2.getName());

        // The two entranceNodes are same, as contextNameNodeMap in ContextUtil holds all EntranceNode
        assertSame(context.getEntranceNode(), context2.getEntranceNode());
    }

    @Test
    public void testEnterDifferentContextName() {
        String name = "contextA";

        Context context = ContextUtil.enter(name);
        assertNotNull(context);

        ContextUtil.exit();

        String name2 = "contextB";
        Context context2 = ContextUtil.enter(name2);
        assertNotSame(context, context2);
        assertNotEquals(context, context2);

        // Different contextName different EntranceNode
        assertNotEquals(context.getEntranceNode(), context2.getEntranceNode());
        assertNotEquals(context.getName(), context2.getName());
    }

    @Test(expected = ContextNameDefineException.class)
    public void testEnterDefaultContextName() {
        Context context = ContextUtil.enter(Constants.CONTEXT_DEFAULT_NAME);
        assertNull("The default context name can't be permit to defined!", context);
    }

    @Test
    public void testExit() {
        assertNull(ContextUtil.getContext());

        ContextUtil.exit();
        assertNull(ContextUtil.getContext());

        ContextUtil.enter("contextA");
        assertNotNull(ContextUtil.getContext());
        ContextUtil.exit();
        assertNull(ContextUtil.getContext());
    }

    @Test
    public void testExitWithEntryInContext() {
        Context context = ContextUtil.enter("contextA");
        assertNotNull(context);

        Entry entry = mock(Entry.class);
        context.setCurEntry(entry);

        ContextUtil.exit();

        assertNotNull(ContextUtil.getContext());
        assertSame(context, ContextUtil.getContext());
    }

    @Test
    public void testIsDefaultContext() {
        assertFalse(ContextUtil.isDefaultContext(null));

        Context context = mock(Context.class);

        when(context.getName()).thenReturn("contextA");
        assertFalse(ContextUtil.isDefaultContext(context));

        when(context.getName()).thenReturn(Constants.CONTEXT_DEFAULT_NAME);
        assertTrue(ContextUtil.isDefaultContext(context));
    }

    @Test
    public void testReplaceContextNull() {
        Context context1 = ContextUtil.enter("contextA");
        assertNotNull(context1);

        Context backupContext = ContextUtil.replaceContext(null);

        assertNull(ContextUtil.getContext());
        assertSame(context1, backupContext);
    }

    @Test
    public void testReplaceContextNotNull() {
        Context context1 = ContextUtil.enter("contextA");
        assertNotNull(context1);

        Context context2 = mock(Context.class);

        Context backupContext = ContextUtil.replaceContext(context2);

        assertNotNull(ContextUtil.getContext());
        assertSame(context2, ContextUtil.getContext());
        assertSame(context1, backupContext);
    }

    @Test
    public void testReplaceContextCurrentNull() {
        assertNull(ContextUtil.getContext());

        Context context2 = mock(Context.class);

        Context backupContext = ContextUtil.replaceContext(context2);

        assertNotNull(ContextUtil.getContext());
        assertSame(context2, ContextUtil.getContext());
        assertNull(backupContext);
    }

    @Test
    public void testReplaceContextSame() {
        Context context1 = ContextUtil.enter("contextA");
        assertNotNull(context1);

        Context backupContext = ContextUtil.replaceContext(context1);

        assertNotNull(ContextUtil.getContext());
        assertSame(context1, ContextUtil.getContext());
        assertSame(context1, backupContext);
    }

    @Test
    public void testRunOnContext() {
        Context context1 = ContextUtil.enter("contextA");
        assertNotNull(context1);

        final Context context2 = mock(Context.class);
        when(context2.getName()).thenReturn("contextB");

        ContextUtil.runOnContext(context2, new Runnable() {
            @Override
            public void run() {
                assertSame(context2, ContextUtil.getContext());
                assertEquals("contextB", context2.getName());
            }
        });

        assertSame(context1, ContextUtil.getContext());
        assertEquals("contextA", context1.getName());
    }
}
