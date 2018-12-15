package com.gss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * @author gusaishuai
 * @since 18/12/15
 */
@Controller
@RequestMapping("/")
public class CaptchaController {

    //生成的图片的宽度
    private static final int WIDTH = 115;
    //生成的图片的高度
    private static final int HEIGHT = 40;

    private static final String SEEDS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    @ResponseBody
    @RequestMapping("/getCaptcha")
    public byte[] getCaptcha() {
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
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder captcha = new StringBuilder(4);
        for(int i = 0; i < 4; i++){
            captcha.append(SEEDS.charAt(rand.nextInt(SEEDS.length() - 1)));
        }
        char[] captchaChar = captcha.toString().toCharArray();
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
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
