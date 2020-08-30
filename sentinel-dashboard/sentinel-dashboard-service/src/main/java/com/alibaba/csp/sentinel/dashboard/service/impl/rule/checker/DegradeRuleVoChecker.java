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
package com.alibaba.csp.sentinel.dashboard.service.impl.rule.checker;

import com.alibaba.csp.sentinel.dashboard.service.vo.rule.req.degrade.AddDegradeRuleReqVo;
import com.alibaba.csp.sentinel.dashboard.service.vo.rule.req.degrade.DeleteDegradeRuleReqVo;
import com.alibaba.csp.sentinel.dashboard.service.vo.rule.req.degrade.UpdateDegradeRuleReqVo;

import static com.alibaba.csp.sentinel.slots.block.RuleConstant.*;
import static com.alibaba.csp.sentinel.dashboard.service.impl.common.ParamChecker.*;

public class DegradeRuleVoChecker {

    public static void checkAdd(AddDegradeRuleReqVo reqVo) {
        checkNotNull(reqVo, "body");

        checkNotBlank(reqVo.getApp(), "app");

        checkNotBlank(reqVo.getResource(), "resource");

        checkNotNull(reqVo.getGrade(), "grade");
        checkInValues(reqVo.getGrade(), "grade", DEGRADE_GRADE_RT, DEGRADE_GRADE_EXCEPTION_RATIO, DEGRADE_GRADE_EXCEPTION_COUNT);

        checkNotNull(reqVo.getCount(), "count");
        checkCondition(reqVo.getCount() >= 0, "count must be greater than or equal to 0");
        if (reqVo.getGrade() == DEGRADE_GRADE_EXCEPTION_RATIO) {
            checkCondition(reqVo.getCount() <= 1, "count must be less than 1 when grade is " + DEGRADE_GRADE_EXCEPTION_RATIO);
        }

        if (reqVo.getGrade() == DEGRADE_GRADE_RT) {
            checkNotNullMessage(reqVo.getSlowRatioThreshold(), "slowRatioThreshold can't be null when grade is " + DEGRADE_GRADE_RT);
            checkCondition(reqVo.getSlowRatioThreshold() >= 0D && reqVo.getSlowRatioThreshold() <= 1D, "slowRatioThreshold must be between 0 and 1 when grade is " + DEGRADE_GRADE_RT);
        }

        checkNotNull(reqVo.getTimeWindow(), "timeWindow");
        checkCondition(reqVo.getTimeWindow() > 0, "timeWindow must be greater than 0");

        checkNotNull(reqVo.getMinRequestAmount(), "minRequestAmount");
        checkCondition(reqVo.getMinRequestAmount() > 0, "minRequestAmount must be greater than 0");
    }

    public static void checkUpdate(UpdateDegradeRuleReqVo reqVo) {
        checkNotNull(reqVo, "body");

        checkNotBlank(reqVo.getApp(), "app");

        checkNotNull(reqVo.getId(), "id");
        checkCondition(reqVo.getId() > 0, "id must be greater than 0");

        checkNotNull(reqVo.getGrade(), "grade");
        checkInValues(reqVo.getGrade(), "grade",0, 1, 2);

        checkNotNull(reqVo.getCount(), "count");
        checkCondition(reqVo.getCount() >= 0, "count must be greater than or equal to 0");
        if (reqVo.getGrade() == DEGRADE_GRADE_EXCEPTION_RATIO) {
            checkCondition(reqVo.getCount() <= 1, "count must be less than 1 when grade is " + DEGRADE_GRADE_EXCEPTION_RATIO);
        }

        if (reqVo.getGrade() == DEGRADE_GRADE_RT) {
            checkNotNullMessage(reqVo.getSlowRatioThreshold(), "slowRatioThreshold can't be null when grade is " + DEGRADE_GRADE_RT);
            checkCondition(reqVo.getSlowRatioThreshold() >= 0D && reqVo.getSlowRatioThreshold() <= 1D, "slowRatioThreshold must be between 0 and 1 when grade is " + DEGRADE_GRADE_RT);
        }

        checkNotNull(reqVo.getTimeWindow(), "timeWindow");
        checkCondition(reqVo.getTimeWindow() > 0, "timeWindow must be greater than 0");

        checkNotNull(reqVo.getMinRequestAmount(), "minRequestAmount");
        checkCondition(reqVo.getMinRequestAmount() > 0, "minRequestAmount must be greater than 0");
    }

    public static void checkDelete(DeleteDegradeRuleReqVo reqVo) {
        checkNotNull(reqVo, "body");

        checkNotBlank(reqVo.getApp(), "app");

        checkNotNull(reqVo.getId(), "id");
        checkCondition(reqVo.getId() > 0, "id must be greater than 0");
    }
}
