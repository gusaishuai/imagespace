package com.imagespace.excel.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
@Slf4j
@Service("excel.exprQuery")
public class ExcelExprQueryAction implements ICallApi {

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String[] exprRows = request.getParameterValues("exprRows");
            String[] leftBracket = request.getParameterValues("leftBracket");
            String[] colNum = request.getParameterValues("colNum");
            String[] match = request.getParameterValues("match");
            String[] regex = request.getParameterValues("regex");
            String[] rightBracket = request.getParameterValues("rightBracket");
            String[] conj = request.getParameterValues("conj");
            Cookie[] cookies = request.getCookies();
            String excelName = cookies == null ? null : Arrays.stream(cookies)
                    .filter(r -> StringUtils.equals(r.getName(), Constant.COOKIE_EXCEL_NAME))
                    .findFirst().map(Cookie::getValue).orElse(null);
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.exprQuery error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
