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

public class PMStation {
	private static final Logger logger = LogManager.getLogger("PMStation.class");
	
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
		File pmstation = new File(file);
		if (!pmstation.exists() || pmstation.length() == 0) {
			isStart = false;
			logger.info(pmstation + "is not exists");
			logger.info("文件不存在，不允执行");

		} else {
			logger.info(pmstation + "---exists");
			logger.info("文件齐全，准备执行");

		}

		if (isStart) {
			logger.info("==================调用程序处理站点数据 start===================");
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
					logger.info("=================处理站点数据 执行成功==========================");
					return sign;
				} else {
					logger.info("=================处理站点数据执行失败====================");
				}

			} catch (Exception ex) {
				logger.info("调用程序处理站点数据，运行程序抛出异常");
				ex.printStackTrace();
			}
			logger.info("===============PMStation finish===============");
		} else {
			logger.info("============文件不齐全，未执行=====");
		}
		return 10;

	}
	
	
	
	private static String[] getParams(String name) throws SAXException, IOException, ParserConfigurationException {
		logger.info("==================站点数据 getParams=========================");
		XMLFileUtil xml = new XMLFileUtil("url.xml");

		Map<String, String> pmStationParams = xml.getPMStationParams();

		logger.info("name:" + name);
		name = name.substring(0, name.lastIndexOf("."));

		String[] params = new String[9];
		params[0] = pmStationParams.get("executePath");
		params[1] = pmStationParams.get("input")+name+".txt";
		params[2] = pmStationParams.get("IDW_num");
		params[3] = pmStationParams.get("s_num");
		params[4] = pmStationParams.get("t_num");
		params[5] = pmStationParams.get("InterpolatedPMStationoutPath")+name+"_Interpolated"+".tif";
		/*params[6] = pmStationParams.get("PMtoutPath")+name+"_PMt"+".tif";
		params[7] = pmStationParams.get("DISoutPath")+name+"_DIS"+".tif";
		params[8] = pmStationParams.get("PMsoutPath")+name+"_PMs"+".tif";*/
		params[8] = pmStationParams.get("PMtoutPath")+name+"_PMt"+".tif";
		params[7] = pmStationParams.get("DISoutPath")+name+"_DIS"+".tif";
		params[6] = pmStationParams.get("PMsoutPath")+name+"_PMs"+".tif";
		
		
		for (int i = 0; i < params.length; i++) {
			//logger.info("params["+i+"]:"+params[i]);
		}
		logger.info("================== 站点数据getParams================end=========");
		return params;
	}
}
