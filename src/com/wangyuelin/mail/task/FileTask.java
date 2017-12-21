package com.wangyuelin.mail.task;

import com.wangyuelin.mail.EmailInfo;
import com.wangyuelin.mail.Log;
import com.wangyuelin.mail.conf.Config;
import com.wangyuelin.mail.conf.FileConfig;
import com.wangyuelin.mail.util.FileUtil;
import com.wangyuelin.mail.util.TextUtil;

import java.io.File;
import java.nio.file.*;
import java.util.List;

/**
 * 1、没到邮件发送时间，则监听SQL文件和收件人相关文件夹变动，就刷新重新读取配置文件和sql文件
 * 2、到了邮件发送时间那就每个1分钟检测一下已经处理完的队列中是否有可以发送的邮件
 * @author wangyuelin
 *
 */
public class FileTask implements Runnable {
    private static String TAG = "FileTask";

	public static volatile boolean EXITS = false;//是否退出

	public FileTask(){

	}

	@Override
	public void run() {
	    while (!EXITS){
            Log.MyLog(TAG, "开始文件的监听：" + new File(FileConfig.SQL_DIR).getAbsolutePath());
	        monitor();
        }

	}

	/**
	 * 监听文件变化,有变化则添加到待处理队列，通知消费者处理队列
	 * @param
	 *
	 */
	private void monitor( ){
		Path sqlPath = Paths.get(FileConfig.SQL_DIR);
		Path confPath = Paths.get(FileConfig.CONF_DIR);
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			sqlPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE ,
					StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			confPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE ,
					StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

			while (!EXITS) {
			    Log.MyLog(TAG, "SQL文件夹的监听");
				WatchKey watckKey = watcher.take();
				List<WatchEvent<?>> events = watckKey.pollEvents();
				for (WatchEvent event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						System.out.println("Created: " + event.context().toString());
                        onFileChange(event.context().toString());

					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
						System.out.println("Delete: " + event.context().toString());

					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						System.out.println("Modify: " + event.context().toString());
						onFileChange(event.context().toString());
					}
				}

                boolean valid = watckKey.reset();
                if (!valid ) {
                    System. out.println("Key has been unregisterede" );
                }

            }

		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}

    /**
     * 配置文件改变，重亲读取配置文件，若是sql文件改变，重新读取sql相关信息
     * @param changeFile
     */
	private void onFileChange(String changeFile){
	    if (TextUtil.isEmpty(changeFile)){
	        return;
        }
        if (changeFile.endsWith("txt") ){//配置文件被修改
            FileUtil.parseReceptionsFile();
        }else if(changeFile.endsWith("properties")){
            FileUtil.parseConfFile();

        }else if(changeFile.endsWith("sql")){
           EmailInfo emailInfo = FileUtil.parsOneSQLFile(FileConfig.SQL_DIR + File.separator + changeFile);
           //添加到未处理队列，唤醒通知处理线程开始处理
            Config.addUnhandleEmailInfo(emailInfo);
        }
    }



}
