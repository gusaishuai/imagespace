package com.imagespace.common.interceptor;

import com.imagespace.common.model.Pagination;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author gusaishuai
 * @since 2019/1/10
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, new DefaultObjectFactory(),
                new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        if (mappedStatement.getId().matches(".*ByPage$")) {
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            Object parameterObject = boundSql.getParameterObject();
            if (parameterObject == null) {
                throw new NullPointerException("parameterObject is null!");
            } else {
                Pagination pagination = (Pagination) metaStatementHandler.getValue("delegate.boundSql.parameterObject.pagination");
                String sql = boundSql.getSql();
                //物理分页
                String pageSql = sql + " limit " + pagination.start() + "," + pagination.getPageSize();
                metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
                //采用物理分页后，就不需要mybatis的内存分页了，所以重置下面的两个参数
                metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
                metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
                Connection connection = (Connection) invocation.getArgs()[0];
                //重设分页参数里的总页数等
                setPage(sql, connection, mappedStatement, boundSql, pagination);
            }
        }
        return invocation.proceed();
    }

    private void setPage(String sql, Connection connection, MappedStatement mappedStatement,
                         BoundSql boundSql, Pagination pagination) {
        // 记录总记录数
        String countSql = "SELECT COUNT(1) FROM (" + sql + ") AS TOTAL";
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(countSql);
            rs = countStmt.executeQuery();
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            pagination.setTotalCount(totalCount);
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
            }
            try {
                countStmt.close();
            } catch (SQLException e) {
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
