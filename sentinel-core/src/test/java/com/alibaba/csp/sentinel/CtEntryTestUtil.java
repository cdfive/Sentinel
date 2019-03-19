package com.alibaba.csp.sentinel;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;

/**
 * @author cdfive
 */
public class CtEntryTestUtil {

    public static CtEntry buildCtEntry(ResourceWrapper resourceWrapper, ProcessorSlot<Object> chain, Context context) {
        return new CtEntry(resourceWrapper, chain, context);
    }
}
