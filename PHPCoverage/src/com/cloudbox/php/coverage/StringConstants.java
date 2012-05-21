package com.cloudbox.php.coverage;

public interface StringConstants {

	public static final String CONNECTION_STATUS_VALID = "VALID";
	public static final String CONNECTION_NO_CONNECTION = "NO CONNECTION";
	public static final String CONNECTION_URL_EXCEPTION = "URL EXCEPTION";
	public static final String CONNECTION_INVALID_LOGIN = "INVALID LOGIN";
	
	public static final String CLOUDBOX_PROJECTNAME = "cloudbox.projectname";
	public static final String CLOUDBOX_JOBNAME = "cloudbox.jobname";
	public static final String CLOUDBOX_DOMAINURL = "cloudbox.domainurl";
	public static final String CLOUDBOX_BUILDLOCATION = "cloudbox.buildlocation";
	public static final String CBDATA_PHP = "cbdata.php";
	public static final String CBDATA_CONTEXT = "context";
	public static final String CBDATA_FILE = "file";
	public static final String CBDATA_MAPPER = "mapper";
	public static final String CBDATA_SERVER = "server";
	
	public static final String CBDATA_USERNAME = "jenkins";
	public static final String CBDATA_USERNAME_STR = "username";

	public static final String STR_SERVERNAME = "serverName";
	public static final String STR_REPORTLOCATION = "reportLocation";

	public static final String SERVER_PUBLICKEY = "publicKey";
	public static final String SERVER_PRIVATEKEY = "privateKey";
	public static final String SERVER_SERVERIP = "serverip";
	public static final String SERVER_USERNAME = "username";

	public static final String DEPLOYSERVER_PUBLICKEY = "publicKeyLoc";
	public static final String DEPLOYSERVER_PRIVATEKEY = "privateKeyLoc";
	public static final String DEPLOYSERVER_SERVERIP = "dnsName";
	public static final String DEPLOYSERVER_USERNAME = "loginUser";
	
	public static final String NEWLINECHAR = "\r\n";
	
	public static final String KEY_FILE_PREFIX = "CMF";
	public static final String KEY_FILE_SUFFIX = "key";

	public static final String COVERAGEFOLDER = "coveragetest.html";

	public static final String REPORT_FILE = "file";
	public static final String REPORT_NAME = "name";
	public static final String REPORT_ACTUAL = "Actual";
	public static final String REPORT_TOTAL = "Total";
	public static final String REPORT_NAME_STR = "PHPCoverage";
	
	public static final String RESULT_TOTAL = "Total";
	public static final String RESULT_TECHSTACK_SERVERNAME = "ServerName";
	public static final String RESULT_BASEFOLDER = "cb_target";
	public static final String RESULT_WORKINGFOLDER = "phpcoverage";
	
	public static final String PARSER_FILENAME = "index.html";

	public static final String MAPPER_CBSTEPS = "cb_steps";
	public static final String MAPPER_PARAMETERS = "Parameters";
	public static final String MAPPER_REPORTLOCATION = "reportlocation";
	public static final String MAPPER_DEPLOY = "deploy";
	public static final String MAPPER_TECHSTACK = "techstack";
	
	public static final String SERVER_PREFIX = "CI_";
	public static final String SERVER_SUFFIX = "_TestNode";
}
