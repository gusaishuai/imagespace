package com.imagespace.quartz.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.quartz.model.QuartzCriteria;
import com.imagespace.quartz.service.QuartzMapFactory;
import com.imagespace.quartz.service.QuartzSchedulerFactory;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 2019/2/13
 */
@Slf4j
@Service("quartz.openOrCloseQuartz")
public class QuartzStatusAction implements ICallApi {

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String classMethod = request.getParameter("key");
            if (StringUtils.isBlank(classMethod)) {
                throw new IllegalArgumentException("key不能为空");
            }
            //0-关闭 1-开启
            String operation = request.getParameter("operation");
            if (StringUtils.isBlank(operation)) {
                throw new IllegalArgumentException("请设置关闭或开启模式");
            }
            if (StringUtils.equals(operation, "ON")) {
                //先物理删除schedule中的job
                QuartzSchedulerFactory.INSTANCE.deleteJob(classMethod);
                //再逻辑删除map中的job
                QuartzMapFactory.INSTANCE.updateStatusQuartzMap(classMethod, false);
            } else if (StringUtils.equals(operation, "OFF")) {
                QuartzCriteria quartzCriteria = QuartzMapFactory.INSTANCE.getQuartzMap(classMethod);
                if (quartzCriteria == null) {
                    throw new IllegalArgumentException(String.format("未找到对应的定时任务：%s", classMethod));
                }
                //先物理开启schedule中的job
                QuartzSchedulerFactory.INSTANCE.addJob(quartzCriteria);
                //再逻辑开启map中的job
                QuartzMapFactory.INSTANCE.updateStatusQuartzMap(classMethod, true);
            }
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("quartz.openOrCloseQuartz error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
