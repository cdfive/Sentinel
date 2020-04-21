package com.alibaba.csp.sentinel.demo.apache.dubbo.provider2.provider;

import org.apache.dubbo.config.annotation.Service;

/**
 * @author xiejihan
 * @date 2020-04-21
 */
@Service
public class AnotherServiceImpl implements AnotherService {

    @Override
    public String play(String name) {
        String result = "play " + name;
        System.out.println(result);
        return result;
    }
}
