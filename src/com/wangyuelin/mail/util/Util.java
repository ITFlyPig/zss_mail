package com.wangyuelin.mail.util;

import com.wangyuelin.mail.Constant;
import com.wangyuelin.mail.EmailInfo;
import com.wangyuelin.mail.Log;
import com.wangyuelin.mail.conf.Config;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Util {

    private static String TAG = "Util";
    /**
     * 解压得到要发送的联系人的列表和对应的sql文件
     *
     * @param filePath
     * @return
     */
    public Map<String, List<String>> parseTos(String filePath) {
        return null;

    }

    /**
     * 获得两个时间的时间间隔
     *
     * @param start
     * @param end
     * @return
     */
    public static long timeLeft(long start, long end) {
        return end - start;

    }

    /**
     * 据小时算得对应的毫秒
     *
     * @param hour
     * @return
     */
    public static long getTimeByHour(int hour) {
        return hour * 60 * 60 * 1000;

    }

    /**
     * 由日期获取对应的毫秒值
     *
     * @param dateStr 格式yyyy-MM-dd HH:mm
     * @return
     */
    public static long getTimeByDate(String dateStr) {
        if (dateStr == null || dateStr.equals("")) {
            return -1;//错误，非法值
        }
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            long time = dateformat.parse(dateStr).getTime();
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;

    }

    /*
    * 将时间戳转换为时间
    */
    public static String stampToDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    /**
     * 是否应该立即发送邮件
     *
     * @param
     * @return
     */
    public static boolean shouldSendEmail(EmailInfo emailInfo) {
        Log.MyLog(TAG, "检测邮件是否应该发送");
        long sengTime = Util.getTimeByDate(Config.sendTime);
        if (sengTime == Constant.ERROR_CODE) {
            EmailUtil.sendMail(Config.errorSubscriberEmail, "邮件发送系统错误", emailInfo.sql + ":处理完之后，邮件的发送时间格式不对！请立即前往设置");
            return false;
        }
        if (System.currentTimeMillis() - sengTime >= 0 && emailInfo.isCreateExcelSuc && !emailInfo.isSendSuccess) {
            Log.MyLog(TAG, "检测邮件是否应该发送：true" );
            return true;

        }
        return false;
    }


    /**
     * 将对象写入到文件
     *
     * @param obj
     */
    public static void writeObjectToFile(Object obj) {
        File file = new File(Config.CACHED_SUCCESS_PATH);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取SQL执行成功的配置文件
     *
     * @return
     */
    public static Object readObjectFromFile() {
        Object temp = null;
        File file = new File(Config.CACHED_SUCCESS_PATH);
        if (!file.exists()) {
            return null;
        }
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            temp = objIn.readObject();
            objIn.close();
            System.out.println("read object success!");
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static int strToint(String num) {
        int i = 0;

        try {
            i = Integer.valueOf(num);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return i;
    }



}
