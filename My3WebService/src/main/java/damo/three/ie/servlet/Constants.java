package damo.three.ie.servlet;

class Constants {

	public static final String MY3_URL = "https://sso.three.ie/mylogin/?service=https%3A%2F%2Fmy3account.three.ie%2FThreePortal%2Fappmanager%2FThree%2FMy3ROI%3F_pageLabel%3DP33403896361331912377205%26_nfpb%3Dtrue%26resource=portlet";
	public static final String MY3_USAGE_PAGE = "https://my3account.three.ie/My_account_balance";
	public static final String MY3_TOKEN_PAGE = "https://my3account.three.ie/ThreePortal/appmanager/Three/My3ROI?_pageLabel=P33403896361331912377205&_nfpb=true&resource=portlet&ticket=ST-";
	public static final String LOGIN_TOKEN_REGEX = ".*<input type=\"hidden\" name=\"lt\" value=\"(LT-.*)\" />.*";
	public static final String LOGGED_IN_TOKEN_REGEX = ".*ticket=ST-(.*)';.*";
	public static final String OUT_OF_BUNDLE_REGEX = ".*Out-of-allowance data used since(.*)</p>.*";

}
