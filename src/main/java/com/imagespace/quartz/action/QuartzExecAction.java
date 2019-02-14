package com.imagespace.quartz.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.quartz.model.ExecuteType;
import com.imagespace.quartz.model.QuartzCriteria;
import com.imagespace.quartz.service.QuartzMapFactory;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

/**
 * @author gusaishuai
 * @since 2019/2/13
 */
@Slf4j
@Service("quartz.execQuartz")
public class QuartzExecAction implements ICallApi {

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String classMethod = request.getParameter("key");
            if (StringUtils.isBlank(classMethod)) {
                throw new IllegalArgumentException("key不能为空");
            }
            QuartzCriteria quartzCriteria = QuartzMapFactory.INSTANCE.getQuartzMap(classMethod);
            if (quartzCriteria == null) {
                throw new IllegalArgumentException(String.format("未找到对应的定时任务：%s", classMethod));
            }
            Method quartzMethod = quartzCriteria.getClazz().getClass().getDeclaredMethod(quartzCriteria.getMethodName());
            //开始日期
            Date startDate = Calendar.getInstance().getTime();
            long startMill = System.currentTimeMillis();
            log.info("【手动】定时任务：{} 开始执行", quartzCriteria.getQuartzName());
            //手动执行任务
            quartzMethod.invoke(quartzCriteria.getClazz());
            //执行时长
            long duration = System.currentTimeMillis() - startMill;
            log.info("【手动】定时任务：{} 结束执行", quartzCriteria.getQuartzName());
            //设置为上次执行时间
            quartzCriteria.setPreExecuteDate(startDate);
            //加入定时任务执行历史
            quartzCriteria.addQuartzExecuteDetailListFIFO(startDate, duration, ExecuteType.MANUAL);
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("quartz.execQuartz error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
