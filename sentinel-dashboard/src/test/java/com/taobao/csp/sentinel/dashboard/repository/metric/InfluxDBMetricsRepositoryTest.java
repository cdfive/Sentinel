package com.taobao.csp.sentinel.dashboard.repository.metric;

import com.taobao.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.taobao.csp.sentinel.dashboard.util.InfluxDBUtils;
import org.junit.Test;

import java.util.Date;

/**
 * @author cdfive
 * @date 2018-10-19
 */
public class InfluxDBMetricsRepositoryTest {

    private InfluxDBMetricsRepository repository = new InfluxDBMetricsRepository();

    @Test
    public void test() {
        String url = "http://localhost:8086";
        String username = "admin";
        String password = "123456";
        InfluxDBUtils.init(url, username, password);

        MetricEntity metric = new MetricEntity();
        metric.setId(1L);
        metric.setGmtCreate(new Date());
        metric.setGmtModified(new Date());
        metric.setApp("app");

        metric.setTimestamp(new Date());
        metric.setResource("resource");
        metric.setPassQps(10L);
        metric.setSuccessQps(8L);
        metric.setBlockQps(2L);
        metric.setExceptionQps(0L);

        metric.setRt(100D);
        metric.setCount(0);

        repository.save(metric);
    }
}
