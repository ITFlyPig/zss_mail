package com.wangyuelin.mail.task;

import com.wangyuelin.mail.Log;
import com.wangyuelin.mail.conf.Config;

/**
 * 到邮件发送时间执行：检查有哪些邮件已经到了可以发送并且sql执行成了，有则发送
 * @author wangyuelin
 *
 */
public class CheckTask implements Runnable{
	private static final String TAG = "CheckTask";


	public static volatile boolean EXITS = false;
	private int sleepTime = 10 * 1000;//2分钟


	@Override
	public void run() {
		while (!EXITS){
//			Log.MyLog(TAG, "开始检查工作");
			Config.checkEmpty();
			try {
//				Log.MyLog(TAG, "开始休眠");
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


			//检测程序是否需要退出
//			if(Config.shouldExits()){
//				ZSS_Mail.getInstance().exits();
//			}

		}
		
		
		
	}

}
