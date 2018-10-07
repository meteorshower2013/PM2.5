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

public class Retrieve {
	
	private static final Logger logger = LogManager.getLogger("Retrieve.class");

	
	public static int start(String name) {

		String[] params = null;
		try {
			params = getParams(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean isStart = true;
		logger.info("判断文件是否齐全");
		for (int i =0;i<9;i++) {
			if (!new File(params[i]).exists()) {
				isStart = false;
				logger.info(params[i] + " is not exists");
				logger.info("文件不存在，不允执行");
				break;
			} else {
				logger.info(params[i] + "---exists");
			}
		}

		if (isStart) {
			logger.info("=====================调用反演程序 start================");
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
					logger.info("执行成功");
					logger.info("===============反演 finish====================");
					return sign;
				} else {
					logger.info("=====================调用反演程序执行失败==========");
				}

			} catch (Exception ex) {
				logger.info("====调用反演程序执行，运行程序抛出异常====");
				ex.printStackTrace();
			}
		} else {
			logger.info("==============文件不齐全，未执行==========================");
		}
		return 10;


		
	}

	private static String[] getParams(String name) throws SAXException, IOException, ParserConfigurationException {
		logger.info("====================反演程序获取参数===start=====================");
		XMLFileUtil xml = new XMLFileUtil("url.xml");

		Map<String, String> retrivedParams = xml.getRetrivedParams();

		//String time = "2016-02-10-09";
		name = name.substring(0,name.lastIndexOf("."));
		
		String[] params = new String[10];
		params[0] = retrivedParams.get("executePath");
		params[1] = retrivedParams.get("whc") + name + "_AOD.tif";
		params[2] = retrivedParams.get("RH") + name + "_RH.tif";
		params[3] = retrivedParams.get("WS") + name + "_WS.tif";
		params[4] = retrivedParams.get("TEM") + name + "_TEM.tif";
		//params[5] = retrivedParams.get("PBL") + name + "_PBL.tif";
		params[5] = retrivedParams.get("PRS") + name + "_PRS.tif";
		params[6] = retrivedParams.get("PMs") + name + "_PMs.tif";
		params[7] = retrivedParams.get("PMt") + name + "_PMt.tif";
		params[8] = retrivedParams.get("DIS") + name + "_DIS.tif";
		params[9] = retrivedParams.get("output") + name + "_retrieved.tif";
		logger.info("====================反演程序获取参数 ==end========================");
		return params;
	}

}
