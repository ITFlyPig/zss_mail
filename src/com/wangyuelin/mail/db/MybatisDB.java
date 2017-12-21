package com.wangyuelin.mail.db;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class MybatisDB extends DbItf {
	Connection con;
	Statement statement;

	@Override
	public void connect() {
		Properties props = new Properties();  
        String root = System.getProperty("user.dir");
        try {
			InputStream in = new BufferedInputStream (new FileInputStream(root + "/properties/db.properties"));
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
				statement = con.createStatement();
				
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
//		if (con == null) {
//			System.out.println("con 为空，退出");
//			return;
//			
//		}
//		
//		String root = System.getProperty("user.dir");
//		ScriptRunner runner = new ScriptRunner(con);
//		try {
//			runner.runScript(new InputStreamReader(new FileInputStream(root + File.separator +"sql" + File.separator + "test.sql" ), "UTF-8"));
//			
//		} catch (UnsupportedEncodingException | FileNotFoundException e) {
//			e.printStackTrace();
//		}
		return null;
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

}
