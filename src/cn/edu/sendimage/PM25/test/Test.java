package cn.edu.sendimage.PM25.test;

import cn.edu.sendimage.PM25.Utils.FileNotify;

public class Test {

	public static void main(String[] args) {
		
		String filepath1 = "D:\\Litongwen\\PM25\\demoData\\satelliteAOD\\original";;
		String filepath2= "D:\\Litongwen\\PM25\\demoData\\retrieved_PM25_auxiliary_data\\originalWeather";
		String filepath3="D:\\Litongwen\\PM25\\demoData\\station_PM25\\observed\\txt";
		String[] strs = {filepath1,filepath2,filepath3};
		try {
			FileNotify.notify(strs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
