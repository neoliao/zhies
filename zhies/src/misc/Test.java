package misc;

import java.io.File;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class Test{
	
	public static String COMPANY_NAME = "深圳市中惠进出口有限公司";
	public static String COMPANY_NAME_EN = "Shenzhen Zhonghui Import and Export CO.,Ltd";
	
	public static void main(String[] args) throws Exception {
		Workbook workbook = Workbook.getWorkbook(new File("D:/demo.xls"));
		WritableWorkbook copy = Workbook.createWorkbook(new File("D:/output.xls"), workbook);
		WritableSheet sheet = copy.getSheet(0);
		WritableCell  cell = sheet.getWritableCell(1, 3);
		((Label)cell).setString("zhies");
		copy.write(); 
		copy.close();
	}
	
}


