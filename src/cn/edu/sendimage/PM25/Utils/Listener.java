package cn.edu.sendimage.PM25.Utils;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.edu.sendimage.PM25.execute.PMStation;
import cn.edu.sendimage.PM25.execute.Reconstruct;
import cn.edu.sendimage.PM25.execute.Retrieve;
import cn.edu.sendimage.PM25.execute.Sunflower;
import cn.edu.sendimage.PM25.execute.WeatherInterpolation;
import net.contentobjects.jnotify.JNotifyListener;

public class Listener implements JNotifyListener {


	private static final Logger logger = LogManager.getLogger("Listener.class");
	
	private static int pmstation_Sign = 9;
	private static int Sunflower_sign = 9;
	private static int WeatherInterpolation_Sign = 9;
	private static int retrieve_Sign = 9;

	private static String pmstation_Name = null;
	private static String Sunflower_Name = null;
	private static String weatherInterpolation_Name = null;

	public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
		logger.info(new Date() + "renamed " + rootPath + " : " + oldName + " -> " + newName);
	}

	public void fileModified(int wd, String rootPath, String name) {
		if (name.equals("log.txt")) {
			return;
		}
		logger.info(new Date() + "modified " + rootPath + " : " + name);
		if (rootPath.contains("originalWeather")) {
			if(name.equals(weatherInterpolation_Name)) {
				System.out.println("处理过 originalWeather "+name);
			}else {
				WeatherInterpolation_Sign = WeatherInterpolation.start(name);
				weatherInterpolation_Name = name;
			}
			
		} else if (rootPath.contains("satelliteAOD")) {
			
			if(name.equals(Sunflower_Name)) {
				System.out.println("处理过satelliteAOD "+name);
			}else {
				System.out.println(Sunflower_Name);
				Sunflower_sign = Sunflower.start(name);
				logger.info("name:" + name);
				String day = name.substring(4, 12);
				String hour = name.substring(13, 17);
				String endtime = day + hour + "00";
				logger.info(endtime);
				name = Sunflower.UTC2Beijing(endtime) + ".txt";
				Sunflower_Name = name;
				System.out.println(Sunflower_Name);
			}
			
		} else if (rootPath.contains("station_PM25")) {
			if(name.equals(pmstation_Name)) {
				System.out.println("处理过 station_PM25"+name);
			}else {
				pmstation_Sign = PMStation.start(name);
				pmstation_Name = name;
			}
			
			
		}
		int sign = isRetrieve(name);
		if (sign == 0) {
			Reconstruct.start();
		}

	}

	public void fileDeleted(int wd, String rootPath, String name) {
		logger.info(new Date() + "deleted " + rootPath + " : " + name);
	}

	public void fileCreated(int wd, String rootPath, String name) {
		logger.info(new Date() + "created " + rootPath + " : " + name);

	}

	void print(String msg) {
		logger.info(msg);
	}

	// 根据三个预处理程序返回结果来判断是否反演
	private int isRetrieve(String name) {
		if (pmstation_Sign == 0 && Sunflower_sign == 0 && WeatherInterpolation_Sign == 0) {
			if (pmstation_Name.equals(Sunflower_Name) && Sunflower_Name.equals(weatherInterpolation_Name)
					&& weatherInterpolation_Name.equals(pmstation_Name)) {
				logger.info(new Date() + "==================触发retrieve========================");
				retrieve_Sign = Retrieve.start(name);
				pmstation_Sign = 9;
				Sunflower_sign = 9;
				WeatherInterpolation_Sign = 9;
				return retrieve_Sign;
			}

		}
		return 10;
	}
}