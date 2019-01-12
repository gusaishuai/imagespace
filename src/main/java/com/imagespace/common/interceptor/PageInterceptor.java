package com.imagespace.common.interceptor;

import com.imagespace.common.model.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author gusaishuai
 * @since 2019/1/10
 */
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环可以分离出最原始的的目标类)
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        if (metaStatementHandler.hasGetter("delegate.boundSql.parameterObject.pagination")) {
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            String sql = boundSql.getSql();
            Pagination pagination = (Pagination) metaStatementHandler.getValue("delegate.boundSql.parameterObject.pagination");
            String pageSql =  String.format("%s limit %s, %s", sql, pagination.start(), pagination.getPageSize());
            //物理分页
            metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
            Connection connection = (Connection) invocation.getArgs()[0];
            //重设分页参数里的总页数
            pagination.setTotalCount(sqlCount(sql, boundSql, connection, mappedStatement));
        }
        return invocation.proceed();
    }

    /**
     * 计算总数
     */
    private int sqlCount(String sql, BoundSql boundSql, Connection connection,
                         MappedStatement mappedStatement) throws SQLException {
        //组装总数sql
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            //统计总数
            String countSql = String.format("SELECT COUNT(1) FROM (%s) AS TOTAL", sql);
            countStmt = connection.prepareStatement(countSql);
            //此时的sql还没有where条件中参数的值，如：where id = ?
            BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(),
                    countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            //加上sql的where条件中带的参数，如：where id = 1234
            ParameterHandler parameterHandler = new DefaultParameterHandler(
                    mappedStatement, boundSql.getParameterObject(), countBoundSql);
            parameterHandler.setParameters(countStmt);
            //查询总数
            rs = countStmt.executeQuery();
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            return totalCount;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                log.error("PageInterceptor.sqlCount close rs error", e);
            }
            try {
                if (countStmt != null) {
                    countStmt.close();
                }
            } catch (SQLException e) {
                log.error("PageInterceptor.sqlCount close countStmt error", e);
            }
        }
    }

    @Override
    public Object plugin(Object o) {
        if (o instanceof StatementHandler) {
            return Plugin.wrap(o, this);
        } else {
            return o;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

}