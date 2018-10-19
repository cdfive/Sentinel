package com.taobao.csp.sentinel.dashboard.util;

import com.taobao.csp.sentinel.dashboard.datasource.entity.influxdb.MetricPO;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        sql.append("SELECT * FROM metric");
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
        paramMap.put("startTime", "2018-10-19");

        List<MetricPO> metricPOS = InfluxDBUtils.queryList("sentinel_db", sql.toString(), paramMap, MetricPO.class);
        System.out.println(metricPOS.size());

        System.out.println("done");
    }
}
