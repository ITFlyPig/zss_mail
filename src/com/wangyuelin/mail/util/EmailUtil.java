package com.wangyuelin.mail.util;

import com.wangyuelin.mail.Log;
import com.wangyuelin.mail.conf.FileConfig;
import org.apache.commons.mail.*;

import java.io.*;
import java.util.Properties;

/**
 * 邮件发送的工具类
 * @author wangyuelin
 *
 */
public class EmailUtil {
    private static Properties props;
    private static String USER_NAME;
    private static String PASSWORD ;
    private static String SMTP_ADDRESS ;

    private static String TAG = "EmailUtil";

    //root + "/properties/mail_conf.properties")
    public static void loadConfg(String filePath){
        if(filePath == null){
            System.out.println("配置文件的路径为空，退出！");
            return;
        }
        // 配置发送邮件的环境属性
        props = new Properties();
        //
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            in.close();
            USER_NAME = props.getProperty("mail.user");
            PASSWORD = props.getProperty("mail.password");
            SMTP_ADDRESS = props.getProperty("mail.smtp.host");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //	public static void sendAttachmentEmail(String attachmentPath, ){
    //		 //发送带附件的邮件
    //        EmailAttachment attachment = new EmailAttachment();
    //        //附件的路劲
    //        attachment.setPath("/Users/wangyuelin/Downloads/C++学习笔记(一).pdf");
    //        attachment.setDisposition(EmailAttachment.ATTACHMENT);
    //        attachment.setDescription("pdf");
    //        attachment.setName("C++学习笔记(一).pdf");
    //     // Create the email message
    //        MultiPartEmail email = new MultiPartEmail();
    //        email.setHostName("smtp.163.com");
    //        email.setAuthentication("wyl_coder@163.com", "kjih4321");
    //      try {
    //			email.addTo("1347248229@qq.com");
    //			email.setFrom("wyl_coder@163.com");
    //			email.setSubject("图片");
    //			email.setMsg("这是你想呀的图片");
    //			email.attach(attachment);
    //			email.send();
    //		} catch (EmailException e) {
    //			e.printStackTrace();
    //		}
    //	}

    /**
     * 发送带附件的邮件
     * @param attachmentPath 附件的路径
     * @param attachmentName 附件的名称
     * @param subject
     * @param content
     * @param recipients 接收邮件的人
     */
    public static boolean sendAttachmentEmail(String attachmentPath, String attachmentName, String subject, String content, String[] recipients ){
        loadConfg(FileConfig.MAIL_FILE);
        File file = new File(attachmentPath + File.separator +attachmentName);
        //发送带附件的邮件
        EmailAttachment attachment = new EmailAttachment();
        //附件的路劲
        attachment.setPath(file.getAbsolutePath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("excel");
        attachment.setName(attachmentName);
        // Create the email message
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(SMTP_ADDRESS);
        email.setAuthentication(USER_NAME, PASSWORD);
        try {
            email.addTo(recipients);
            email.setFrom(USER_NAME);
            email.setSubject(subject);
            email.setMsg(content);
            email.attach(attachment);
            email.send();
            return true;
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将错误发送到错误的订阅者
     * @param toAddress
     * @param subject
     * @param content
     */
    public static void sendMail(String toAddress, String subject, String content){
        loadConfg(FileConfig.MAIL_FILE);
        Email email = new SimpleEmail();
        email.setDebug(true);
        email.setCharset("utf-8");
        email.setHostName(SMTP_ADDRESS);
        email.setAuthenticator(new DefaultAuthenticator(USER_NAME, PASSWORD));
        //email.setSSLOnConnect(true);
        //        email.setSSL(true);//commons-mail-1.1支持的方法，1.4中使用setSSLOnConnect(true)代替
        try {
            email.setFrom(USER_NAME);
            email.setSubject(subject);
            email.setMsg(content);
            email.addTo(toAddress);
            email.send();
            Log.MyLog(TAG, "邮件发送成功，接收人：" + toAddress);
        } catch (EmailException e) {
            Log.MyLog(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

    }
}
