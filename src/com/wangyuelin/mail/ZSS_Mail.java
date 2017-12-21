package com.wangyuelin.mail;

import com.wangyuelin.mail.conf.Config;
import com.wangyuelin.mail.conf.FileConfig;
import com.wangyuelin.mail.task.CheckTask;
import com.wangyuelin.mail.task.FileTask;
import com.wangyuelin.mail.task.SendTask;
import com.wangyuelin.mail.task.SqlTask;
import com.wangyuelin.mail.util.EmailUtil;
import com.wangyuelin.mail.util.FileUtil;
import com.wangyuelin.mail.util.TextUtil;
import com.wangyuelin.mail.util.Util;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 程序的入口
 */
public class ZSS_Mail {
	ScheduledExecutorService service;
	SqlTask sqlTask;
	SendTask sendTask;
	CheckTask checkTask;
	FileTask fileTask;
	ExecutorService executorService;
	private static ZSS_Mail zssMail;

    public static ZSS_Mail getInstance() {
        if (zssMail == null) {
            synchronized (ZSS_Mail.class) {
                if (zssMail == null) {
                    zssMail = new ZSS_Mail();
                }
            }
        }
        return zssMail;
    }




    public static void main(String[] args)  {  
        // 配置发送邮件的环境属性  
//        final Properties props = new Properties();  
//        String root = System.getProperty("user.dir");
//        try {
//			InputStream in = new BufferedInputStream (new FileInputStream(root + "/properties/mail_conf.properties"));
//			props.load(in);
//			in.close();
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        

        /*
        //发送带附件的邮件
        EmailAttachment attachment = new EmailAttachment();
        //附件的路劲
        attachment.setPath("/Users/wangyuelin/Downloads/C++学习笔记(一).pdf");
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("pdf");
        attachment.setName("C++学习笔记(一).pdf");
     // Create the email message
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("smtp.163.com");
        email.setAuthentication("wyl_coder@163.com", "kjih4321");
      try {
			email.addTo("1347248229@qq.com");
			email.setFrom("wyl_coder@163.com");
			email.setSubject("图片");
			email.setMsg("这是你想呀的图片");
			email.attach(attachment);
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
		}
		*/

        
        //测试数据库查询和csv文件的建立
//        DbItf db = new MySqlDb();
//        db.connect();
//        db.query();
        
//        String root = System.getProperty("user.dir");
//        String configPath = root + File.separator + "properties"+ File.separator +"mail_conf.properties";
//        EmailUtil.loadConfg(configPath);
//        EmailUtil.send();
    	
    	ZSS_Mail mailTest = getInstance();
    	mailTest.initConfig();
    	mailTest.clearPreCacheFile();
    	mailTest.execute();

    }  
    
    /**
     * 实现执行的策略：1.大于一个小时添加的任务，提前一个小时开始执行，并且将执行结果缓存，等待定时的时间在发送邮件
     * 2.小与1个小时添加的任务立即执行任务，并将执行 的结果缓存
     * 3.添加任务的时间超过了发送的时间也是立即执行
     */
    private void execute(){
    	
    	long now = System.currentTimeMillis();
    	
    	if(TextUtil.isEmpty(Config.sendTime)){
    		EmailUtil.sendMail(Config.errorSubscriberEmail, "邮件发送系统错误", "没有设置邮件的发送时间！！！，请立即设置");
    		return;
    	}
    	long sendTime = Util.getTimeByDate(Config.sendTime);
    	if (sendTime == Constant.ERROR_CODE) {
    		EmailUtil.sendMail(Config.errorSubscriberEmail, "邮件发送系统错误", "设置邮件的发送时间格式错误！！！，必须是yyyy-MM-dd HH:mm格式，请立即设置");
    		return;
		}
    	//判断是否离发送邮件还有1个多小时
    	
    	long left = sendTime - System.currentTimeMillis();
    	if (left <= Util.getTimeByHour(Config.aheadOfSchedule)) {//距离执行发送任务还有一个多小时，定时等到只有1个小时的时候执行, 不足一个小时则立即执行
			left = 0;
		}else  {//在距离发送邮件大于一个小时，但是设置了立即执行，也是立即执行
			if(Config.isExecuteNow){
				left = 0;//立即执行
			}
		}
		checkTask = new CheckTask();
		sqlTask = new SqlTask();
		sendTask = new SendTask();
		fileTask = new FileTask();


		long time = sendTime - System.currentTimeMillis();
		if (time < 0){
			time = 0;//立即开始检查工作
		}
		service = Executors.newSingleThreadScheduledExecutor();
		service.schedule(checkTask,time , TimeUnit.MILLISECONDS);


		executorService = Executors.newCachedThreadPool();
		executorService.execute(sqlTask);
		executorService.execute(sendTask);
		executorService.execute(fileTask);

    	
    	//还要新开一个线程，检查哪些已经查询完毕的就发送
    }
    

    /**
     * 每次进来的初始化方法，解析读取配置文件
     */
    private void initConfig(){
    	FileUtil.parseConfFile();
    	FileUtil.parseReceptionsFile();
		FileUtil.parseSQLFile();
    	
    }
    
    /**
     * 用sql语句没变化的且执行成功的cache来初始化解析得到的
     * @param
     * @return
     */
    private EmailInfo getInfoByCachedInfo(EmailInfo nowTask, List<EmailInfo> cacheList){
    	if(nowTask == null || cacheList == null){
    		return null;
    	}
    	
    	for (int i = 0; i < cacheList.size(); i++) {
    		EmailInfo cached = cacheList.get(i);
    		if(nowTask.sqlFile.equalsIgnoreCase(cached.sqlFile) && nowTask.sql.equalsIgnoreCase(cached.sql)){
    			nowTask.isCreateExcelSuc = cached.isCreateExcelSuc;
    			nowTask.isSendSuccess = cached.isSendSuccess;
    			nowTask.reaultFileName = cached.reaultFileName;
    			nowTask.reaultFilePath = cached.reaultFilePath;
    			return nowTask;
    		}
			
		}
    	return null;
    }

    /**
     * 清除之前产生的chache文件
     */
    private void clearPreCacheFile(){
        if(TextUtil.isEmpty(FileConfig.CACHE_DIR)){
            return;
        }
        File cacheDir = new File(FileConfig.CACHE_DIR);
        if (!cacheDir.exists()){
            return;
        }
        File[] childs = cacheDir.listFiles();
        int len = childs.length;
        for (int i = 0; i < len; i++){
            File child = childs[i];
            child.delete();
        }

    }

    /**
     * 退出程序
     */
    public void exits(){
        service.shutdownNow();
        executorService.shutdownNow();
        System.exit(0);

    }
    
}  