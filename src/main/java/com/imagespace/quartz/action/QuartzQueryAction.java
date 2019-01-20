package com.imagespace.quartz.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Page;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.quartz.model.vo.QuartzVo;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gusaishuai
 * @since 19/1/19
 */
@Slf4j
@Service("quartz.queryQuartz")
public class QuartzQueryAction implements ICallApi {

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String quartzName = request.getParameter("quartzName");
            String methodName = request.getParameter("methodName");
            Page<QuartzVo> voPage = new Page<>(1, 15);
            QuartzVo vo1 = new QuartzVo();
            vo1.setQuartzName("定时任务1");
            vo1.setClassName("com.aaa.Class");
            vo1.setMethodName("methodName");
            vo1.setStartTime("2019-01-19 15:10:17");
            vo1.setIntervalTime("5秒");
            vo1.setRepeatNum("无限次");
            vo1.setStatus("开启");

            QuartzVo vo2 = new QuartzVo();
            vo2.setQuartzName("定时任务2");
            vo2.setClassName("com.aaa.Class2");
            vo2.setMethodName("methodName2");
            vo2.setStartTime("2018-01-19 15:10:17");
            vo2.setCronExpression("* * 12 ? * *");
            vo2.setStatus("开启");

            List<QuartzVo> voList = new ArrayList<>();
            voList.add(vo1);
            voList.add(vo2);

            voPage.setTotalCount(16);
            voPage.setList(voList);
            return new CallResult(voPage);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("quartz.queryQuartz error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
