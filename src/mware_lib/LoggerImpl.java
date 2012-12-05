package mware_lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class LoggerImpl {
	
	private static boolean saveToFile = true;
	private static String packagename = LoggerImpl.class.getName().substring(0, LoggerImpl.class.getClass().getName().lastIndexOf("."));
	private static File file = new File("."+File.separator+packagename+".log");
	
	
	public static void info(String className, String log){
		log(className+" \n\t INFO: "+log);
	}
	
	public static void warning(String className, String log){
		log(className+" \n\t WARNING: "+log);
	}
	
	public static void error(String className, String log){
		log(className+" \n\t ERROR: "+log);
	}
	
	private static synchronized void log(String log){
		Timestamp t = new Timestamp(System.currentTimeMillis());
		String temp = t.toString()+" "+log;
		System.out.println(temp);
		if(saveToFile){
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(file,true));
				out.write(temp);
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
