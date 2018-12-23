package com.imagespace.excel.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Iterator;

/**
 * @author gusaishuai
 * @since 2018/12/22
 */
@Slf4j
@Service("excel.uploadExcel")
public class ExcelUploadAction implements ICallApi {

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            MultipartFile multipartFile = null;
            if (request instanceof MultipartHttpServletRequest) {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                Iterator<String> fileNames = multipartRequest.getFileNames();
                while (fileNames.hasNext()) {
                    multipartFile = multipartRequest.getFile(fileNames.next());
                    if (multipartFile == null || multipartFile.getBytes() == null || multipartFile.getBytes().length == 0) {
                        continue;
                    }
                    break;
                }
            }
            if (multipartFile != null) {
                FileUtils.writeByteArrayToFile(
                        new File("temp/excel/" + multipartFile.getOriginalFilename()), multipartFile.getBytes());
            }
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.uploadExcel error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
