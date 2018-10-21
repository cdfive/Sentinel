package com.taobao.csp.sentinel.dashboard.repository.metric;

import com.taobao.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.taobao.csp.sentinel.dashboard.util.InfluxDBUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * @author cdfive
 * @date 2018-10-19
 */
public class InfluxDBMetricsRepositoryTest {

    private InfluxDBMetricsRepository repository = new InfluxDBMetricsRepository();

    @Before
    public void before() {
        String url = "http://localhost:8086";
        String username = "admin";
        String password = "123456";
        InfluxDBUtils.init(url, username, password);
    }

    @Test
    public void save() {
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

    @Test
    public void queryByAppAndResourceBetween() throws Exception {
        long start = System.currentTimeMillis();

        String app = "testApp";
        String resource = "resource";

        long endTime = DateUtils.parseDate("2018-10-10 11:11:11", new String[]{"yyyy-MM-dd HH:mm:ss"}).getTime();
        long startTime = endTime - 1000 * 60 * 5;
//            long startTime = endTime - 1000 * 6 * 1;

        List<MetricEntity> metricEntities = repository.queryByAppAndResourceBetween(app, resource, startTime, endTime);
        System.out.println("total size=" + metricEntities.size());

        System.out.println("cost " + (System.currentTimeMillis() - start) / 1000.0 + "s");
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void queryByAppAndResourceBetweenLoop() throws Exception {
        int loopTimes = 10;
        for (int i = 0; i < loopTimes; i++) {
            queryByAppAndResourceBetween();
        }
    }
}
