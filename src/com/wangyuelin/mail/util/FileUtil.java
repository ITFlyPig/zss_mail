package com.wangyuelin.mail.util;

import com.wangyuelin.mail.EmailInfo;
import com.wangyuelin.mail.Log;
import com.wangyuelin.mail.conf.Config;
import com.wangyuelin.mail.conf.FileConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by wangyuelin on 2017/12/19.
 * 解析文件的工具类
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    private static int ZIP_SIZE = 2 * 1024 * 1024;//文件大小超过2M就进行压缩

    /**
     * 解析属性文件
     */
    public static void parsePropertiesFile(){

        parseConfFile();
    }

    /**
     * 解析基本配置文件
     */
    public static void parseConfFile(){
        File file = new File(FileConfig.CONF_FILE);
        if(file.exists()){
            Properties props = new Properties();
            try {
                InputStream in = new BufferedInputStream(new FileInputStream(file));
                props.load(in);
                in.close();
                //获取配置属性
                Config.aheadOfSchedule = Util.strToint(props.getProperty("ahead.time"));
                Config.errorSubscriberEmail = props.getProperty("error.email");
                Config.sendTime = props.getProperty("mail.send");
                Config.isExecuteNow = Boolean.valueOf(props.getProperty("isExecuteNow"));
                Log.MyLog(TAG, "解析配置文件得到的配置：" + "ahead.time：" + Config.aheadOfSchedule + "\n"
                          +"error.email:" + Config.errorSubscriberEmail + "\n"
                           + "mail.send:" + Config.sendTime + " \n"
                           + "isExecuteNow:" + Config.isExecuteNow);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else {
            Log.MyLog(TAG, "未找到配置文件，使用默认的值!! 解析的问价的路径：" + file.getAbsolutePath());
        }
    }

    /**
     * 解析收件人
     */
    public static void parseReceptionsFile(){
        if (TextUtil.isEmpty(FileConfig.RECEPTION_FILE)){
            return ;
        }
        File file = new File(FileConfig.RECEPTION_FILE);
        ArrayList<String> mails = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                mails.add(s);
                stringBuilder.append(s).append("\n");
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }


        Config.receiveEmails = (String[]) mails.toArray(new String[mails.size()]);
        Log.MyLog(TAG, "解析得到的联系人：" + stringBuilder.toString());

    }

    /**
     * 解析SQL文件
     */
    public static void parseSQLFile(){
        if (TextUtil.isEmpty(FileConfig.SQL_DIR)){
            return;
        }
        File sqlDir = new File(FileConfig.SQL_DIR);
        if(!sqlDir.exists()){
            return;
        }
        File[] sqlArray = sqlDir.listFiles();
        if (sqlArray == null){
            return;
        }
        for (int i = 0; i <  sqlArray.length; i++){
            File file = sqlArray[i];
            if (!file.getAbsolutePath().endsWith("sql")){
                continue;
            }
            EmailInfo emailInfo = new EmailInfo();
            emailInfo.sqlFile = file.getAbsolutePath();
            emailInfo.content = "测试";
            emailInfo.subject = "测试";
            emailInfo.emails = null;//等待解析
            emailInfo.sql = readTxtFile(file.getAbsolutePath());//等地啊解析
            emailInfo.executeTime = Util.getTimeByDate(Config.sendTime);
            emailInfo.sendTime = Util.getTimeByDate(Config.sendTime);
            emailInfo.reaultFileName = getFileName(emailInfo.sqlFile) + ".xls";
            Config.addUnhandleEmailInfo(emailInfo);
            Log.MyLog(TAG, file.getAbsolutePath() + "  解析SQL文件得到的sql语句：" + emailInfo.sql +" \n");
        }
    }

    /**
     * 解析一个文件
     */
    public static EmailInfo parsOneSQLFile(String path){
        String sql = readTxtFile(path);
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.sqlFile = path;
        emailInfo.content = "测试";
        emailInfo.subject = "测试";
        emailInfo.emails = null;//等待解析
        emailInfo.sql = sql;
        emailInfo.executeTime = Util.getTimeByDate(Config.sendTime);
        emailInfo.sendTime = Util.getTimeByDate(Config.sendTime);
        emailInfo.reaultFileName = getFileName(emailInfo.sqlFile) +  ".xls";
        return emailInfo;
    }

    /**
     * 读取txt文件
     * @param path
     * @return
     */
    public static String readTxtFile(String path){
        if (TextUtil.isEmpty(path)){
            return "";
        }
        File file = new File(path);
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }


    /**
     * 判断文件是否应该进行压缩
     * @param path
     * @param fileName
     * @return
     */
    public static boolean shouldZIP(String path, String fileName){
        if (TextUtil.isEmpty(path) || TextUtil.isEmpty(fileName)){
            return false;
        }
        File file = new File(path + File.separator + fileName);
        if (file.exists() && file.isFile() && (file.length() > ZIP_SIZE) ){
            return true;

        }
        return false;
    }



    /**
     * 得到文件的名称
     * @param path 可以使绝对路径，也可以是只有文件名
     * @return
     */
    public static String getFileName(String path){
        String fileName ="";
        if (TextUtil.isEmpty(path)){
            fileName = System.currentTimeMillis() + "";
            return fileName;
        }

        if (path.contains(File.separator)){//表示是路径
            fileName  = path.substring(path.lastIndexOf(File.separator) + 1);

        }else {
            fileName = path;
        }
        if (!TextUtil.isEmpty(fileName)){
           fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;

    }

}
