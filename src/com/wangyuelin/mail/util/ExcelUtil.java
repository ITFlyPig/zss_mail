package com.wangyuelin.mail.util;

import com.wangyuelin.mail.Log;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelUtil {
	private static String TAG = "ExcelUtil";

	/**
	 * 创建Excel文件
	 * @param outPutPath
	 * @param fileName
	 * @param data
	 * @throws WriteException
	 * @throws IOException
	 */
	public static boolean createExcel(String outPutPath, String fileName, List<List<Object>> data) {
		if(TextUtil.isEmpty(outPutPath) || TextUtil.isEmpty(fileName) || data == null){
			Log.MyLog(TAG, "创建Excel失败， 文件的输出路径为空，或者查询到的内容为空");
			return false;
		}
		File file = new File(outPutPath + File.separator + fileName);
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				Log.MyLog(TAG, "缓存文件创建失败");
			}
		}
		Log.MyLog(TAG, "缓存文件的位置：" + file.getAbsolutePath());
		OutputStream os;
		try {
			os = new FileOutputStream(file);
			//创建工作薄
	        WritableWorkbook workbook = Workbook.createWorkbook(os);
	        //创建新的一页
	        String curTime = Util.stampToDate(System.currentTimeMillis());
	        WritableSheet sheet = workbook.createSheet(curTime,0);
	        //创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
	        
	        int rowSize = data.size();//行的size
	        for (int i = 0; i < rowSize; i++) {
	        	List<Object> rowData = data.get(i);//每一行的数据
	        	for (int j = 0; j < rowData.size(); j++) {
	        		//创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
	        		Object cellData = rowData.get(j);
	        		Label label = new Label(j, i, cellData.toString());
	        		sheet.addCell(label);
					
				}
			}
	        //把创建的内容写入到输出流中，并关闭输出流
	        workbook.write();
	        workbook.close();
	        os.close();
	        return true;
		} catch (  IOException | WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
        
    }

}
