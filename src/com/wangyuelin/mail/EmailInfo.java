package com.wangyuelin.mail;

import java.io.Serializable;
import java.util.List;

public class EmailInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String[] emails;//要发送的联系人
	public String sql;//sql语句
	public String sqlFile;//sql文件
	public long sendTime;//发送的时间
	public long executeTime;//执行SQL的时间，没有的话默认是提前半小时执行好，获得执行的结果缓存起来
	public String reaultFilePath;//执行的结果文件路径
	public String reaultFileName;//执行的结果文件名
	public boolean isExecuteNow;//是否立即执行
	public String subject;
	public String content;
	
	public boolean isSendSuccess;//邮件是否发送成功
	public boolean isCreateExcelSuc;//创建Excel文件是否成功

}
