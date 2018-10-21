package com.taobao.csp.sentinel.dashboard.util;

import com.taobao.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.taobao.csp.sentinel.dashboard.datasource.entity.influxdb.MetricPO;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author cdfive
 * @date 2018-10-19
 */
public class InfluxDBUtilsTest {

    @Test
    public void test() {
        String url = "http://localhost:8086";
        String username = "admin";
        String password = "123456";
        InfluxDBUtils.init(url, username, password);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM sentinel_metric");
        sql.append(" WHERE app=$app");
        sql.append(" AND time>=$startTime");

//        QueryResult queryResult = InfluxDBUtils.query(new InfluxDBQueryCallback() {
//            @Override
//            public QueryResult doCallBack(InfluxDB influxDB) {
//                Query query = BoundParameterQuery.QueryBuilder.newQuery(sql.toString())
//                        .forDatabase(InfluxDBUtils.database)
//                        .bind("app", "app")
//                        .bind("startTime", "2018-10-19")
//                        .create();
//                QueryResult queryResult = influxDB.query(query);
//                return queryResult;
//            }
//        });

//        QueryResult queryResult = InfluxDBUtils.query(new InfluxDBQueryCallback() {
//            @Override
//            public QueryResult doCallBack(InfluxDB influxDB) {
//                return influxDB.query(new Query("select * from metric", InfluxDBUtils.database));
//            }
//        });

//        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

//        List<MetricPO> metricPOS = resultMapper.toPOJO(queryResult, MetricPO.class);


        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("app", "sentinel-dashboard");
        paramMap.put("startTime", "2018-10-20 14:11:18.678");

//        org.influxdb.InfluxDBMapperException: InfluxDB returned an error with Series: invalid operation: time and *influxql.StringLiteral are not compatible
//        paramMap.put("startTime", new Date());

//        paramMap.put("startTime", System.currentTimeMillis() - 600000);

//        paramMap.put("startTime", DateUtils.addMinutes(new Date(), -10).getTime());

        List<MetricPO> metricPOS = InfluxDBUtils.queryList("sentinel_db", sql.toString(), paramMap, MetricPO.class);
        System.out.println(metricPOS.size());

        System.out.println("done");
    }

    @Test
    public void insert() throws Exception {
        InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086");
        influxDB.setDatabase("sentinel_db");

        influxDB.write(Point.measurement("metric")
//                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("id", "1")
                .addField("idle", 90L)
                .addField("user", 9L)
                .addField("system", 1L)
                .build());

//        long startTime = System.currentTimeMillis() - 1000 * 60;
//
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT * FROM metric");
//        sql.append(" WHERE app=$app");
//        sql.append(" AND time>=$startTime");
//
//        Query query = BoundParameterQuery.QueryBuilder.newQuery(sql.toString())
////                .bind("app", app)
//                .bind("startTime", startTime)
//                .create();
//        QueryResult queryResult = influxDB.query(query);

        influxDB.close();
    }

    private static final String SENTINEL_DATABASE = "sentinel_db";

    private static final String METRIC_MEASUREMENT = "sentinel_metric";

