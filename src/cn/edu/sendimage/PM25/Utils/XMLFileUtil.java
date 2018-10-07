package cn.edu.sendimage.PM25.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLFileUtil {
	private static final Logger logger = LogManager.getLogger("XMLFileUtil.class");

	private static Document document;

	public XMLFileUtil(String path) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = factory.newDocumentBuilder();
		XMLFileUtil.document = db.parse(path);
	}

	public List<String> getUrl() throws Exception {

		NodeList nodelist = document.getElementsByTagName("baseUrl");
		List<String> urlList = new ArrayList<String>();
		StringBuffer url = new StringBuffer("");
		String baseUrl = (nodelist.item(0).getFirstChild().getNodeValue());
		url.append(baseUrl);
		// logger.info(url);
		NodeList params = document.getElementsByTagName("parameters");
		NodeList paramsChildNodes = params.item(0).getChildNodes();
		NodeList staIDs = null;
		if (paramsChildNodes != null && paramsChildNodes.getLength() > 0) {
			url.append("?");
			for (int i = 0, len = paramsChildNodes.getLength(); i < len; i = i + 1) {
				// System.out.print(paramsChildNodes.item(i).getNodeName());
				// logger.info("---length:" +
				// paramsChildNodes.item(i).getChildNodes().getLength());
				if (i % 2 != 0) {
					Node node = paramsChildNodes.item(i);

					if (node.getChildNodes().getLength() == 1) {
						if (i == 1) {
							url.append(node.getNodeName()).append("=").append(node.getFirstChild().getNodeValue());
						} else {
							url.append("&");
							url.append(node.getNodeName()).append("=").append(node.getFirstChild().getNodeValue());
						}

					} else {
						if (node.getNodeName().equals("elements")) {
							url.append("&");
							url.append(node.getNodeName()).append("=");
							NodeList elements = node.getChildNodes();
							for (int j = 0, length = elements.getLength(); j < length; j = j + 1) {
								logger.info("Elements:-" + j + "-" + elements.item(j).getNodeName());
								if (j % 2 != 0) {
									Node element = elements.item(j);

									if (j == length - 2) {
										url.append(element.getFirstChild().getNodeValue());
									} else {
										url.append(element.getFirstChild().getNodeValue() + ",");
									}
								}
							}
						}
						if (paramsChildNodes.item(i).getNodeName().equals("staIDs")) {
							staIDs = paramsChildNodes.item(i).getChildNodes();
						}
					}

				}
				logger.info(url);
			}

			logger.info("==================================================");
			for (int j = 0, length = staIDs.getLength(); j < length; j = j + 1) {
				if (j % 2 != 0) {
					Node station = staIDs.item(j);
					// logger.info("nihao");
					// logger.info(station.getFirstChild().getNextSibling().getFirstChild().getNodeValue());
					logger.info(
							url + "&staIDs=" + station.getFirstChild().getNextSibling().getFirstChild().getNodeValue());
					urlList.add(
							url + "&staIDs=" + station.getFirstChild().getNextSibling().getFirstChild().getNodeValue());
				}
			}

		}

		return urlList;
	}

	public String[] getLongitudeAndLatitudeByStationId(String id) {
		String[] arr = new String[2];
		NodeList stationIds = document.getElementsByTagName("id");
		for (int i = 0, len = stationIds.getLength(); i < len; i++) {
			if (id.equals(stationIds.item(i).getFirstChild().getNodeValue())) {
				// logger.info(stationIds.item(i).getNextSibling().getNextSibling().getFirstChild().getNodeValue());
				String longitude = stationIds.item(i).getNextSibling().getNextSibling().getFirstChild().getNodeValue();
				String latitude = stationIds.item(i).getNextSibling().getNextSibling().getNextSibling().getNextSibling()
						.getFirstChild().getNodeValue();
				// logger.info("longitude:" + longitude);
				// logger.info("latitude:" + latitude);
				arr[0] = longitude;
				arr[1] = latitude;
			}

		}

		return arr;
	}

	public String getdownloadPath() {
		String downfilePath = document.getElementsByTagName("downFilePath").item(0).getFirstChild().getNodeValue();
		return downfilePath;
	}

	public Map<String, String> getRetrivedParams() {
		Map<String, String> map = new HashMap<String, String>();
		Node retrievedNode = document.getElementsByTagName("retrieved").item(0);
		NodeList retrievedNodeList = retrievedNode.getChildNodes();
		for (int i = 0, len = retrievedNodeList.getLength(); i < len; i++) {
			retrievedNodeList.item(i);
			logger.info(retrievedNodeList.item(i).getNodeName());

			if ("executePath".equals(retrievedNodeList.item(i).getNodeName())) {
				String exePath = retrievedNodeList.item(i).getFirstChild().getNodeValue().trim();
				map.put(retrievedNodeList.item(i).getNodeName(), exePath);
			}
			if ("output".equals(retrievedNodeList.item(i).getNodeName())) {
				String output = retrievedNodeList.item(i).getFirstChild().getNodeValue().trim();
				map.put("output", output);
				logger.info(map);
			}
		}
		String whc = document.getElementsByTagName("whc").item(0).getFirstChild().getNodeValue().trim();
		map.put("whc", whc);
		String RH = document.getElementsByTagName("RH").item(0).getFirstChild().getNodeValue().trim();
		map.put("RH", RH);
		String WS = document.getElementsByTagName("WS").item(0).getFirstChild().getNodeValue().trim();
		map.put("WS", WS);
		String TEM = document.getElementsByTagName("TEM").item(0).getFirstChild().getNodeValue().trim();
		map.put("TEM", TEM);
		String PBL = document.getElementsByTagName("PBL").item(0).getFirstChild().getNodeValue().trim();
		map.put("PBL", PBL);
		String PRS = document.getElementsByTagName("PRS").item(0).getFirstChild().getNodeValue().trim();
		map.put("PRS", PRS);
		String PMs = document.getElementsByTagName("PMs").item(0).getFirstChild().getNodeValue().trim();
		map.put("PMs", PMs);
		String PMt = document.getElementsByTagName("PMt").item(0).getFirstChild().getNodeValue().trim();
		map.put("PMt", PMt);
		String DIS = document.getElementsByTagName("DIS").item(0).getFirstChild().getNodeValue().trim();
		map.put("DIS", DIS);

		logger.info(map);
		return map;
	}

	public Map<String, String> getReconstructedParams() {
		Map<String, String> map = new HashMap<String, String>();
		Node reconstructedNode = document.getElementsByTagName("reconstructed").item(0);
		NodeList reconstructedNodeList = reconstructedNode.getChildNodes();
		for (int i = 0, len = reconstructedNodeList.getLength(); i < len; i++) {
			// reconstructedNodeList.item(i);
			logger.info(reconstructedNodeList.item(i).getNodeName());

			if ("executePath".equals(reconstructedNodeList.item(i).getNodeName())) {
				String exePath = reconstructedNodeList.item(i).getFirstChild().getNodeValue().trim();
				map.put(reconstructedNodeList.item(i).getNodeName(), exePath);
			}
			if ("output".equals(reconstructedNodeList.item(i).getNodeName())) {
				String output = reconstructedNodeList.item(i).getFirstChild().getNodeValue().trim();
				map.put("output", output);
				logger.info(map);
			}
		}
		String retrievedPath = document.getElementsByTagName("retrievedPath").item(0).getFirstChild().getNodeValue()
				.trim();
		map.put("retrievedPath", retrievedPath);
		String IDW = document.getElementsByTagName("IDW").item(0).getFirstChild().getNodeValue().trim();
		map.put("IDW", IDW);

		return map;
	}

	public Map<String, String> getSunflowerParams() {
		Map<String, String> map = new HashMap<String, String>();
		Node sunflowerNode = document.getElementsByTagName("sunflower").item(0);
		NodeList sunflowerNodeList = sunflowerNode.getChildNodes();
		for (int i = 0, len = sunflowerNodeList.getLength(); i < len; i++) {
			// reconstructedNodeList.item(i);
			//logger.info(sunflowerNodeList.item(i).getNodeName());

			if ("executePath".equals(sunflowerNodeList.item(i).getNodeName())) {
				String exePath = sunflowerNodeList.item(i).getFirstChild().getNodeValue().trim();
				map.put(sunflowerNodeList.item(i).getNodeName(), exePath);
			}
			if ("output".equals(sunflowerNodeList.item(i).getNodeName())) {
				String output = sunflowerNodeList.item(i).getFirstChild().getNodeValue().trim();
				map.put("output", output);
				//logger.info(map);
			}
			if ("input".equals(sunflowerNodeList.item(i).getNodeName())) {
				String output = sunflowerNodeList.item(i).getFirstChild().getNodeValue().trim();
				map.put("input", output);
				//logger.info(map);
			}
		}

		String Output_coordinate_system = document.getElementsByTagName("Output_coordinate_system").item(0)
				.getFirstChild().getNodeValue().trim();
		map.put("Output_coordinate_system", Output_coordinate_system);
		String Cropping_space_range = document.getElementsByTagName("Cropping_space_range").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("Cropping_space_range", Cropping_space_range);
		String Output_pixel_size = document.getElementsByTagName("Output_pixel_size").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("Output_pixel_size", Output_pixel_size);
		String Invalid_value_filling = document.getElementsByTagName("Invalid_value_filling").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("Invalid_value_filling", Invalid_value_filling);
		String Whether_to_cover = document.getElementsByTagName("Whether_to_cover").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("Whether_to_cover", Whether_to_cover);
		String Input_file_type = document.getElementsByTagName("Input_file_type").item(0).getFirstChild().getNodeValue()
				.trim();
		map.put("Input_file_type", Input_file_type);
		String Extract_field_name = document.getElementsByTagName("Extract_field_name").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("Extract_field_name", Extract_field_name);
		//logger.info(map);
		return map;
	}

	public Map<String, String> getWeatherInterpolationParams() {
		Map<String, String> map = new HashMap<String, String>();
		Node interpolation = document.getElementsByTagName("Interpolation").item(0);
		NodeList interpolationNodeList = interpolation.getChildNodes();
		for (int i = 0, len = interpolationNodeList.getLength(); i < len; i++) {
			// reconstructedNodeList.item(i);
			//logger.info(interpolationNodeList.item(i).getNodeName());

			if ("executePath".equals(interpolationNodeList.item(i).getNodeName())) {
				String exePath = interpolationNodeList.item(i).getFirstChild().getNodeValue().trim();
				map.put(interpolationNodeList.item(i).getNodeName(), exePath);
			}
			
			if ("input".equals(interpolationNodeList.item(i).getNodeName())) {
				String input = interpolationNodeList.item(i).getFirstChild().getNodeValue().trim();
				map.put("input", input);
				//logger.info(map);
			}
		}
		
		String WSoutPath = document.getElementsByTagName("WSoutPath").item(0)
				.getFirstChild().getNodeValue().trim();
		map.put("WSoutPath", WSoutPath);
		String TEMoutPath = document.getElementsByTagName("TEMoutPath").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("TEMoutPath", TEMoutPath);
		String RHoutPath = document.getElementsByTagName("RHoutPath").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("RHoutPath", RHoutPath);
		String PRSoutPah = document.getElementsByTagName("PRSoutPah").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("PRSoutPah", PRSoutPah);
		String StationNum = document.getElementsByTagName("StationNum").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("StationNum", StationNum);
		
		return map;
	}
	
	public Map<String, String> getPMStationParams(){
		Map<String, String> map = new HashMap<String, String>();
		Node PMStation = document.getElementsByTagName("PMStation").item(0);
		NodeList PMStationList = PMStation.getChildNodes();
		for (int i = 0, len = PMStationList.getLength(); i < len; i++) {
			
			logger.info(PMStationList.item(i).getNodeName());

			if ("executePath".equals(PMStationList.item(i).getNodeName())) {
				String exePath = PMStationList.item(i).getFirstChild().getNodeValue().trim();
				map.put(PMStationList.item(i).getNodeName(), exePath);
			}
			
			if ("input".equals(PMStationList.item(i).getNodeName())) {
				String input = PMStationList.item(i).getFirstChild().getNodeValue().trim();
				map.put("input", input);
				//logger.info(map);
			}
		}
		
		//outPath
		String InterpolatedPMStationoutPath = document.getElementsByTagName("InterpolatedPMStationoutPath").item(0)
				.getFirstChild().getNodeValue().trim();
		map.put("InterpolatedPMStationoutPath", InterpolatedPMStationoutPath);
		String DISoutPath = document.getElementsByTagName("DISoutPath").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("DISoutPath", DISoutPath);
		String PMsoutPath = document.getElementsByTagName("PMsoutPath").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("PMsoutPath", PMsoutPath);
		String PMtoutPath = document.getElementsByTagName("PMtoutPath").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("PMtoutPath", PMtoutPath);
		
		//params
		String IDW_num = document.getElementsByTagName("IDW_num").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("IDW_num", IDW_num);
		String s_num = document.getElementsByTagName("s_num").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("s_num", s_num);
		String t_num = document.getElementsByTagName("t_num").item(0).getFirstChild()
				.getNodeValue().trim();
		map.put("t_num", t_num);
		
		return map;
	}
	
	public Map<String, String> getAccuracyParams(){
		Map<String, String> map = new HashMap<String, String>();
		Node Accuracy = document.getElementsByTagName("Accuracy").item(0);
		NodeList AccuracyList = Accuracy.getChildNodes();
		for (int i = 0, len = AccuracyList.getLength(); i < len; i++) {
			
			logger.info(AccuracyList.item(i).getNodeName());
			if ("executePath".equals(AccuracyList.item(i).getNodeName())) {
				String exePath = AccuracyList.item(i).getFirstChild().getNodeValue().trim();
				map.put(AccuracyList.item(i).getNodeName(), exePath);
			}
			if ("input".equals(AccuracyList.item(i).getNodeName())) {
				String input = AccuracyList.item(i).getFirstChild().getNodeValue().trim();
				map.put("station_input", input);
				//logger.info(map);
			}
		}
		Node retrived = document.getElementsByTagName("retrieved").item(0);
		NodeList retrivedList = retrived.getChildNodes();
		for (int i = 0, len = retrivedList.getLength(); i < len; i++) {
			
			logger.info(retrivedList.item(i).getNodeName());
			
			if ("output".equals(retrivedList.item(i).getNodeName())) {
				String input = retrivedList.item(i).getFirstChild().getNodeValue().trim();
				map.put("retrieved_input", input);
				//logger.info(map);
			}
		}
		
		
		
		return map;
	}
	
	public Map<String, String> getAccuracyReconstructParams(){
		Map<String, String> map = new HashMap<String, String>();
		Node Accuracy = document.getElementsByTagName("Accuracy").item(0);
		NodeList AccuracyList = Accuracy.getChildNodes();
		for (int i = 0, len = AccuracyList.getLength(); i < len; i++) {
			
			logger.info(AccuracyList.item(i).getNodeName());
			if ("executePath".equals(AccuracyList.item(i).getNodeName())) {
				String exePath = AccuracyList.item(i).getFirstChild().getNodeValue().trim();
				map.put(AccuracyList.item(i).getNodeName(), exePath);
			}
			if ("input".equals(AccuracyList.item(i).getNodeName())) {
				String input = AccuracyList.item(i).getFirstChild().getNodeValue().trim();
				map.put("station_input", input);
				//logger.info(map);
			}
		}
		Node reconstructed = document.getElementsByTagName("reconstructed").item(0);
		NodeList reconstructedList = reconstructed.getChildNodes();
		for (int i = 0, len = reconstructedList.getLength(); i < len; i++) {
			
			logger.info(reconstructedList.item(i).getNodeName());
			
			if ("output".equals(reconstructedList.item(i).getNodeName())) {
				String input = reconstructedList.item(i).getFirstChild().getNodeValue().trim();
				map.put("reconstructedList_input", input);
				//logger.info(map);
			}
		}
		
		
		
		return map;
	}
	
	public static void main(String[] args) throws Exception {
		XMLFileUtil xml = new XMLFileUtil("url.xml");

		Map<String, String> pmStationParams = xml.getAccuracyReconstructParams();
		System.out.println(pmStationParams);
	}
}
