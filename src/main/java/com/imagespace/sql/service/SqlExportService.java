package com.imagespace.sql.service;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.MediaCallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 2018/12/18
 */
@Slf4j
@Service("sql.exportSql")
public class SqlExportService implements ICallApi {

    @Autowired
    private _SqlService sqlService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String sql = request.getParameter("sql");
            if (StringUtils.isBlank(sql)) {
                throw new IllegalArgumentException("sql为空");
            }
            String exportData = sqlService.exportQuery(sql);
            return new MediaCallResult(exportData.getBytes(), MediaType.TEXT_PLAIN_VALUE, "sql.txt");
        } catch (IllegalArgumentException | DataAccessException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("sql.exportSql error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