    @Test
    public void batchInsert() throws Exception {
        long start = System.currentTimeMillis();

        String url = "http://10.100.12.106:8086";
        String username = "admin";
        String password = "123456";
        InfluxDBUtils.init(url, username, password);

        MetricEntity metric = new MetricEntity();
//        metric.setTimestamp(DateUtils.parseDate("2018-10-09 11:11:11", new String[]{"yyyy-MM-dd HH:mm:ss"}));
        metric.setApp("testApp");
        metric.setResource("resource");
        metric.setId(System.currentTimeMillis());
        metric.setGmtCreate(new Date());
        metric.setGmtModified(new Date());
        metric.setPassQps(1L);
        metric.setSuccessQps(1L);
        metric.setBlockQps(0L);
        metric.setExceptionQps(0L);
        metric.setRt(0D);
        metric.setCount(1);

//        for (int i = 0; i < 100; i++) {
//            metric.setTimestamp(DateUtils.addMilliseconds(metric.getTimestamp(), -1));
////            InfluxDBUtils.insert(SENTINEL_DATABASE, new InfluxDBUtils.InfluxDBInsertCallback() {
////                        @Override
////                        public void doCallBack(String database, InfluxDB influxDB) {
////                            influxDB.write(Point.measurement(METRIC_MEASUREMENT)
////                                    .time(DateUtils.addHours(metric.getTimestamp(), 8).getTime(), TimeUnit.MILLISECONDS)
////                                    .tag("app", metric.getApp())
////                                    .tag("resource", metric.getResource())
////                                    .addField("id", metric.getId())
////                                    .addField("gmtCreate", metric.getGmtCreate().getTime())
////                                    .addField("gmtModified", metric.getGmtModified().getTime())
////                                    .addField("passQps", metric.getPassQps())
////                                    .addField("successQps", metric.getSuccessQps())
////                                    .addField("blockQps", metric.getBlockQps())
////                                    .addField("exceptionQps", metric.getExceptionQps())
////                                    .addField("rt", metric.getRt())
////                                    .addField("count", metric.getCount())
////                                    .addField("resourceCode", metric.getResourceCode())
////                                    .build());
////                        }
////                    }
////            );
//
//            System.out.println((i+1));
//        }

//        if (true) {
//            Object ojb = InfluxDBUtils.process(SENTINEL_DATABASE, new InfluxDBUtils.InfluxDBCallback() {
//                @Override
//                public <T> T doCallBack(String database, InfluxDB influxDB) {
//                    String sql = "select min(time) from sentinel_metric"
//                    BoundParameterQuery.QueryBuilder queryBuilder = BoundParameterQuery.QueryBuilder.newQuery(sql);
//                    queryBuilder.forDatabase(database);
//
//
//                    return (T) influxDB.query(queryBuilder.create());
//                }
//            });

//            return;
//        }

//        DateFormatUtils.format(minTime, "yyyy-MM-dd HH:mm:ss.SSS")

        final Date minTime;

        String sql = "SELECT * FROM sentinel_metric order by time limit 1";
        List<MetricPO> metricPOS = InfluxDBUtils.queryList(SENTINEL_DATABASE, sql, null, MetricPO.class);

        if (CollectionUtils.isEmpty(metricPOS)) {
            minTime = DateUtils.parseDate("2018-01-10 11:11:11", new String[]{"yyyy-MM-dd HH:mm:ss"});
        } else {
            MetricPO metricPO = metricPOS.get(0);
            Instant time = metricPO.getTime();
            minTime = DateUtils.addHours(Date.from(time), -8);
        }

        InfluxDBUtils.process(SENTINEL_DATABASE, new InfluxDBUtils.InfluxDBCallback() {
            @Override
            public <T> T doCallBack(String database, InfluxDB influxDB) {
                influxDB.enableBatch(BatchOptions.DEFAULTS.actions(2000).flushDuration(100));
//                influxDB.enableBatch(BatchOptions.DEFAULTS.actions(5000).flushDuration(50));

                for (int i = 0; i < 1000000; i++) {
                    metric.setTimestamp(DateUtils.addSeconds(minTime, (i+1)));
                    metric.setId(metric.getTimestamp().getTime());

                    influxDB.write(Point.measurement(METRIC_MEASUREMENT)
                            .time(DateUtils.addHours(metric.getTimestamp(), 8).getTime(), TimeUnit.MILLISECONDS)
                            .tag("app", metric.getApp())
                            .tag("resource", metric.getResource())
                            .addField("id", metric.getId())
                            .addField("gmtCreate", metric.getGmtCreate().getTime())
                            .addField("gmtModified", metric.getGmtModified().getTime())
                            .addField("passQps", metric.getPassQps())
                            .addField("successQps", metric.getSuccessQps())
                            .addField("blockQps", metric.getBlockQps())
                            .addField("exceptionQps", metric.getExceptionQps())
                            .addField("rt", metric.getRt())
                            .addField("count", metric.getCount())
                            .addField("resourceCode", metric.getResourceCode())
                            .build());

                    System.out.println((i+1) + "" + DateUtils.addHours(metric.getTimestamp(), 8));
                }

                return null;
            }
        });


        System.out.println("done");
        System.out.println("cost " + (System.currentTimeMillis() - start) / 1000.0 + "s");
    }
}
