package cn.edu.sendimage.PM25.execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import cn.edu.sendimage.PM25.Utils.XMLFileUtil;

public class WeatherInterpolation {
	private static final Logger logger = LogManager.getLogger("WeatherInterpolation.class");
	public static int start(String name) {
		String[] params = null;
		try {
			params = getParams(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean isStart = true;
		
		logger.info("判断文件是否齐全");
		
		String file = (String) params[1];
		logger.info(file);
		File weather = new File(file);
		if (!weather.exists() || weather.length() == 0) {
			isStart = false;
			logger.info(weather + "is not exists");
			logger.info("文件不存在，不允执行");
		} else {
			logger.info(weather + "---exists");
			logger.info("文件齐全，准备执行");
		}

		if (isStart) {
			logger.info("===================调用WeatherInterpolation start=================");
			try {
				
				Process p = Runtime.getRuntime().exec(params);
				// 合并流
				SequenceInputStream sis = new SequenceInputStream(p.getInputStream(), p.getErrorStream());
				InputStreamReader isr = new InputStreamReader(sis, "GBK");
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
					logger.info("=====================WeatherInterpolation执行成功============================");
					return sign;
				} else {
					logger.info("=====================WeatherInterpolation执行失败============================");
				}

			} catch (Exception ex) {
				logger.info("运行程序抛出异常");
				ex.printStackTrace();
			}
			logger.info("==================WeatherInterpolation finish======================");
		} else {
			logger.info("参数错误");
		}
		return 10;

	}

	private static String[] getParams(String name) throws SAXException, IOException, ParserConfigurationException {
		logger.info("================== 气象数据getParams=========================");
		XMLFileUtil xml = new XMLFileUtil("url.xml");

		Map<String, String> weatherInterpolationParams = xml.getWeatherInterpolationParams();

		logger.info("name:" + name);
		name = name.substring(0, name.lastIndexOf("."));

		String[] params = new String[7];
		params[0] = weatherInterpolationParams.get("executePath");
		params[1] = weatherInterpolationParams.get("input")+name+".txt";
		params[2] = weatherInterpolationParams.get("StationNum");
		params[3] = weatherInterpolationParams.get("WSoutPath")+name+"_WS"+".tif";
		params[4] = weatherInterpolationParams.get("TEMoutPath")+name+"_TEM"+".tif";
		params[5] = weatherInterpolationParams.get("RHoutPath")+name+"_RH"+".tif";
		params[6] = weatherInterpolationParams.get("PRSoutPah")+name+"_PRS"+".tif";
		
		
		for (int i = 0; i < params.length; i++) {
			logger.info("params["+i+"]:"+params[i]);
		}
		logger.info("================== 气象数据getParams================end=========");
		return params;
	}
}
