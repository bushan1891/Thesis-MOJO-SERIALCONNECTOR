package com.diegocueva.serial.datain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	public static void debug(String line){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(System.getProperty("DEBUG") != null){
			System.out.println( sdf.format(new Date()) + " " +line);
		}
	}
}
