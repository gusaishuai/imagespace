package com.imagespace.excel.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
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

    @Value("${excel.upload.tempdir}")
    private String tempDir;

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
            if (multipartFile == null) {
                throw new IllegalArgumentException("无法获取上传文件");
            }
            //计算文件MD5，防止重复上传
            String fileMD5 = DigestUtils.md5Hex(multipartFile.getBytes());
            //重新生成文件名称
            String newFileName = String.format("%s%s%s", fileMD5, FilenameUtils.EXTENSION_SEPARATOR_STR,
                    FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
            //写入磁盘
            FileUtils.writeByteArrayToFile(new File(tempDir + newFileName), multipartFile.getBytes());
            //保存cookie
            saveCookie(response, newFileName);
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.uploadExcel error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

    /**
     * 保存到cookie中
     */
    private void saveCookie(HttpServletResponse response, String newFileName) {
        Cookie cookie = new Cookie(Constant.COOKIE_EXCEL_NAME, newFileName);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
    }

}
