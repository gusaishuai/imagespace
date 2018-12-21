package com.imagespace.sql.service;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.sql.model.SqlExecResult;
import com.imagespace.sql.model.SqlKeyWord;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Slf4j
@Service("sql.execSql")
public class SqlExecService implements ICallApi {

    @Autowired
    private _SqlService sqlService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String sql = request.getParameter("sql");
            if (StringUtils.isBlank(sql)) {
                throw new IllegalArgumentException("sql为空");
            }
            String pageNoStr = request.getParameter("pageNo");
            int pageNo = StringUtils.isBlank(pageNoStr) ? 1 : Integer.valueOf(pageNoStr);
            SqlExecResult sqlExecResult = null;
            if (SqlKeyWord.SELECT.start(sql)) {
                sqlExecResult = sqlService.select(sql, pageNo);
            } else if (SqlKeyWord.UPDATE.start(sql) || SqlKeyWord.DELETE.start(sql)) {
                if (!StringUtils.containsIgnoreCase(sql, "where")) {
                    throw new IllegalArgumentException("删改语句必须加上WHERE条件");
                }
                sqlExecResult = sqlService.update(sql);
            } else if (SqlKeyWord.INSERT.start(sql)) {
                sqlExecResult = sqlService.update(sql);
            } else {
                throw new IllegalArgumentException("非法的sql");
            }
            return new CallResult(sqlExecResult);
        } catch (IllegalArgumentException | DataAccessException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("sql.execSql error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
