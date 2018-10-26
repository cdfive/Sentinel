package com.taobao.csp.sentinel.dashboard.util;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author cdfive
 * @date 2018-10-19
 */
@Component
public class InfluxDBUtils {

    private static String url;

    private static String username;

    private static String password;

    private static InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

    @Value("${influxdb.url}")
    public void setUrl(String url) {
        InfluxDBUtils.url = url;
    }

    @Value("${influxdb.username}")
    public void setUsername(String username) {
        InfluxDBUtils.username = username;
    }

    @Value("${influxdb.password}")
    public void setPassword(String password) {
        InfluxDBUtils.password = password;
    }

    public static void init(String url, String username, String password) {
        InfluxDBUtils.url = url;
        InfluxDBUtils.username = username;
        InfluxDBUtils.password = password;
    }

    public static <T> T process(String database, InfluxDBCallback callback) {
        InfluxDB influxDB = InfluxDBFactory.connect(url, username, password);
        influxDB.setDatabase(database);

        T t = callback.doCallBack(database, influxDB);

        influxDB.close();

        return t;
    }

    public static void insert(String database, InfluxDBInsertCallback influxDBInsertCallback) {
        process(database, new InfluxDBCallback() {
            @Override
            public <T> T doCallBack(String database, InfluxDB influxDB) {
                influxDBInsertCallback.doCallBack(database, influxDB);
                return null;
            }
        });

    }

    public static QueryResult query(String database, InfluxDBQueryCallback influxDBQueryCallback) {
        return process(database, new InfluxDBCallback() {
            @Override
            public <T> T doCallBack(String database, InfluxDB influxDB) {
                QueryResult queryResult = influxDBQueryCallback.doCallBack(database, influxDB);
                return (T) queryResult;
            }
        });
    }

    public static <T> List<T> queryList(String database, String sql, Map<String, Object> paramMap, Class<T> clasz) {
        QueryResult queryResult = query(database, new InfluxDBQueryCallback() {
            @Override
            public QueryResult doCallBack(String database, InfluxDB influxDB) {
                BoundParameterQuery.QueryBuilder queryBuilder = BoundParameterQuery.QueryBuilder.newQuery(sql);
                queryBuilder.forDatabase(database);

                if (paramMap != null && paramMap.size() > 0) {
                    Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
                    for (Map.Entry<String, Object> entry : entries) {
                        queryBuilder.bind(entry.getKey(), entry.getValue());
                    }
                }

                return influxDB.query(queryBuilder.create());
            }
        });

        return resultMapper.toPOJO(queryResult, clasz);
    }

    public interface InfluxDBCallback {
        <T> T doCallBack(String database, InfluxDB influxDB);
    }

    public interface InfluxDBInsertCallback {
        void doCallBack(String database, InfluxDB influxDB);
    }

    public interface InfluxDBQueryCallback {
        QueryResult doCallBack(String database, InfluxDB influxDB);
    }
}
