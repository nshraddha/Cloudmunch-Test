package com.cloudbox.php.coverage.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.cloudbox.php.coverage.StringConstants;

public class CloudboxURLReader {
	
	static {
		// ###########
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSLv3");
		TrustManager[] tma = { new X509TrustManager(){

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			
		}};
		sc.init(null, tma, null);
		SSLSocketFactory ssf = sc.getSocketFactory();
		HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
		// ###########

		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
//		HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	private static CloudboxURLReader _instance = new CloudboxURLReader();

	private static URLGenerator urlGenerator = new URLGenerator();
	private static boolean functionPOST = false;
	private static URL cloudboxURL = null;

	private CloudboxURLReader() {
	}

	public static URLGenerator getUrlGenerator() {
		return urlGenerator;
	}

	public static void setUrlGenerator(URLGenerator urlGenerator) {
		CloudboxURLReader.urlGenerator = urlGenerator;
	}

	public static String getHostName() {
		return urlGenerator.getWeb();
	}

	public static void setHostName(String hostName) {
		urlGenerator.setWeb(hostName);
	}

	public static String getAppName() {
		return urlGenerator.getApp();
	}

	public static void setAppName(String appName) {
		urlGenerator.setApp(appName);
	}

	public static String getFunctionName() {
		return urlGenerator.getFileName();
	}

	public static void setFunctionName(String functionName) {
		urlGenerator.setFileName(functionName);
	}

	public static boolean isFunctionPOST() {
		return functionPOST;
	}

	public static void setFunctionPOST(boolean functionPOST) {
		CloudboxURLReader.functionPOST = functionPOST;
	}

	public static CloudboxURLReader getInstance() {
		return _instance;
	}

	public static String getContent(final String url) {
		String inputLine = "";
		final StringBuffer completeString = new StringBuffer();

		BufferedReader bufferReader = null;
		try {
			cloudboxURL = new URL(url);
			String protocol = cloudboxURL.getProtocol();
			if(protocol.equalsIgnoreCase("http")){
				System.out.println("Its a HTTP Connection (Unsecured)");
			}else if(protocol.equalsIgnoreCase("https")){
				System.out.println("Its a HTTPS Connection (Secured)");

			}
			bufferReader = new BufferedReader(new InputStreamReader(cloudboxURL.openStream()));
			while ((inputLine = bufferReader.readLine()) != null) {
				completeString.append(inputLine);
			}
			bufferReader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			if (completeString.length() > 0)
				completeString.delete(0, completeString.length() - 1);
			completeString.append(StringConstants.CONNECTION_URL_EXCEPTION);
		} catch (javax.net.ssl.SSLHandshakeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			if (completeString.length() > 0)
				completeString.delete(0, completeString.length() - 1);
			completeString.append(StringConstants.CONNECTION_NO_CONNECTION);
		} catch (Exception e){
			e.printStackTrace();
		}

		return completeString.toString();
	}

	public static String getContent() {
		if (functionPOST)
			return getPOSTContent();
		else
			return getGETContent();
	}

	public static String getPOSTContent() {
		functionPOST = true;
		DataOutputStream printout = null;
		String content = "";
		final StringBuffer completeString = new StringBuffer();

		try {
			cloudboxURL = new URL(urlGenerator.getURL(false));

			URLConnection urlConnection = cloudboxURL.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);

			printout = new DataOutputStream(urlConnection.getOutputStream());
			content = urlGenerator.createArgumentString();
			printout.writeBytes(content);
			printout.flush();
			printout.close();

			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String str = null;
			while (null != (str = bufferReader.readLine())) {
				completeString.append(str);
			}

			bufferReader.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return completeString.toString();
	}

	public static String getGETContent() {
		functionPOST = false;

		String inputLine = "";
		final StringBuffer completeString = new StringBuffer();

		BufferedReader bufferReader = null;
		try {
			cloudboxURL = new URL(urlGenerator.getURL(true));
			bufferReader = new BufferedReader(new InputStreamReader(cloudboxURL.openStream()));
			while ((inputLine = bufferReader.readLine()) != null) {
				completeString.append(inputLine);
			}
			bufferReader.close();
		} catch (MalformedURLException e) {
			if (completeString.length() > 0)
				completeString.delete(0, completeString.length() - 1);
			completeString.append(StringConstants.CONNECTION_URL_EXCEPTION);
		} catch (IOException e) {
			if (completeString.length() > 0)
				completeString.delete(0, completeString.length() - 1);
			completeString.append(StringConstants.CONNECTION_NO_CONNECTION);
		}

		return completeString.toString();
	}

}
