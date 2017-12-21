package com.wangyuelin.mail.conf;

import java.io.File;

/**
 * 文件路径的配置
 * @author wangyuelin
 *
 */
public class FileConfig {
	public static String CONF_DIR = "./conf";//配置文件的文件夹,有默认路径
	//文件夹
	public static String SQL_DIR = "./sql";//SQL文件的存放文件夹
	//文件
	public static String RECEPTION_FILE = CONF_DIR + File.separator + "receptions.txt";//收件人信息的文件路径
	public static String CONF_FILE = CONF_DIR + File.separator + "conf.properties";//基本配置文件的路径
	public static String DB_FILE = CONF_DIR + File.separator + "db.properties";//数据库文件的路劲
	public static String FILE_CONF = CONF_DIR + File.separator + "file.properties";//文件的路径的配置文件
	public static String MAIL_FILE = CONF_DIR + File.separator + "mail_conf.properties";//邮件的配置文件的路劲

	public static String CACHE_DIR = "./cache";
	



	
	
	

}
