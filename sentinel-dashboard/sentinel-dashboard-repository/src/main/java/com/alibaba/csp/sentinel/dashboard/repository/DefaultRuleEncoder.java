package com.alibaba.csp.sentinel.dashboard.repository;

import com.alibaba.csp.sentinel.dashboard.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @author cdfive
 */
public class DefaultRuleEncoder<T extends RuleEntity> implements Converter<List<T>, String> {

    @Override
    public String convert(List<T> source) {
        return JSON.toJSONString(source);
    }
}