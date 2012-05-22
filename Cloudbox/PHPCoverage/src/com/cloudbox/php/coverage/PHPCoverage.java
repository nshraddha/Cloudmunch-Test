package com.cloudbox.php.coverage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.marcoratto.apache.ssh.Scp;
import uk.co.marcoratto.apache.ssh.ScpException;
import uk.co.marcoratto.scp.listeners.ListenerPrintStream;

import com.cloudbox.Util.CloudBoxUtil;
import com.cloudbox.php.coverage.util.CloudboxURLReader;
import com.cloudbox.php.coverage.util.URLGenerator;

public class PHPCoverage {

	/**
	 * @param args
	 */
//	public static void main2(String[] args) {
//		Map<String, CoverageStorage> result = parseReport(FilenameUtils.separatorsToUnix("E:/temp/Cloudbox/coveragetest.html/var_www_html_cloudbox.html"));
//
//	}

	public static void main(String[] args) {
		String targetName = args[0];
		String reportFileName = args[1];
		String projectName = System.getenv(StringConstants.CLOUDBOX_PROJECTNAME);
		String jobName = System.getenv(StringConstants.CLOUDBOX_JOBNAME);
		String domainURL = System.getenv(StringConstants.CLOUDBOX_DOMAINURL);
		String workingDir = System.getenv(StringConstants.CLOUDBOX_BUILDLOCATION) + File.separator + StringConstants.RESULT_BASEFOLDER + File.separator + StringConstants.RESULT_WORKINGFOLDER;
		JSONObject outputData = new JSONObject();
		
		System.out.println("projectName : " + projectName);
		System.out.println("jobName : " + jobName);
		System.out.println("domainURL : " + domainURL);
		System.out.println("workingDir : " + workingDir);
		// projectName = "Cloudbox";
		// jobName = "quick";
		// domainURL = "http://dev.cloudmunch.com";

		Map<String, String> mapData = null;
		mapData = new HashMap<String, String>();
		mapData = findReferenceCBApp(getURLData(domainURL, StringConstants.CBDATA_CONTEXT, StringConstants.CBDATA_MAPPER), projectName, jobName, targetName);
		// mapData.put("serverName", "CI_XXXXX_TestNode");
		// mapData.put("serverName", "CloudboxTest");
		// mapData.put("reportLocation",
		// "E:/temp/Cloudbox/coveragetest.html/index.html");

		if (mapData.containsKey(StringConstants.RESULT_TECHSTACK_SERVERNAME) && mapData.containsKey(StringConstants.STR_REPORTLOCATION)) {
			String serverName = mapData.get(StringConstants.RESULT_TECHSTACK_SERVERNAME);
			String reportLocation = mapData.get(StringConstants.STR_REPORTLOCATION);

			// need to replace local deployserver to remote context server
			// String deployConfig =
			// CloudBoxUtil.readFile("E:/GITWorkSpace_new1/CloudBox/InitData/deployServer.json");
			// String deployConfig =
			// CloudBoxUtil.readFile("/var/cloudbox/temp/PHPCoverage/deployServer.json");
			String deployConfig = getURLData(domainURL, StringConstants.CBDATA_CONTEXT, StringConstants.CBDATA_SERVER);
			try {
				boolean serverEntryFound = false;
				JSONArray deployConfigJson = new JSONArray(deployConfig);
				for (int i = 0; i < deployConfigJson.length(); i++) {
					JSONObject s = deployConfigJson.getJSONObject(i);
					if (s.has(serverName)) {
						// System.out.println(s);
						serverEntryFound = true;
						s = s.getJSONObject(serverName);
						mapData.put(StringConstants.SERVER_PUBLICKEY, s.getString(StringConstants.DEPLOYSERVER_PUBLICKEY));
						mapData.put(StringConstants.SERVER_PRIVATEKEY, s.getString(StringConstants.DEPLOYSERVER_PRIVATEKEY));
						mapData.put(StringConstants.SERVER_SERVERIP, s.getString(StringConstants.DEPLOYSERVER_SERVERIP));
						mapData.put(StringConstants.SERVER_USERNAME, s.getString(StringConstants.DEPLOYSERVER_USERNAME));
						break;
					}
				}
				if (serverEntryFound) {
//					String pubkey = getURLData(domainURL, StringConstants.CBDATA_FILE, mapData.get(StringConstants.SERVER_PUBLICKEY));
					StringBuffer prvkey = new StringBuffer(getURLData(domainURL, StringConstants.CBDATA_FILE, mapData.get(StringConstants.SERVER_PRIVATEKEY)));
					{
						int actualKeyLength = prvkey.length() - 31 - 29;
						int eachLineLength = 76;
						int numberLines = (actualKeyLength / eachLineLength) + ((actualKeyLength % eachLineLength) > 0 ? 1 : 0) + 2; // 1648//1692

						// newLineCharToInsert
						for (int i = 0; i < numberLines; i++) {
							if (i == 0) {
								prvkey.insert(31, StringConstants.NEWLINECHAR);
							} else if (i == numberLines - 2) {
								prvkey.insert(prvkey.length() - 29, StringConstants.NEWLINECHAR);
							} else if (i >= 0 && i < (numberLines - 2)) {
								prvkey.insert(31 + i * StringConstants.NEWLINECHAR.length() + i * eachLineLength, StringConstants.NEWLINECHAR);
							}
						}
					}
					// String prvkey_filename = new
					// File(mapData.get("privateKey")).getName();

					try {
						File workingDirFile = new File(workingDir);
						workingDirFile.mkdirs();
						File keyFile = File.createTempFile(StringConstants.KEY_FILE_PREFIX, StringConstants.KEY_FILE_SUFFIX, workingDirFile);
						keyFile.deleteOnExit();

						BufferedWriter output = new BufferedWriter(new FileWriter(keyFile));
						output.write(prvkey.toString());
						output.close();

						Scp scp = new Scp(new ListenerPrintStream());
						scp.setTrust(true);
						scp.setVerbose(true);
						// scp.setLocalFile("E:/temp/Cloudbox/coveragetest.html/*");
						// scp.setRemoteTodir(mapData.get("username") + "@" +
						// mapData.get("serverip") + ":" +
						// "/var/cloudbox/temp/coveragetest.html");
						// FilenameUtils.separatorsToUnix()
						scp.setFile(mapData.get(StringConstants.SERVER_USERNAME) + "@" + mapData.get(StringConstants.SERVER_SERVERIP) + ":"
								+ FilenameUtils.separatorsToUnix(reportLocation + File.separator + StringConstants.COVERAGEFOLDER + File.separator + "*"));
						scp.setTodir(workingDir);
						scp.setKeyfile(keyFile.getAbsolutePath());
						// scp.setKeyfile("E:/Downloads/dcloud.pem");
						scp.execute();
					} catch (ScpException e) {
						outputData.put("message", "Couldn't get the Reports Copied");
						outputData.put("cause", e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					Map<String, CoverageStorage> result = parseReport(FilenameUtils.separatorsToUnix(workingDir + File.separator + StringConstants.PARSER_FILENAME));
					outputData = convertToCBReport(result);
					// convertToCBReport(result);
					// System.out.println(deployConfigJson.toString());
				} else {
					outputData.put("message", "Couldn't get the Server Details");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
			try {
				outputData.put("message", "Couldn't find either Server Details / Report Location in the mapper");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		BufferedWriter output;
		try {
			File f = new File(reportFileName);
			f.getParentFile().mkdirs();
			output = new BufferedWriter(new FileWriter(reportFileName));
			output.write(outputData.toString());
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static JSONObject convertToCBReport(Map<String, CoverageStorage> result) {
		JSONObject returnValue = new JSONObject();
		try {
			returnValue.put(StringConstants.REPORT_FILE, new JSONObject());
			returnValue.getJSONObject(StringConstants.REPORT_FILE).put(StringConstants.REPORT_NAME, StringConstants.REPORT_NAME_STR);
			returnValue.getJSONObject(StringConstants.REPORT_FILE).put(StringConstants.REPORT_ACTUAL, result.get(StringConstants.RESULT_TOTAL).getCoveredLines());
			returnValue.getJSONObject(StringConstants.REPORT_FILE).put(StringConstants.REPORT_TOTAL, result.get(StringConstants.RESULT_TOTAL).getTotalLines());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	private static String getURLData(String domainURL, String key, String value) {
		URLGenerator urlGenerator = new URLGenerator();
		urlGenerator.setWeb(domainURL);
		urlGenerator.setFileName(StringConstants.CBDATA_PHP);
		urlGenerator.setApp("");
		urlGenerator.addArgument(StringConstants.CBDATA_USERNAME_STR, StringConstants.CBDATA_USERNAME);
		// urlGenerator.addArgument("username", "box@cloudboxonline.com");
		// urlGenerator.addArgument("password", "box");
		urlGenerator.addArgument(key, value);

		// String url = urlGenerator.getURL(true);
		CloudboxURLReader.setUrlGenerator(urlGenerator);
		String str = CloudboxURLReader.getGETContent();
		// System.out.println(str);

		return str;
	}

	private static Map<String, String> findReferenceCBApp(String mapperFile, String projectName, String jobName, String targetName) {
		Map<String, String> returnMap = new HashMap<String, String>();
		try {
			JSONObject mapper = new JSONObject(mapperFile);
			if (mapper.has(projectName)) {
				JSONObject pro = mapper.getJSONObject(projectName);
				if (pro.has(jobName)) {
					JSONObject job = pro.getJSONObject(jobName);
					if (job.has(StringConstants.MAPPER_CBSTEPS)) {
						JSONObject cb_steps = job.getJSONObject(StringConstants.MAPPER_CBSTEPS);
						if (cb_steps.has(targetName)) {
							JSONObject instr_steps = cb_steps.getJSONObject(targetName);
							if (instr_steps.has(StringConstants.MAPPER_PARAMETERS)) {
								JSONObject params = instr_steps.getJSONObject(StringConstants.MAPPER_PARAMETERS);
								if (params.has(StringConstants.MAPPER_REPORTLOCATION)) {
									returnMap.put(StringConstants.STR_REPORTLOCATION, params.getString(StringConstants.MAPPER_REPORTLOCATION));

								}
							}
						}
					}
					if (job.has(StringConstants.MAPPER_DEPLOY)) {
						JSONObject deploy = job.getJSONObject(StringConstants.MAPPER_DEPLOY);
						if (deploy.has(StringConstants.MAPPER_TECHSTACK)) {
							JSONObject techstack = deploy.getJSONObject(StringConstants.MAPPER_TECHSTACK);
							String[] listedtechstack = JSONObject.getNames(techstack);
							for (int i = 0; i < listedtechstack.length; i++) {
								String choice = techstack.getString(listedtechstack[i]);
								if (choice.equalsIgnoreCase("yes") || choice.equalsIgnoreCase("true") || choice.equalsIgnoreCase("on")) {
									returnMap.put(StringConstants.RESULT_TECHSTACK_SERVERNAME, StringConstants.SERVER_PREFIX + listedtechstack[i] + StringConstants.SERVER_SUFFIX);
									break;
								}
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	private static Map<String, CoverageStorage> parseReport(String fileLocation) {
		Parser parser;
		NodeFilter filter;
		Map<String, CoverageStorage> allResult = new LinkedHashMap<String, CoverageStorage>();
		List<String> fr = new ArrayList<String>();
		try {
			parser = new Parser();
			filter = new TagNameFilter("tr");
			Parser.getConnectionManager().setRedirectionProcessingEnabled(true);
			Parser.getConnectionManager().setCookieProcessingEnabled(true);
			// parser.setResource("E:/temp/w3c/report/tidy.html");
			parser.setInputHTML(CloudBoxUtil.readFile(fileLocation));
			NodeList list = parser.parse(filter);
			int rows[] = new int[3];
			rows[0] = 8;
			rows[1] = 9;
			rows[2] = 10;
			// int rows[] = new int[1];
			// rows[0] = 11;
			for (int i = 0; i < rows.length; i++) {
				TableRow tr = (TableRow) list.elementAt(rows[i]);
				fr = getResultList(tr);
				allResult.put(fr.get(0), new CoverageStorage(fr.get(2), fr.get(4), fr.get(6)));
			}
			// System.out.println(allResult);
		} catch (ParserException e) {
			System.out.println("Parser Exception Found");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("general Exception Found");
			e.printStackTrace();
		}
		return allResult;
	}

	private static List<String> getResultList(TableRow tr) {
		List<String> foundResult = new ArrayList<String>();
		for (int j = 0; j < tr.getChildCount(); j++) {
			Node foundChild = tr.getChild(j);
			if (foundChild instanceof TableColumn) {
				NodeList nl = foundChild.getChildren();
				for (int i = 0; i < nl.size(); i++) {
					Node n = nl.elementAt(i);
					if (n instanceof TextNode || n instanceof LinkTag) {
						String content = "";
						if (n instanceof TextNode) {
							content = n.getText().trim();
						} else if (n instanceof LinkTag) {
							content = ((LinkTag) n).getChild(0).getText();
						}
						if (content.equalsIgnoreCase("\n") || content.isEmpty()) {
							// do nothing
						} else {
							foundResult.add(content);
							// System.out.println(content);
							break;
						}
					}
				}
				// String content = ((TableColumn)
				// foundChild).getChild(0).getText().trim();
				// if (content.equalsIgnoreCase("\n") || content.isEmpty()) {
				// // do nothing
				// } else {
				// foundResult.add(content);
				// System.out.println(content);
				// }
			}
		}
		return foundResult;
	}

}
