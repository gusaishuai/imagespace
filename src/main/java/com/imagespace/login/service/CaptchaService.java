package com.imagespace.login.service;

import com.imagespace.common.anno.IgnoreUserCheck;
import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.service.impl.RedisPool;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

/**
 * @author gusaishuai
 * @since 18/12/15
 */
@Slf4j
@IgnoreUserCheck
@Service("login.captcha")
public class CaptchaService implements ICallApi {

    //生成的图片的宽度
    private static final int WIDTH = 115;
    //生成的图片的高度
    private static final int HEIGHT = 40;
    //验证码的待选文字
    private static final String SEEDS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    private final RedisPool redisPool;

    @Autowired
    public CaptchaService(RedisPool redisPool) {
        this.redisPool = redisPool;
    }

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.getGraphics();
            //背景颜色
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            //边框颜色
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
            //扰乱线条颜色
            g.setColor(Color.DARK_GRAY);
            for (int i = 0; i < 10; i++) {
                int x1 = new Random().nextInt(WIDTH);
                int y1 = new Random().nextInt(HEIGHT);
                int x2 = new Random().nextInt(WIDTH);
                int y2 = new Random().nextInt(HEIGHT);
                g.drawLine(x1, y1, x2, y2);
            }
            //文字字体
            g.setFont(new Font("Default", Font.BOLD, 23));
            int x = 5;
            Color[] colors = new Color[]{Color.GREEN, Color.RED, Color.BLUE, Color.MAGENTA};
            String captcha = generateCaptcha(4);
            char[] captchaChar = captcha.toCharArray();
            Graphics2D g2D = (Graphics2D) g;
            for (int i = 0; i < 4; i++) {
                //设置字体旋转角度
                int degree = new Random().nextInt() % 30;
                //文字颜色
                g2D.setColor(colors[new Random().nextInt(4)]);
                //正向角度
                g2D.rotate(degree * Math.PI / 180, x, 20);
                g2D.drawString(captchaChar[i] + "", x, 30);
                //反向角度
                g2D.rotate(-degree * Math.PI / 180, x, 20);
                x += 30;
            }
            ByteArrayOutputStream os = null;
            try {
                os = new ByteArrayOutputStream();
                ImageIO.write(bi, "png", os);
                //保存到cookie和redis中
                saveCookieAndRedis(response, captcha);
                return new CallResult(os.toByteArray());
            } catch (IOException e) {
                log.error("getCaptcha imageIO write error", e);
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        log.error("getCaptcha os close error", e);
                    }
                }
            }
            return new CallResult(ResultCode.FAIL, "获取验证码失败");
        } catch (Exception e) {
            log.error("getCaptcha error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

    /**
     * 生成num位验证码
     */
    private String generateCaptcha(int num) {
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder captcha = new StringBuilder(num);
        for(int i = 0; i < num; i++){
            captcha.append(SEEDS.charAt(rand.nextInt(SEEDS.length() - 1)));
        }
        return captcha.toString();
    }

    /**
     * 保存到cookie中
     */
    private void saveCookieAndRedis(HttpServletResponse response, String captcha) {
        String captchaKey = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(Constant.COOKIE_CAPTCHA_KEY, captchaKey);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(10 * 60);
        response.addCookie(cookie);
        //5分钟过期时间
        redisPool.set(captchaKey, captcha, 5 * 60);
    }

}
