package com.imagespace.sql.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.MediaCallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.sql.model.SqlKeyWord;
import com.imagespace.sql.service.SqlService;
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
public class SqlExportAction implements ICallApi {

    @Autowired
    private SqlService sqlService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String exportId = request.getParameter("exportId");
            if (StringUtils.isBlank(exportId)) {
                String sql = request.getParameter("sql");
                if (StringUtils.isBlank(sql)) {
                    throw new IllegalArgumentException("sql为空");
                }
                if (!SqlKeyWord.SELECT.start(sql)) {
                    throw new IllegalArgumentException("sql不是查询语句");
                }
                //预检测
                String exportUuid = sqlService.preExportQuery(sql);
                return new CallResult(exportUuid);
            } else {
                String exportData = sqlService.exportQuery(exportId);
                return new MediaCallResult(exportData.getBytes(), MediaType.TEXT_PLAIN_VALUE, "sql.txt");
            }
        } catch (IllegalArgumentException | DataAccessException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("sql.exportSql error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
