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
package com.alibaba.csp.sentinel.demo.apache.dubbo.provider;

import com.alibaba.csp.sentinel.demo.apache.dubbo.FooService;
import com.alibaba.csp.sentinel.demo.apache.dubbo.provider2.AnotherService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;

import java.time.LocalDateTime;

/**
 * @author Eric Zhao
 */
@Service
public class FooServiceImpl implements FooService {

//    @Reference(url = "dubbo://127.0.0.1:25759", timeout = 500)
//    private FooService fooService;

    @Reference(url = "dubbo://127.0.0.1:25759", timeout = 500)
    private AnotherService anotherService;

    @Override
    public String sayHello(String name) {
//        String debug = fooService.sayHello("debug");
//        System.out.println(debug);
//        System.out.println("debug=>" + name);

        System.out.println(anotherService.play(" game"));
        return String.format("Hello, %s at %s", name, LocalDateTime.now());
    }

    @Override
    public String doAnother() {
        return LocalDateTime.now().toString();
    }

    @Override
    public String exceptionTest(boolean biz, boolean timeout) {
        if (biz) {
            throw new RuntimeException("biz exception");
        }
        if (timeout) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "Success";
    }

}
