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

public class Reconstruct {
	
	private static final Logger logger = LogManager.getLogger("Reconstruct.class");

	public static int start() {
		String[] params = null;
		try {
			params = getParams();
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean isStart = true;
		logger.info("判断文件是否齐全");
		for (int i =0;i<4;i++) {
			if (!new File(params[i]).exists()) {
				isStart = false;
				logger.info(params[i] + " is not exists");
				break;
			} else {
				logger.info(params[i] + "---exists");
			}
		}
		
		if (isStart) {
			logger.info("================Reconstruct start=================");
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
					logger.info("==================融合重建 执行成功===================");
					return sign;
				} else {
					logger.info("执行失败");
					return 9;
				}

			} catch (Exception ex) {
				logger.info("运行程序错误");
				ex.printStackTrace();
			}
			logger.info("=======================Reconstruct finish=================");
		} else {
			logger.info("==============文件不齐全，未执行==========================");
		}
		return 9;
		
		
		
	}
	
	private static String[] getParams() throws SAXException, IOException, ParserConfigurationException {
		logger.info("================== 融合getParams=========================");
		XMLFileUtil xml = new XMLFileUtil("url.xml");

		Map<String, String> reconstructedParams = xml.getReconstructedParams();

		//String time = "2016-02-10-09";

		String[] params = new String[4];
		params[0] = reconstructedParams.get("executePath");
		params[1] = reconstructedParams.get("retrievedPath");
		params[2] = reconstructedParams.get("IDW");
		params[3] = reconstructedParams.get("output");
		logger.info("================== 融合getParams====end=====================");
		return params;
	}
}
