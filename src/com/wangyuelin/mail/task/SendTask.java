package com.wangyuelin.mail.task;

import com.wangyuelin.mail.EmailInfo;
import com.wangyuelin.mail.Log;
import com.wangyuelin.mail.conf.Config;
import com.wangyuelin.mail.conf.FileConfig;
import com.wangyuelin.mail.util.EmailUtil;

/**
 * Created by wangyuelin on 2017/12/19.
 * 不断的检查已处理的队列中是否有等待发送的邮件
 */
public class SendTask implements Runnable {
    private static String TAG = "SendTask";
    public static boolean EXITS = false;

    @Override
    public void run() {
        Log.MyLog(TAG, "SendTask开始");
        while (!EXITS){
            Log.MyLog(TAG, "开始发送邮件的任务");
            EmailInfo emailInfo = Config.getOneHandleEmail();
            Log.MyLog(TAG, "开始发送邮件的任务：" + emailInfo.sqlFile);
                boolean result = EmailUtil.sendAttachmentEmail(FileConfig.CACHE_DIR, emailInfo.reaultFileName,emailInfo.subject, emailInfo.content,  Config.receiveEmails);//发送邮件
                emailInfo.isSendSuccess = result;//记录邮件的发送结果
                Log.MyLog(TAG, "邮件发送的结果：" + emailInfo.isSendSuccess);

//            checkExits();

        }
    }

    /**
     * 检测系统是否应该退出
     */
    private void checkExits(){
        if ((Config.handleTasks == null || Config.handleTasks.size() == 0) && (Config.unhandleTasks == null || Config.unhandleTasks.size() == 0)){
            CheckTask.EXITS = true;
            SqlTask.EXITS = true;
            SendTask.EXITS = true;
            FileTask.EXITS = true;
        }

    }
}
