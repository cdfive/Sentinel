package com.alibaba.csp.sentinel.dashboard.controller.rule;

import com.alibaba.csp.sentinel.dashboard.auth.AuthAction;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService;
import com.alibaba.csp.sentinel.dashboard.common.IdGenerator;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.domain.vo.gateway.rule.AddFlowRuleReqVo;
import com.alibaba.csp.sentinel.dashboard.repository.rule.InMemoryRuleRepositoryAdapter;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.vo.req.MachineReqVo;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

/**
 * @author cdfive
 */
public class BaseRuleController<T extends RuleEntity> {

    @Autowired
    protected IdGenerator idGenerator;

    @Autowired
    private DynamicRuleProvider<T> ruleProvider;

    @Autowired
    protected DynamicRulePublisher<T> rulePublisher;

    @Autowired
    protected InMemoryRuleRepositoryAdapter<T> repository;

//    @RequestMapping("/rules")
    protected List<T> queryRuleList(MachineReqVo reqVo) throws Exception {
//    protected Result<List<T>> queryRuleList(MachineReqVo reqVo) throws Exception {
        boolean operateApp = isOperateApp(reqVo);
        String app = reqVo.getApp();
        String ip = reqVo.getIp();
        Integer port = reqVo.getPort();
        List<T> rules;
        if (operateApp) {
            rules = ruleProvider.getRules(app);
        } else {
            rules = ruleProvider.getRules(app, ip, port);
        }

        if (rules != null && !rules.isEmpty()) {
            for (T entity : rules) {
                if (operateApp) {
                    entity.setIp(null);
                    entity.setPort(null);
                } else {
                    entity.setIp(ip);
                    entity.setPort(port);
                }
            }
        }

        return rules;
//        return Result.ofSuccess(rules);
    }

    protected void addRule(MachineReqVo reqVo, T entity) throws Exception {
        List<T> rules;
        boolean operateApp = isOperateApp(reqVo);
        if (operateApp) {
            rules = ruleProvider.getRules(reqVo.getApp());
        } else {
            rules = ruleProvider.getRules(reqVo.getApp(), reqVo.getIp(), reqVo.getPort());
        }
        rules.add(entity);

        if (operateApp) {
            rulePublisher.publish(reqVo.getApp(), rules);
        } else {
            rulePublisher.publish(reqVo.getApp(), reqVo.getIp(), reqVo.getPort(), rules);
        }
    }

    protected T updateRule(MachineReqVo reqVo, Long id, UpdateCallback<T> updateCallback) throws Exception {
        List<T> rules;
        boolean operateApp = isOperateApp(reqVo);
        if (operateApp) {
            rules = ruleProvider.getRules(reqVo.getApp());
        } else {
            rules = ruleProvider.getRules(reqVo.getApp(), reqVo.getIp(), reqVo.getPort());
        }

        Optional<T> optRule = rules.stream().filter(o -> id.equals(o.getId())).findFirst();
        if (!optRule.isPresent()) {
////            return Result.ofFail(-1, "data not exist, id=" + id);
//            return null;
        }

        T entity = optRule.get();
        updateCallback.doUpdate(entity);

        if (operateApp) {
            rulePublisher.publish(reqVo.getApp(), rules);
        } else {
            rulePublisher.publish(reqVo.getApp(), reqVo.getIp(), reqVo.getPort(), rules);
        }

        return entity;
    }

//    protected T getRule(MachineReqVo reqVo, Long id) throws Exception {
//        List<T> rules;
//        boolean operateApp = isOperateApp(reqVo);
//        if (operateApp) {
//            rules = ruleProvider.getRules(reqVo.getApp());
//        } else {
//            rules = ruleProvider.getRules(reqVo.getApp(), reqVo.getIp(), reqVo.getPort());
//        }
//        Optional<T> optRule = rules.stream().filter(o -> id.equals(o.getId())).findFirst();
//        if (!optRule.isPresent()) {
////            return Result.ofFail(-1, "data not exist, id=" + id);
//            return null;
//        }
//
//        return optRule.get();
//    }

    protected void deleteRule(MachineReqVo reqVo, Long id) throws Exception {
        List<T> rules;
        boolean operateApp = isOperateApp(reqVo);
        if (operateApp) {
            rules = ruleProvider.getRules(reqVo.getApp());
        } else {
            rules = ruleProvider.getRules(reqVo.getApp(), reqVo.getIp(), reqVo.getPort());
        }
        Optional<T> optRule = rules.stream().filter(o -> id.equals(o.getId())).findFirst();
        if (!optRule.isPresent()) {
        }

        T rule = optRule.get();
        rules.remove(rule);

        if (operateApp) {
            rulePublisher.publish(reqVo.getApp(), rules);
        } else {
            rulePublisher.publish(reqVo.getApp(), reqVo.getIp(), reqVo.getPort(), rules);
        }
    }

    private void publishRules(/*@NonNull*/ MachineReqVo reqVo) throws Exception {
        boolean operateApp = isOperateApp(reqVo);
        if (operateApp) {
            List<T> rules = repository.findAllByApp(reqVo.getApp());
            rulePublisher.publish(reqVo.getApp(), rules);
        } else {
            List<T> rules = repository.findAllByMachine(MachineInfo.of(reqVo.getApp(), reqVo.getIp(), reqVo.getPort()));
            rulePublisher.publish(reqVo.getApp(), reqVo.getIp(), reqVo.getPort(), rules);
        }
    }

    protected boolean isOperateApp(MachineReqVo reqVo) {
        String ip = reqVo.getIp();
        Integer port = reqVo.getPort();
        return StringUtil.isEmpty(ip) || port == null;
    }

    protected static interface UpdateCallback<T> {
        void doUpdate(T toUpdateRuleEntity);
    }
}
