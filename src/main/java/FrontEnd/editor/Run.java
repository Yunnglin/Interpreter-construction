package FrontEnd.editor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class Run {
	public void javaRun(String src,String name)
	{
		Runtime rt = Runtime.getRuntime();
		try {
			File file = new File("D:\\run.bat");
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(
		    new OutputStreamWriter(new FileOutputStream(file), "GBK"));//开始写入批处理文件
			bw.write("cd "+src);
			bw.newLine();
			bw.write(src.substring(0, 2));
			bw.newLine();
			bw.write("cls");
			bw.newLine();
			bw.write("javac "+name+".java");
			bw.newLine();
			bw.write("java "+name);
			bw.flush();
			bw.close();
			rt.exec("cmd /c start D:\\run.bat");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void htmlRun(String src,String name)
	{
		Runtime rt = Runtime.getRuntime();
		try {
			File file = new File("D:\\run.bat");
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(
		    new OutputStreamWriter(new FileOutputStream(file), "GBK"));//开始写入批处理文件
			bw.write("cd "+src);
			bw.newLine();
			bw.write(src.substring(0, 2));
			bw.newLine();
			bw.write("cls");
			bw.newLine();
			bw.write(name+".html");
			bw.newLine();
			bw.write("exit");
			bw.flush();
			bw.close();
			rt.exec("cmd /c start D:\\run.bat");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
