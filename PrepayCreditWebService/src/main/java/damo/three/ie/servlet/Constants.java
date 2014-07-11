/*
 * This file is part of Prepay Credit for Android
 *
 * Copyright © 2013  Damien O'Reilly
 *
 * Prepay Credit for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Prepay Credit for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Prepay Credit for Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Report bugs or new features at: https://github.com/DamienOReilly/PrepayCredit
 * Contact the author at:          damienreilly@gmail.com
 */

package damo.three.ie.servlet;

class Constants {

	public static final String MY3_URL = "https://sso.three.ie/mylogin/?service=https%3A%2F%2Fmy3account.three.ie%2FThreePortal%2Fappmanager%2FThree%2FMy3ROI%3F_pageLabel%3DP33403896361331912377205%26_nfpb%3Dtrue%26resource=portlet";
	public static final String MY3_USAGE_PAGE = "https://my3account.three.ie/My_account_balance";
	public static final String MY3_TOKEN_PAGE = "https://my3account.three.ie/ThreePortal/appmanager/Three/My3ROI?_pageLabel=P33403896361331912377205&_nfpb=true&resource=portlet&ticket=ST-";
	public static final String LOGIN_TOKEN_REGEX = ".*<input type=\"hidden\" name=\"lt\" value=\"(LT-.*)\" />.*";
	public static final String LOGGED_IN_TOKEN_REGEX = ".*ticket=ST-(.*)';.*";
	public static final String OUT_OF_BUNDLE_REGEX = ".*Out-of-allowance data used since(.*?)</p>.*";

}
