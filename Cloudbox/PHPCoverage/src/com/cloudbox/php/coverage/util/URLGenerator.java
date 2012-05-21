package com.cloudbox.php.coverage.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class URLGenerator {

	private String web;
	private String app;
	private String fileName;
	private Map<String, String> arguments = new HashMap<String, String>();
	
	public URLGenerator(){
		
	}
	

	public String getWeb() {
		return web;
	}


	public void setWeb(String web) {
		this.web = web;
	}


	public String getApp() {
		return app;
	}


	public void setApp(String app) {
		this.app = app;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void addArgument(String key, String value){
		arguments.put(key, value);
	}
	
	public String createArgumentString(){
		StringBuffer retURL = new StringBuffer();
		if(arguments.size() > 0){
			Set<String> listOfArguments = arguments.keySet();
			for (Iterator<String> keyItem = listOfArguments.iterator(); keyItem.hasNext();) {
				String key = keyItem.next();
				String value = arguments.get(key);
				retURL.append(key + "=" + value);
				if(keyItem.hasNext()){
					retURL.append("&");
				}else{
				}
			}
		}else{
		}
		return retURL.toString();
	}

	public String getURL(boolean getArguments){
		StringBuffer retURL = new StringBuffer();
		retURL.append(web);
		retURL.append("/");
		if(app.trim().isEmpty() == false){
			retURL.append(app);
			retURL.append("/");
		}
		retURL.append(fileName);
		if(arguments.size() > 0 && getArguments == true){
			retURL.append("?");
			retURL.append(createArgumentString());
		}else{
		}
		return retURL.toString();
	}


	@Override
	public String toString() {
		return getURL(true);
	}
	
}
