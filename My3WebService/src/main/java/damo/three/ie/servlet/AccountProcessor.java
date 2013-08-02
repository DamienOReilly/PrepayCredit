/*
 * This file is part of My3 Prepay for Android
 *
 * Copyright © 2013  Damien O'Reilly
 *
 * My3 Prepay for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * My3 Prepay for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My3 Prepay for Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Report bugs or new features at: https://github.com/DamienOReilly/My3Usage
 * Contact the author at:          damienreilly@gmail.com
 */

package damo.three.ie.servlet;

import damo.three.ie.servlet.net.ThreeHttpClient;
import damo.three.ie.servlet.util.HtmlUtilities;
import damo.three.ie.servlet.util.ThreeException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AccountProcessor {

    public static final String MY3_URL = "https://sso.three.ie/mylogin/?service=https%3A%2F%2Fmy3account.three.ie%2FThreePortal%2Fappmanager%2FThree%2FMy3ROI%3F_pageLabel%3DP33403896361331912377205%26_nfpb%3Dtrue%26resource=portlet";
    private static final String MY3_USAGE_PAGE = "https://my3account.three.ie/My_account_balance";
    private static final String MY3_TOKEN_PAGE = "https://my3account.three.ie/ThreePortal/appmanager/Three/My3ROI?_pageLabel=P33403896361331912377205&_nfpb=true&resource=portlet&ticket=ST-";
    private static final String LOGIN_TOKEN_REGEX = ".*<input type=\"hidden\" name=\"lt\" value=\"(LT-.*)\" />.*";
    private static final String LOGGED_IN_TOKEN_REGEX = ".*ticket=ST-(.*)';.*";
    private static final String OUT_OF_BUNDLE_REGEX = ".*Out-of-allowance data used since(.*)</p>.*";

    private static final Logger log = Logger.getLogger(AccountProcessor.class.getName());

    private HttpClient httpClient = null;
    private String pageContent = null;
    private final String username;
    private String usernameObfuscated;
    private final String password;

    private final PrintWriter out;

    public AccountProcessor(final PrintWriter out, final String username,
                            final String password) {

        this.out = out;
        this.username = username;
        // mask out the last 3 digits of the mobile number before logging. Valid
        // number is at least 10 digits. We will only obfuscate this.

        if (username.length() >= 10) {
            usernameObfuscated = username.substring(0, username.length() - 3)
                    + "***";
        }
        this.password = password;

    }

    /**
     * Let's kick this thing off
     */
    public void go() {

        try {

            log.info("User " + usernameObfuscated
                    + " requesting usage information.");

            this.httpClient = new ThreeHttpClient().getHttpClient();

            pageContent = new ProcessRequest(httpClient, MY3_URL).process();

            // Check if this brought us to the login page.., if so, then login.
            // Sometimes when using my3 on gsm, we aren't asked for login. Seems
            // to be some server side session, as its not handled my cookies
            // anyway.
            Pattern p1 = Pattern.compile(LOGIN_TOKEN_REGEX, Pattern.DOTALL);
            Matcher m1 = p1.matcher(pageContent);

            // If we retrieved a login-token, attempt to submit login
            // credentials
            if (m1.matches()) {

                pageContent = new ProcessRequest(httpClient,
                        getNameValuePair(m1.group(1))).process();

                if (pageContent.contains("Sorry, you've entered an invalid")) {

                    throw new ThreeException(
                            "Invalid 3 mobile number or password.");
                } else if (pageContent
                        .contains("You have entered your login details incorrectly too many times")) {
                    throw new ThreeException(
                            "Account is temporarily disabled due to too many incorrect logins. Please try again later.");
                }

                acceptToken();
                /**
                 * Otherwise check if we are already logged in Sometimes when on
                 * GSM, it auto logs you in and you get sent to to Page with ST
                 * token.
                 */
            } else if (pageContent.contains("Login successful.")) {

                acceptToken();

            } else {
                throw new ThreeException(
                        "Error logging in. Unexpected response from server.");
            }

        } catch (Exception e) {
            echoException(e);

        } finally {

            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }

    }

    /**
     * Accept token that is needed to be submitted in POST data to login.
     *
     * @throws ThreeException
     * @throws IOException
     * @throws ParseException
     * @throws JSONException
     */
    private void acceptToken() throws ThreeException, IOException,
            ParseException, JSONException {
        Pattern p1 = Pattern.compile(LOGGED_IN_TOKEN_REGEX, Pattern.DOTALL);
        Matcher m1 = p1.matcher(pageContent);

        if (m1.matches()) {
            pageContent = new ProcessRequest(httpClient, MY3_TOKEN_PAGE
                    + m1.group(1)).process();

            my3FetchUsage();

        } else {
            throw new ThreeException(
                    "Error reading token from login procedure.");
        }
    }

    /**
     * Fetch the usage from the usage page.
     *
     * @throws ThreeException
     * @throws IOException
     * @throws JSONException
     */
    private void my3FetchUsage() throws ThreeException, IOException,
            JSONException {

        if (pageContent.contains("Welcome back.")) {

            pageContent = new ProcessRequest(httpClient, MY3_USAGE_PAGE)
                    .process();
            my3ParseUsage();

        } else {
            throw new ThreeException(
                    "Error logging in. Unexpected response from server.");
        }

    }

    /**
     * Clean/Scrape/Parse the usages. Expose as JSON format.
     *
     * @throws JSONException
     */
    private void my3ParseUsage() throws JSONException {
        // The HTML on my3 is pig-ugly, so we will use JSoup to
        // clean and parse it.

        Document doc = Jsoup.parse(pageContent);
        HtmlUtilities.removeComments(doc);

        Elements elements = doc.getElementsByTag("table");

        // The My3WebService will also return usages as JSON. This is a common
        // format that the app and webservice will use.
        JSONArray jsonArray = new JSONArray();

        // three don't have a sub label for the 3-to-3 calls.. feck them!
        boolean three2threeCallsBug = false;

        for (Element element : elements) {

            for (Element subelement : element.select("tbody > tr")) {

                if ((subelement.text().contains("3 to 3 Calls"))
                        && (subelement.text().contains("Valid until")))
                    three2threeCallsBug = true;

                Elements subsubelements = subelement.select("td");

                if (subsubelements.size() == 3) {

                    // skip the "total" entries
                    if (subsubelements.select("td").get(0).text()
                            .contains("Total")) {
                        continue;
                    }

                    JSONObject currentItem = new JSONObject();

                    if (three2threeCallsBug) {
                        currentItem.put("item", "3 to 3 Calls");
                        three2threeCallsBug = false;
                    } else {
                        // Get rid of that "non-breaking space" character if it
                        // exists
                        String titleToClean = subsubelements.select("td")
                                .get(0).text().replace("\u00a0", "").trim();
                        currentItem.put("item", titleToClean);
                    }

                    currentItem.put("value1", subsubelements.select("td")
                            .get(1).text());
                    currentItem.put("value2", subsubelements.select("td")
                            .get(2).text());

                    // Out of Bundle charges has an extra property
                    if (currentItem.getString("item").equals("Internet")) {

                        Pattern p1 = Pattern.compile(OUT_OF_BUNDLE_REGEX,
                                Pattern.DOTALL);
                        Matcher m1 = p1.matcher(pageContent);

                        if (m1.matches()) {
                            currentItem.put("value3", m1.group(1));
                        }

                    }
                    jsonArray.put(currentItem);
                }

            }

        }

        out.print(jsonArray.toString());
    }

    /**
     * Send back to client an exception. Log it also for offline investigation
     * if needed.
     *
     * @param e Exception
     */
    private void echoException(Exception e) {
        out.print("Exception[" + e.getLocalizedMessage() + "]");
        log.error("User " + usernameObfuscated + " had problem.");
        log.error(e);
    }

    /**
     * Build a NameValuePair list for POST data.
     *
     * @param token Extracted token from My3 page
     * @return NameValuePair
     */
    List<NameValuePair> getNameValuePair(String token) {
        List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("username", username));
        nvp.add(new BasicNameValuePair("password", password));
        nvp.add(new BasicNameValuePair("lt", token));

        return nvp;

    }
}