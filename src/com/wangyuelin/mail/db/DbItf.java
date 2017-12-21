package com.wangyuelin.mail.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *数据库操作的接口
 * @author wangyuelin
 *
 */
public abstract class DbItf {
	public abstract void connect();
	public abstract List<List<Object>> query(String sql);
	public abstract void disconnect();//断开连接
	
	/**
	 * 获取数据库的列的名字的集合
	 * @param rs
	 * @return
	 */
	public List<Object> getHeaderList(ResultSet rs){
		if (rs == null) {
			return null;
		}
		try {
			ResultSetMetaData data = rs.getMetaData();
			if(data == null){
				return null;
			}
			
			ArrayList<Object> clolumnList = new ArrayList<Object>();
			int columnCount = data.getColumnCount();//列的总数目
			for (int i = 1; i <= columnCount; i++) {
				String clolumnName =  data.getColumnName(i);
				clolumnList.add(clolumnName);
			}
			return clolumnList;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("获取数据库的列的名字出错");
		}
		
		return null;
	}
	
	/**
	 * 据类型获取一行中对应的值
	 * @param columnType
	 * @param rs
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	public Object getValueByType(int columnType, ResultSet rs, int index ) throws SQLException {
		Object value = null;
		switch(columnType){
		case Types.NUMERIC:
			value = rs.getLong(index);
		break;
		case Types.VARCHAR:
			value = rs.getString(index);
		break;
		case Types.DATE:
			value = rs.getDate(index);
		break;
		case Types.TIMESTAMP:
			value = rs.getTimestamp(index);
		break;
		case Types.TIME:
			value = rs.getTime(index);
		break;
		case Types.BOOLEAN:
			value = rs.getBoolean(index);
		break;
		case Types.ARRAY :
			value = rs.getArray(index);
		break;
		case Types.BIGINT :
			value = rs.getInt(index);
		break;
		case Types.BINARY:
			value = rs.getBinaryStream(index);
		break;
		case Types.BLOB:
			value = rs.getBlob(index );
		break;
		case Types.CHAR:
			value = rs.getString(index);
		break;
		case Types.INTEGER:
			value = rs.getInt(index);
		break;
		case Types.DOUBLE :
			value = rs.getDouble(index);
		break;
		case Types.FLOAT:
			value = rs.getFloat(index);
		break;
		case Types.SMALLINT:
			value = rs.getInt(index);
		break;
		case Types.DECIMAL:
			value = rs.getLong(index);
		break;
		default:
			value = rs.getObject(index);
		break;
		}
		return value;
	}

}
