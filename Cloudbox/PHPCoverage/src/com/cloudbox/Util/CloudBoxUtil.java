package com.cloudbox.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * Utility class for internal use.
 * 
 * 
 */
public class CloudBoxUtil {

	public static String readFile(String filePath) {
		StringBuffer sb = new StringBuffer();
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		try {
			File f = new File(filePath);
			fstream = new FileInputStream(f);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				sb.append(strLine).append("\r\n");
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		} finally {
			try {
				br.close();
				in.close();
				fstream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static void writeFile(String filePath, String content) {
		FileWriter fcstream;
		try {
			fcstream = new FileWriter(new File(filePath));
			BufferedWriter out2 = new BufferedWriter(fcstream);
			// out2.write(currentRunList.toString());
			out2.write(content);
			out2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getJSONArrayFileContent(File file) {
		BufferedReader reader = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
			reader.close();
			if (stringBuilder.length() == 0) {
				stringBuilder.append("[]");
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
		return stringBuilder.toString();
	}

	/**
	 * 
	 * This method copies the contents of the specified source file to a file of
	 * the same name in the specified destination directory. The destination
	 * directory is created if it does not exist. If the destination file
	 * exists, then this method will overwrite it.
	 * 
	 * @param srcFilePath
	 * @param destination
	 */
	public static void copyFileIfExists(String srcFilePath, String destination) {

		if (srcFilePath != null) {
			try {
				FileUtils.copyFileToDirectory(new File(srcFilePath), new File(destination));
			} catch (IOException e) {
				System.out.println("No File Found at: " + srcFilePath);
			}
		}
	}

	/**
	 * 
	 * This method copies the contents of the specified source file to the
	 * specified destination file. The directory holding the destination file is
	 * created if it does not exist. If the destination file exists, then this
	 * method will overwrite it.
	 * 
	 * @param srcFilePath
	 * @param fileNameAs
	 * @param destination
	 */
	public static void copyFileAsIfExists(String srcFilePath, String fileNameAs, String destination) {

		if (srcFilePath != null) {
			try {
				FileUtils.copyFile(new File(srcFilePath), new File(destination + "/" + fileNameAs));
			} catch (IOException e) {
				System.out.println("No File Found at: " + srcFilePath);
			}
		}
	}

	public static String correctFilePath(String path) {
		path = path.replace('\\', '/');
		Pattern objPattern = Pattern.compile("//");
		Matcher matcher = objPattern.matcher(path);
		path = matcher.replaceAll("/");
		return path;
	}

	public static String correctFolderPath(String path) { // NO_UCD
		path = path.replace("\\", "//");
		path = path.concat("//");
		Pattern objPattern = Pattern.compile("//");
		Matcher matcher = objPattern.matcher(path);
		path = matcher.replaceAll("//");
		return path;
	}

	public static String correctPath(String path) {
		path = path.replace('\\', '/');
		path = path.concat("/");
		Pattern objPattern = Pattern.compile("//");
		Matcher matcher = objPattern.matcher(path);
		path = matcher.replaceAll("/");
		return path;
	}

	/**
	 * Converts the given UTC TimeStamp to given format from
	 * "yyyy-MM-dd'T'HH:mm:ss'Z'" format
	 * 
	 * @param utcTimeStamp
	 * @param formatExpected
	 * @return
	 */
	public static String getTimeStampInFormat(String utcTimeStamp, String formatExpected) {
		TimeZone utc = TimeZone.getTimeZone("UTC");
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		f.setTimeZone(utc);
		GregorianCalendar cal = new GregorianCalendar(utc);
		try {
			cal.setTime(f.parse(utcTimeStamp));
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}

		SimpleDateFormat sdf = new SimpleDateFormat(formatExpected);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(cal.getTime());
	}

	public static String getFileExtension(String fileName) {
		int dotPos = fileName.lastIndexOf(".");
		return fileName.substring(dotPos);
	}

	public static String getFileNameNoExtension(String fileName) {
		int dotPos = fileName.lastIndexOf(".");
		return fileName.substring(0, dotPos);
	}

	/**
	 * Convert empty string to null.
	 */
	public static String fixEmpty(String s) {
		if (s == null || s.length() == 0)
			return null;
		return s;
	}

	/**
	 * Guesses the current host name.
	 */
	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "localhost";
		}
	}

	/**
	 * Convert null to "".
	 */
	public static String fixNull(String s) {
		if (s == null)
			return "";
		else
			return s;
	}

	/**
	 * Convert empty string to null, and trim whitespace.
	 * 
	 */
	public static String fixEmptyAndTrim(String s) {
		if (s == null)
			return null;
		return fixEmpty(s.trim());
	}

	/**
	 * Cuts all the leading path portion and get just the file name.
	 */
	public static String getFileName(String filePath) {
		int idx = filePath.lastIndexOf('\\');
		if (idx >= 0)
			return getFileName(filePath.substring(idx + 1));
		idx = filePath.lastIndexOf('/');
		if (idx >= 0)
			return getFileName(filePath.substring(idx + 1));
		return filePath;
	}
}
