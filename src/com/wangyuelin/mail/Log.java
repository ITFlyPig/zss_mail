package com.wangyuelin.mail;

public class Log {
	/**
	 * 打印log
	 * @param tag
	 * @param text
	 */
	public static void MyLog(String tag, String text){
		if (tag == null) {
			tag = "";
		}
		if (text == null) {
			text = "";
		}
		System.out.println(tag + ": " + text);
	}
	
	/**
	 * 将错误发送给订阅者，方便及时处理错误
	 * @param error
	 */
	public static void sendErrorLog(String error){
		
		
	}
	

}
