package com.wangyuelin.mail.conf;

import com.wangyuelin.mail.EmailInfo;
import com.wangyuelin.mail.Log;
import com.wangyuelin.mail.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Config {
	private static String TAG = "Config";

	public static volatile boolean isExecuteNow;//是否是立即执行，从配置文件读取
	public static volatile int aheadOfSchedule = 1;//提前多久执行，从配置文件读取，单位是小时hour，默认提前一小时
	public static volatile String sendTime;//格式必须是yyyy-MM-dd HH:mm
	public static volatile String errorSubscriberEmail;//接收错误邮件的订阅者

	public static volatile String CACHED_SUCCESS_PATH = "./result_cache/result.txt";//缓存执行结果的路劲
	public static volatile List<EmailInfo> emailTasks = new ArrayList<>();//等待执行的sql查询任务
	public static volatile List<EmailInfo> unhandleTasks = new ArrayList<>();//等待执行的sql查询任务
	public static volatile List<EmailInfo> handleTasks = new ArrayList<>();//已经执行的sql查询任务

    public  static volatile String[] receiveEmails;//邮件的接受者






	/**
	 * 往未处理的队列中添加任务
	 * @param emailInfo
	 */
	public static void addUnhandleEmailInfo(EmailInfo emailInfo){
		synchronized (unhandleTasks){
			unhandleTasks.add(emailInfo);
			unhandleTasks.notify();
		}

	}

	/**
	 * 删除
	 * @param emailInfo
	 */
	public static void removeUnhandleEmailInfo(EmailInfo emailInfo){
		synchronized (unhandleTasks){
			for (int i = 0; i < unhandleTasks.size(); i++){
				EmailInfo unhandle = unhandleTasks.get(i);
				if(unhandle.sqlFile.equalsIgnoreCase(emailInfo.sqlFile)){
					unhandleTasks.remove(i);
				}
			}
		}

	}

    /**
     * 获取一个未处理的任务
     * @return
     */
	public static EmailInfo getOneUnhandleEmail(){

        synchronized (unhandleTasks){
            while ( unhandleTasks.size() == 0){
                try {
                    unhandleTasks.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            EmailInfo emailInfo = unhandleTasks.get(0);
            unhandleTasks.remove(0);
            return emailInfo;
        }

    }

	/**
	 * 添加到已处理的队列
	 * @param emailInfo
	 */
	public static void addHandleEmailInfo(EmailInfo emailInfo){
		synchronized (handleTasks){
			handleTasks.add(emailInfo);
			Log.MyLog(TAG, "addHandleEmailInfo方法将任务添加到已处理完成的队列中，并开始唤醒");
			handleTasks.notify();
			Log.MyLog(TAG, "addHandleEmailInfo方法将任务添加到已处理完成的队列中，唤醒完成");
		}

	}
    /**
     * 添加到已处理的队列,仅仅是添加，不唤醒
     * @param emailInfo
     */
    public static void addOnlyHandleEmailInfo(EmailInfo emailInfo){
        synchronized (handleTasks){
            handleTasks.add(emailInfo);
        }

    }

	/**
	 * 从已处理队列中删除
	 * @param emailInfo
	 */
	public static void removeHandleEmailInfo(EmailInfo emailInfo){
		synchronized (handleTasks){
			for (int i = 0; i < handleTasks.size(); i++){
				EmailInfo handle = handleTasks.get(i);
				if(handle.sqlFile.equalsIgnoreCase(emailInfo.sqlFile)){
					handleTasks.remove(i);
				}
			}
		}

	}


    /**
     * 获取一个已处理的任务
     * @return
     */
    public static EmailInfo getOneHandleEmail(){

        synchronized (handleTasks){

            boolean isHaveShouldSendMail = false;
            for (int i = 0; i < handleTasks.size(); i++){
                EmailInfo emailInfo = handleTasks.get(i);
                if (Util.shouldSendEmail(emailInfo)){
                    isHaveShouldSendMail = true;
                    break;
                }
            }
            Log.MyLog(TAG, "已处理完成队列中是否有应该发送的邮件：" + isHaveShouldSendMail + " 已处理队列size：" + handleTasks.size());


            while ( handleTasks.size() == 0 || !isHaveShouldSendMail ){

                try {
                    handleTasks.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

				for (int i = 0; i < handleTasks.size(); i++){//被唤醒之后在检查一次
					EmailInfo emailInfo = handleTasks.get(i);
					if (Util.shouldSendEmail(emailInfo)){
						isHaveShouldSendMail = true;
						break;
					}
				}
            }
			Log.MyLog(TAG, "开始删除一个已处理队列中的任务，并将其发送");
			EmailInfo emailInfo = handleTasks.get(0);
            handleTasks.remove(0);
            return emailInfo;
        }

    }


	/**
	 * 检查处理完成队列中是否有应该发送的邮件
	 * @return
	 */
	public static boolean isHaveShouldSendMail(){
    	synchronized (handleTasks){
			boolean isHaveShouldSendMail = false;
			for (int i = 0; i < handleTasks.size(); i++){
				EmailInfo emailInfo = handleTasks.get(i);
				if (Util.shouldSendEmail(emailInfo)){
					isHaveShouldSendMail = true;
					break;
				}
			}
			return isHaveShouldSendMail;
		}


	}

	/**
	 * 检查是否为空，是否应该通知
	 */
	public static void checkEmpty(){
		synchronized (handleTasks){
			if (handleTasks.size() > 0){
				Log.MyLog(TAG, "队列中有任务，唤醒");
				handleTasks.notify();
			}
		}
	}

	/**
	 * 是否应该退出程序
	 * 如果待处理和处理完成的队列中的任务都没有了，且超过邮件定时的发送的时间10分钟了，就退出
	 * @return
	 */
	public static boolean shouldExits(){
		if (unhandleTasks.size() == 0 && handleTasks.size() == 0
				&& (System.currentTimeMillis() - Util.getTimeByDate(Config.sendTime)) > 10 * 60 * 1000){
			return true;
		}
		return false;

	}


}
