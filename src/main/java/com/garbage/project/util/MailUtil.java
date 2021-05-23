package com.garbage.project.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Properties;

public class MailUtil {

    private static final String AUTH_CODE="ygdqoswfmwrzbafb";

    private static final String SENDER_EMAIL = "1405679773@qq.com";

    public static void sendEmail(String url,String reciever) throws Exception{
        // 服务器地址:
        String smtp = "smtp.qq.com";
        // 登录用户名:
        String username = SENDER_EMAIL;
        // 登录口令:
        String password = AUTH_CODE;
        // 连接到SMTP服务器25端口:
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp); // SMTP主机名
        props.put("mail.smtp.port", "25"); // 主机端口号
        props.put("mail.smtp.auth", "true"); // 是否需要用户认证
        props.put("mail.smtp.starttls.enable", "true"); // 启用TLS加密
        // 获取Session实例:
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, AUTH_CODE);
            }
        });
        // 设置debug模式便于调试:
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);
        // 设置发送方地址:
        message.setFrom(new InternetAddress(SENDER_EMAIL));
        // 设置接收方地址:
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(reciever));
        // 设置邮件主题:
        message.setSubject("Reset Your Password", "UTF-8");
        // 设置邮件正文:
        int code = (int) ((Math.random()+1) * 100000);
        message.setText("<a>重置您的密码，请点击进入如下链接并输入验证码，30分钟后失效</a>" +
                "<a href=" + url+">"+url+"</a>" +
                "<p>验证码为：" +code+ "</p>", "UTF-8","html");
        // 发送:
        Transport.send(message);
    }

//    public static void main(String[] args) {
//        try {
//            sendEmail();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
