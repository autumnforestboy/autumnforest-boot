package io.github.autumnforest.boot.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
public class DBUtil {

    public static Connection getMysqlConnection(String ip, Integer port, String db, String username, String password) throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + (StringUtils.isEmpty(db) ? "" : db) + "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8", username, password);
    }

    public static <R> R queryOne(Connection connection, String sql, Class<R> clazz, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        return runner.query(connection, sql,
                new BeanHandler<>(clazz, new BasicRowProcessor(new GenerousBeanProcessor())), params);
    }

    public static <R> List<R> queryList(Connection connection, String sql, Class<R> clazz, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        return runner.query(connection, sql,
                new BeanListHandler<>(clazz, new BasicRowProcessor(new GenerousBeanProcessor())), params);
    }

    public static <R> R query(Connection connection, String sql, ScalarHandler<R> scalarHandler, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        return runner.query(connection, sql,
                scalarHandler, params);
    }

    //count
    public static List<Map<String, Object>> queryMapList(Connection connection, String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        return runner.query(connection, sql,
                new MapListHandler(), params);
    }


    public static Map<String, Object> queryMap(Connection connection, String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        return runner.query(connection, sql,
                new MapHandler(), params);
    }

}
