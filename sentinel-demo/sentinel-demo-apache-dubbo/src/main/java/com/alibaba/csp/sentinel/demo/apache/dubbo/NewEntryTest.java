package com.alibaba.csp.sentinel.demo.apache.dubbo;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class NewEntryTest {
    public static void main(String[] args) throws Exception {
//        Map<String, Object> map = new HashMap<>();
        try {
            for (int i = 0; i < 1000000; i++) {
                Entry entry = SphU.entry("cdfive", ResourceTypeConstants.COMMON_RPC, EntryType.OUT);
//                map.put("KEY", entry);
                entry.exit();
                System.out.println(i + "=>" + entry.getResourceWrapper().getName());
                TimeUnit.MICROSECONDS.sleep(ThreadLocalRandom.current().nextInt(50));
            }
        } catch (BlockException e) {
            e.printStackTrace();
        }

        System.out.println("NewEntryTest done");
    }
}
