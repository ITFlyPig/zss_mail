package com.wangyuelin.mail.db;

import com.wangyuelin.mail.conf.FileConfig;
import com.wangyuelin.mail.util.TextUtil;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * 数据库连接的工具类
 * @author wangyuelin
 *
 */
public class MySqlDb extends DbItf {
	Connection con;
	Statement statement;
	

	/**
	 * 连接数据库
	 */
	@Override
	public void connect(){
		Properties props = new Properties();  
        try {
			InputStream in = new BufferedInputStream (new FileInputStream(FileConfig.DB_FILE));
			props.load(in);
			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String driver = props.getProperty("db.driver");
		String url = props.getProperty("db.url");
		String user = props.getProperty("db.user");
		String passsord = props.getProperty("db.pwd");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url,user,passsord);
			if (!con.isClosed()) {
				System.out.println("Succeeded connecting to the Database!");
				
				
			}
		} catch (ClassNotFoundException e) {
			 //数据库驱动类异常处理
			 System.out.println("Sorry,can`t find the Driver!");   
			 
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//数据库连接失败异常处理
			System.out.println("SQL异常");  
		}
		
	}


	@Override
	public List<List<Object>> query(String sql) {
		if (TextUtil.isEmpty(sql)) {
			System.out.println("sql语句 为空");
			return null;
		}
		
		try {
			statement = con.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			if (statement == null) {
				System.out.println("statement 为空");
				return null;
			}
			ResultSet rs = null;
			try {
				rs = statement.executeQuery(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (rs == null) {
				System.out.println("ResultSet 为空");
				return null;
			}
			//获取每列的列名
			List<Object> headers = getHeaderList(rs);
			if (headers == null) {
				System.out.println("获取每列的列名出错");
				return null;
				
			}
			//获取每行的值
			List<List<Object>> contentList = new ArrayList<List<Object>>();
			contentList.add(headers);//添加头部信息
			try {
				while (rs.next()) {
					//获取一行的值
					ResultSetMetaData data = rs.getMetaData();
					int clonumnCount = data.getColumnCount();
					List<Object> rowDataList = new ArrayList<Object>();
					for (int i = 1; i <= clonumnCount; i++) {
						Object valueObject = getValueByType(data.getColumnType(i), rs, i);
						if(valueObject != null){
							rowDataList.add(valueObject);
						}
					}
					contentList.add(rowDataList);
					
				}
			} catch (SQLException e) {
				System.out.println("SQL查询异常");
				e.printStackTrace();
			}finally {
				try {
					
					if (statement != null) {
						statement.close();
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return contentList;
		
	}


	@Override
	public void disconnect() {
		if(con != null){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	

}
