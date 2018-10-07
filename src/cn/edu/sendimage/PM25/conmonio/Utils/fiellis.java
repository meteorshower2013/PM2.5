package cn.edu.sendimage.PM25.conmonio.Utils;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.edu.sendimage.PM25.execute.Accuracy;
import cn.edu.sendimage.PM25.execute.AccuracyReconstruct;
import cn.edu.sendimage.PM25.execute.PMStation;
import cn.edu.sendimage.PM25.execute.Reconstruct;
import cn.edu.sendimage.PM25.execute.Retrieve;
import cn.edu.sendimage.PM25.execute.Sunflower;
import cn.edu.sendimage.PM25.execute.WeatherInterpolation;

final class conmonioFileListener implements FileAlterationListener {

	private static final Logger logger = LogManager.getLogger("conmonioFileListener.class");

	private static int pmstation_Sign = 9;
	private static int Sunflower_sign = 9;
	private static int WeatherInterpolation_Sign = 9;
	private static int retrieve_Sign = 9;

	private static String pmstation_Name = null;
	private static String Sunflower_Name = null;
	private static String weatherInterpolation_Name = null;

	@Override
	public void onStart(FileAlterationObserver observer) {
		//System.out.println(observer + "monitor start scanning.." + System.currentTimeMillis());

	}

	@Override
	public void onDirectoryCreate(File directory) {
		System.out.println(directory.getName() + " directory created.");

	}

	@Override
	public void onDirectoryChange(File directory) {
		System.out.println(directory.getAbsolutePath() + " directory change.");

	}

	@Override
	public void onDirectoryDelete(File directory) {
		System.out.println(directory.getName() + " directory delete.");

	}

	@Override
	public void onFileCreate(File file) {
		String rootPath = file.getParent();
		String name = file.getName();
		logger.info("监听到有文件创建");
		logger.info(file.getAbsolutePath() + " created.");
		if (name.equals("log.txt")) {
			return;
		}

		if (rootPath.contains("originalWeather")) {
			logger.info("中国气象局数据文件");
			WeatherInterpolation_Sign = WeatherInterpolation.start(name);
			weatherInterpolation_Name = name;

		} else if (rootPath.contains("satelliteAOD")) {
			logger.info("卫星数据文件");
			try {
				logger.info("****线程等待30秒，确保数据下载完毕******");
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Sunflower_sign = Sunflower.start(name);
			
			logger.info("截取葵花数据时间并转化为中国时间，为反演做准备");
			logger.info("name:" + name);
			String day = name.substring(4, 12);
			String hour = name.substring(13, 17);
			String endtime = day + hour + "00";
			logger.info(endtime);
			name = Sunflower.UTC2Beijing(endtime) + ".txt";
			Sunflower_Name = name;
			//System.out.println(Sunflower_Name);

		} else if (rootPath.contains("station_PM25")) {
			logger.info("站点数据文件");
			pmstation_Sign = PMStation.start(name);
			pmstation_Name = name;

		}
		int sign = isRetrieve(name);
		if (sign == 0) {
			logger.info("==============触发重建融合======================");
			int isaccuracy = Reconstruct.start();
			if(isaccuracy == 0) {
				logger.info("==============触发反演精度评价======================");
				Accuracy.start(name);
				logger.info("==============触发重建精度评价======================");
				AccuracyReconstruct.start(name);
			}
		}

	}

	@Override
	public void onFileChange(File file) {
		//System.out.println(file.getName() + " changed.");

	}

	@Override
	public void onFileDelete(File file) {
		System.out.println(file.getName() + " deleted.");

	}

	@Override
	public void onStop(FileAlterationObserver observer) {
		//System.out.println(observer + "monitor stop scanning.." + System.currentTimeMillis());

	}

	// 根据三个预处理程序返回结果来判断是否反演
	private int isRetrieve(String name) {
		logger.info("====判断是否可以反演========================");
		if (pmstation_Sign == 0 && Sunflower_sign == 0 && WeatherInterpolation_Sign == 0) {
			logger.info("三个数据预处理程序正常运行");
			if (pmstation_Name.equals(Sunflower_Name) && Sunflower_Name.equals(weatherInterpolation_Name)
					&& weatherInterpolation_Name.equals(pmstation_Name)) {
				logger.info("三个预处理文件是同一小时发生");
				logger.info(new Date() + "==================触发反演========================");
				retrieve_Sign = Retrieve.start(name);
				pmstation_Sign = 9;
				Sunflower_sign = 9;
				WeatherInterpolation_Sign = 9;
				return retrieve_Sign;
			}
			else {
				logger.info("三个预处理文件不是同一小时发生，不执行反演");
			}

		}else {
			if(pmstation_Sign!= 0) {
				logger.info("站点程序未准备就绪");
			}
			if(Sunflower_sign!= 0) {
				logger.info("葵花程序未准备就绪");
			}
			if(WeatherInterpolation_Sign!= 0) {
				logger.info("气象程序未准备就绪");
			}
		}
		return 10;
	}

}
