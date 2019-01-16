package com.imagespace.sql.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.sql.service.SqlService;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author gusaishuai
 * @since 2018/12/18
 */
@Slf4j
@Service("sql.getColumn")
public class SqlColumnAction implements ICallApi {

    private final SqlService sqlService;

    @Autowired
    public SqlColumnAction(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String table = request.getParameter("table");
            if (StringUtils.isBlank(table)) {
                throw new IllegalArgumentException("table为空");
            }
            List<Map<String, Object>> tableColumnList = sqlService.getTableColumns(table);
            return new CallResult(tableColumnList);
        } catch (IllegalArgumentException | DataAccessException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("sql.getColumn error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
