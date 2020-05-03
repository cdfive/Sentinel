/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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
package com.alibaba.csp.sentinel.dashboard.repository.zookeeper;

import com.alibaba.csp.sentinel.dashboard.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.repository.AbstractRuleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

/**
 * @author cdfive
 */
public class ZookeeperRuleProvider<T extends RuleEntity> extends AbstractRuleProvider<T> {

    @Autowired
    private CuratorFramework zkClient;

    @Override
    protected String fetchRules(String app, String ip, Integer port) throws Exception {
        String zkPath = buildRuleKey(app, ip, port);

        // Node path of Zookeeper must be start with / character
        zkPath = "/" + zkPath;

        Stat stat = zkClient.checkExists().forPath(zkPath);
        if(stat == null){
            return null;
        }

        byte[] bytes = zkClient.getData().forPath(zkPath);
        if (ObjectUtils.isEmpty(bytes)) {
            return null;
        }

        return new String(bytes);
    }
}