package cn.edu.sendimage.PM25.execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import cn.edu.sendimage.PM25.Utils.XMLFileUtil;

public class Sunflower {

	private static final Logger logger = LogManager.getLogger("Sunflower.class");
	public static int start(String name) {
		
		String[] params = null;
		try {
			//System.out.println("获取参数 ");
			params = getParams(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean isStart = true;
		
		logger.info("判断文件是否齐全");
		String file = params[6].substring(7,params[6].indexOf(":AOT"));
		//System.out.println(file);
		
		logger.info(file);
		File AOD = new File(file);
		if (!AOD.exists() || AOD.length() == 0) {
			isStart = false;
			logger.info(AOD + " is not exists");
			logger.info("文件不存在，不允执行");
		} else {
			logger.info(AOD + "---exists");
			logger.info("文件齐全，准备执行");

		}

		if (isStart) {
			logger.info("===================葵花卫星数据处理 start==================");
			try {
				logger.info(params[8]);
				String s = "D:\\PM25\\litongwen\\AODprograms\\GDAL\\bin\\gdalwarp.exe -t_srs \"+proj=latlong +datum=WGS84\" -te 112.0 28.4 116.7 32.3 -tr 0.05 0.05 -dstnodata -9999 -overwrite NETCDF:D:\\PM25\\demoData\\satelliteAOD\\original\\H08_20180702_2200_1HARP020_FLDK.02401_02401.nc:AOT D:\\PM25\\demoData\\satelliteAOD\\whc\\AOD_H08_20180702_2200_1HARP020_FLDK.02401_02401.tif";
				logger.info(s);
				Process p = Runtime.getRuntime().exec(params[8]);
				// 合并流
				SequenceInputStream sis = new SequenceInputStream(p.getInputStream(), p.getErrorStream());
				InputStreamReader isr = new InputStreamReader(sis, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while (null != (line = br.readLine())) {
					logger.info(line);
				}
				p.destroy();
				br.close();
				isr.close();

				p.waitFor();
				int sign = p.exitValue();
				if (sign == 0) {
					logger.info("===================== 葵花卫星数据处理执行成功===================");
					return sign;
				} else {
					logger.info("================= 葵花卫星数据处理执行失败==================");
				}

			} catch (Exception ex) {
				logger.info("==============葵花卫星数据处理运行程序抛出异常===================");
				ex.printStackTrace();
			}
			logger.info("=================Sunflower finish============================");
		} else {
			logger.info("==============文件不齐全，未执行==========================");
		}
		return 10;

	}

	private static String[] getParams(String name) throws SAXException, IOException, ParserConfigurationException {
		logger.info("================== 卫星数据getParams=========================");
		XMLFileUtil xml = new XMLFileUtil("url.xml");

		Map<String, String> sunflowerParams = xml.getSunflowerParams();
		
		logger.info("name:" + name);
		String day = name.substring(4, 12);
		String hour = name.substring(13,17);
		String endtime = day +hour+"00";
		logger.info(endtime);
		String time = UTC2Beijing(endtime);

		String[] params = new String[9];
		params[0] = sunflowerParams.get("executePath");
		params[1] = sunflowerParams.get("Output_coordinate_system");
		params[2] = sunflowerParams.get("Cropping_space_range");
		params[3] = sunflowerParams.get("Output_pixel_size");
		params[4] = sunflowerParams.get("Invalid_value_filling");
		params[5] = sunflowerParams.get("Whether_to_cover");
		params[6] = sunflowerParams.get("Input_file_type") + sunflowerParams.get("input") + name + sunflowerParams.get("Extract_field_name");
		System.out.println(params[6]);
		params[7] = (sunflowerParams.get("output") +  time +"_AOD"+ ".tif");

		params[8] = sunflowerParams.get("executePath") + " " + sunflowerParams.get("Output_coordinate_system") + " "
				+ sunflowerParams.get("Cropping_space_range") + " " + sunflowerParams.get("Output_pixel_size") + " "
				+ sunflowerParams.get("Invalid_value_filling") + " " + sunflowerParams.get("Whether_to_cover") + " "
				+ params[6] + " " + params[7];
		for (int i = 0; i < params.length; i++) {
			logger.info(params[i]);
		}
		
		logger.info("================== 卫星数据getParams================end=========");
		return params;
	}
	
	public static String UTC2Beijing(String utctime) {
		String year = utctime.substring(0, 4);
		String month = utctime.substring(4, 6);
		String day = utctime.substring(6, 8);
		String hour = utctime.substring(8, 10);
		String minute = utctime.substring(10, 12);
		String second = utctime.substring(12, 14);

		Calendar c1 = Calendar.getInstance();

		c1.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), Integer.parseInt(hour),
				Integer.parseInt(minute), Integer.parseInt(second));
		c1.add(Calendar.HOUR_OF_DAY, +8);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = sdf.format(c1.getTime());

		return dateString;
	}
}
