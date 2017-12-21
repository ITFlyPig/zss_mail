package com.wangyuelin.mail.task;

import com.wangyuelin.mail.EmailInfo;
import com.wangyuelin.mail.Log;
import com.wangyuelin.mail.conf.Config;
import com.wangyuelin.mail.conf.FileConfig;
import com.wangyuelin.mail.db.DbItf;
import com.wangyuelin.mail.db.MySqlDb;
import com.wangyuelin.mail.util.*;

import java.io.File;
import java.util.List;

/**
 * 执行SQl任务的具体类
 * @author wangyuelin
 *
 */
public class SqlTask implements Runnable {
	private static String TAG = "SqlTask";
	public static boolean EXITS = false;

	@Override
	public void run() {
		Log.MyLog(TAG, "SqlTask开始");
		DbItf db = new MySqlDb();
		db.connect();
		while (!EXITS){
			Log.MyLog(TAG, "开始处理任务");
			EmailInfo task = Config.getOneUnhandleEmail();//有可能阻塞等待
			Log.MyLog(TAG, "开始处理任务：" + task.sqlFile);

			if (check(task)) {//开始执行任务
				Log.MyLog(TAG, "开始查询");
				List<List<Object>> result = db.query(task.sql);
				if(result == null || result.size() == 0){
					Log.MyLog(TAG, "查询的结果为空");
					EmailUtil.sendMail(Config.errorSubscriberEmail, "邮件发送系统错误", "SQL语句：" + task.sql + " 的执行结果为空，请检查SQL是否正确！！");
					continue;
				}
				Log.MyLog(TAG, "开始创建Excel文件");
				boolean success = ExcelUtil.createExcel(FileConfig.CACHE_DIR, task.reaultFileName, result);//创建文件
				task.isCreateExcelSuc = success;
				//文件太大就压缩
				if(task.isCreateExcelSuc && FileUtil.shouldZIP(FileConfig.CACHE_DIR, task.reaultFileName)){
					File srcFile = new File(FileConfig.CACHE_DIR + File.separator + task.reaultFileName);
					File[] files = new File[]{srcFile};

					String zipName = FileUtil.getFileName(task.reaultFileName) + ".zip";
					String zipPath = FileConfig.CACHE_DIR + File.separator + zipName;
					ZipFileUtil.compressFiles2Zip(files, zipPath);
					task.zipFileName = zipName;
				}

				Log.MyLog(TAG, "开始添加到处理完成队列");
				Config.addHandleEmailInfo(task);
				Log.MyLog(TAG, "处理完成，添加到完成的队列" );
			}else {
				Config.addOnlyHandleEmailInfo(task);//仅仅将处理完成的任务添加到队列中
			}


		}
	}
	
	/**
	 * 执行任务之前先检查
	 * @param task
	 */
	private boolean check(EmailInfo task){

		if(TextUtil.isEmpty(task.sql) || TextUtil.isEmpty(task.sqlFile)){
			EmailUtil.sendMail(Config.errorSubscriberEmail, "邮件发送系统错误", "要执行sql路劲为空或者解析出的sql语句为空！");
			return false;
		}
		if(task.sendTime == 0){
			EmailUtil.sendMail(Config.errorSubscriberEmail, "邮件发送系统错误", "邮件的发送时间没有设定！请立即前往设置");
			return false;
		}
		return true;
	}
	

}
