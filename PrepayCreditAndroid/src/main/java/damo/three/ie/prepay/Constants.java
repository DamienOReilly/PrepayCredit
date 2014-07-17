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

package damo.three.ie.prepay;

public class Constants {

    public static final String MY3_ACCOUNT_PAGE = "https://sso.three.ie/mylogin/?service=https://my3account.three" +
            ".ie/My_account_balance&dontTestForDongleUser=true";
    public static final String MY3_MAIN_PAGE = "https://my3account.three.ie";
    public static final String MY3_SERVICE_REGEX = ".*<p>Click <a href=\"(.*?)\".*to access the service you requested" +
            ".*";
    public static final String LOGIN_TOKEN_REGEX = ".*<input type=\"hidden\" name=\"lt\" value=\"(LT-.*)\" />.*";
    public static final String OUT_OF_BUNDLE_REGEX = ".*Out-of-allowance data used since (\\d{1," +
            "2}).*?\\s(.*?)\\s(\\d{4}).*";
    public static final String TAG = "PrepayCredit";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, " +
            "like Gecko) Chrome/35.0.1916.153 Safari/537.36";

    public static final String REGISTER_URL = "https://my3account.three.ie/Sign_up";
    public static final String FORGOT_PASS_URL = "https://my3account.three.ie/Forgotten_password";

    public static final int HOUR_TO_UPDATE = 10;
    public static final int HOUR_TO_RETRY = 19;
}
