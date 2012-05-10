package com.cloudbox.php.coverage;

import java.util.StringTokenizer;

import org.apache.commons.lang.math.NumberUtils;

public class CoverageStorage {

	private int totalLines = 0;
	private int coveredLines = 0;

	private int totalMethods = 0;
	private int coveredMethods = 0;

	private int totalClasses = 0;
	private int coveredClasses = 0;

	public CoverageStorage(String lines, String methods, String classes) {
		if (lines != null && lines.isEmpty() == false && lines.trim().equalsIgnoreCase("&nbsp;") == false)
			setLines(lines);
		if (methods != null && methods.isEmpty() == false && methods.trim().equalsIgnoreCase("&nbsp;") == false)
			setMethods(methods);
		if (classes != null && classes.isEmpty() == false && classes.trim().equalsIgnoreCase("&nbsp;") == false)
			setClasses(classes);
	}

	public void setLines(String str) {
		StringTokenizer st = new StringTokenizer(str, "/");
		int index = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken().trim();
			if (NumberUtils.isNumber(token)) {
				if (index == 0) {
					coveredLines = NumberUtils.toInt(token);
				} else {
					totalLines = NumberUtils.toInt(token);
				}
			}
			// System.out.println(token);
			index++;
		}
	}

	public void setClasses(String str) {
		StringTokenizer st = new StringTokenizer(str, "/");
		int index = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken().trim();
			if (NumberUtils.isNumber(token)) {
				if (index == 0) {
					coveredClasses = NumberUtils.toInt(token);
				} else {
					totalClasses = NumberUtils.toInt(token);
				}
			}
			// System.out.println(token);
			index++;
		}
	}

	public void setMethods(String str) {
		StringTokenizer st = new StringTokenizer(str, "/");
		int index = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken().trim();
			if (NumberUtils.isNumber(token)) {
				if (index == 0) {
					coveredMethods = NumberUtils.toInt(token);
				} else {
					totalMethods = NumberUtils.toInt(token);
				}
			}
			// System.out.println(token);
			index++;
		}
	}

	public int getTotalLines() {
		return totalLines;
	}

	public int getCoveredLines() {
		return coveredLines;
	}

	public int getTotalMethods() {
		return totalMethods;
	}

	public int getCoveredMethods() {
		return coveredMethods;
	}

	public int getTotalClasses() {
		return totalClasses;
	}

	public int getCoveredClasses() {
		return coveredClasses;
	}

}
