package com.alibaba.csp.sentinel.context;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author cdfive
 */
public class ContextUtilTest {

    @Test
    public void testEnter() {
        String name = "entry1";

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
    public void testEnterSameContextName() {
        String name = "entry1";

        Context context = ContextUtil.enter(name);
        assertNotNull(context);

        ContextUtil.exit();

        Context context2 = ContextUtil.enter(name);
        assertNotEquals(context, context2);//
    }

    @Test
    public void testEnterDifferentContextName() {
        String name = "entry1";

        Context context = ContextUtil.enter(name);
        assertNotNull(context);

        ContextUtil.exit();

        String name2 = "entry2";
        Context context2 = ContextUtil.enter(name2);
        assertNotEquals(context, context2);
    }

    @Test(expected = ContextNameDefineException.class)
    public void testEnterDefaultContextName() {
        Context context = ContextUtil.enter(Constants.CONTEXT_DEFAULT_NAME);
        assertNull("The default context name can't be permit to defined!", context);
    }

    @Test
    public void testIsDefaultContext() {
        assertFalse(ContextUtil.isDefaultContext(null));

        Context context = mock(Context.class);
        when(context.getName()).thenReturn("serviceA");
        assertFalse(ContextUtil.isDefaultContext(context));

        when(context.getName()).thenReturn(Constants.CONTEXT_DEFAULT_NAME);
        assertTrue(ContextUtil.isDefaultContext(context));
    }
}
