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

package damo.three.ie.my3;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import damo.three.ie.fragment.AccountProcessorFragment;
import damo.three.ie.net.ProcessRequest;
import damo.three.ie.net.ThreeHttpClient;
import damo.three.ie.util.DateUtils;
import damo.three.ie.util.HtmlUtilities;
import damo.three.ie.util.JSONUtils;
import damo.three.ie.util.ThreeException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountProcessor extends AsyncTask<Void, Void, JSONArray> {

    private static final String INTERMEDIATE_SERVER_URL = "https://secure.damienoreilly.org/My3WebService/FetchUsage";
    private static final String MY3_URL = "https://sso.three.ie/mylogin/?service=https%3A%2F%2Fmy3account.three.ie%2FThreePortal%2Fappmanager%2FThree%2FMy3ROI%3F_pageLabel%3DP33403896361331912377205%26_nfpb%3Dtrue%26resource=portlet";
    private static final String MY3_USAGE_PAGE = "https://my3account.three.ie/My_account_balance";
    private static final String MY3_TOKEN_PAGE = "https://my3account.three.ie/ThreePortal/appmanager/Three/My3ROI?_pageLabel=P33403896361331912377205&_nfpb=true&resource=portlet&ticket=ST-";
    private static final String LOGIN_TOKEN_REGEX = ".*<input type=\"hidden\" name=\"lt\" value=\"(LT-.*)\" />.*";
    private static final String LOGGED_IN_TOKEN_REGEX = ".*ticket=ST-(.*)';.*";
    private static final String OUT_OF_BUNDLE_REGEX = ".*Out-of-allowance data used since(.*)</p>.*";
    private static final String TAG = "My3";

    private HttpClient httpClient = null;
    private String pageContent = null;
    private Throwable damn = null;
    private SharedPreferences sharedPrefs = null;
    private JSONArray jsonArray = null;
    private List<NameValuePair> postData = null;

    private final AccountProcessorFragment accountProcessorFragment;
    private Context context = null;

    /**
     * @param accountProcessorFragment Fragment that initialized this {@link AsyncTask}
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public AccountProcessor(AccountProcessorFragment accountProcessorFragment)
            throws
            KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException {

        this.accountProcessorFragment = accountProcessorFragment;

        context = accountProcessorFragment.getSherlockActivity()
                .getApplicationContext();

        this.httpClient = ThreeHttpClient.getInstance(context).getHttpClient();
        postData = new ArrayList<NameValuePair>();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        addPropertyToPostData("username", sharedPrefs.getString("mobile", ""));
        addPropertyToPostData("password", sharedPrefs.getString("password", ""));
    }

    /**
     * Begin fetching the usage
     *
     * @throws IOException
     * @throws ThreeException
     * @throws ParseException
     * @throws JSONException
     */
    private void start() throws IOException,
            ThreeException, ParseException, JSONException {

        // check if the user is using Wi-Fi. We won't use the intermediate
        // server if the user is on Wi-Fi. Direct to my3account.three.ie should
        // be be fast enough on Wi-Fi and also means there is less load on our
        // WebService.
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // Are we going to use the intermediate server?
        if ((sharedPrefs.getBoolean("intermediate_server", true))
                && (!wifi.isConnected())) {

            Log.d(TAG, "Using: secure.damienoreilly.org");
            pageContent = new ProcessRequest(httpClient,
                    INTERMEDIATE_SERVER_URL, postData).process();

            // Did the My3WebService give us back an exception?
            if (pageContent.startsWith("Exception[")) {
                Pattern p = Pattern.compile("Exception\\[(.*)\\]");
                Matcher m = p.matcher(pageContent);

                if (m.matches()) {
                    throw new ThreeException(m.group(1));
                }
            }

            jsonArray = new JSONArray(pageContent);

        } else {

            pageContent = new ProcessRequest(httpClient, MY3_URL).process();
            Log.d(TAG, "using: my3account.three.ie");

            // Check if this brought us to the login page.., if so, then login.
            // Sometimes when using my3 on gsm, we aren't asked for login. Seems
            // to be some server side session, as its not handled my cookies
            // anyway.
            Pattern p1 = Pattern.compile(LOGIN_TOKEN_REGEX, Pattern.DOTALL);
            Matcher m1 = p1.matcher(pageContent);

            // If we retrieved a login-token, attempt to submit login
            // credentials
            if (m1.matches()) {
                Log.d(TAG, "Logging in...");

                addPropertyToPostData("lt", m1.group(1));

                pageContent = new ProcessRequest(httpClient, MY3_URL, postData)
                        .process();

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

                Log.d(TAG,
                        "Seems we are already logged in! Fairly common when on GSM, not WiFi.");
                acceptToken();

            } else {
                throw new ThreeException(
                        "Error logging in. Unexpected response from server.");
            }
        }

    }

    /**
     * On my3account.three.ie, we need to scrape a token to POST with our login details.
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
            Log.d(TAG, "Submitting token via: " + MY3_TOKEN_PAGE + m1.group(1));
            pageContent = new ProcessRequest(httpClient, MY3_TOKEN_PAGE
                    + m1.group(1)).process();

            my3FetchUsage();

        } else {
            throw new ThreeException(
                    "Error reading token from login procedure.");
        }
    }

    /**
     * Grab the usage page
     *
     * @throws ThreeException
     * @throws IOException
     * @throws JSONException
     */
    private void my3FetchUsage() throws ThreeException, IOException,
            JSONException {

        if (pageContent.contains("Welcome back.")) {
            Log.d(TAG, "Grabbing usage.");

            pageContent = new ProcessRequest(httpClient, MY3_USAGE_PAGE)
                    .process();
            my3ParseUsage();

        } else {
            throw new ThreeException(
                    "Error logging in. Unexpected response from server.");
        }

    }

    /**
     * Clean up the HTML, and parse. Convert usages into JSON.
     *
     * @throws JSONException
     */
    private void my3ParseUsage() throws JSONException {
        // The HTML on my3 is pig-ugly, so we will use JSoup to
        // clean and parse it.
        Log.d(TAG, "Ok, now parsing usage. my3.");

        Document doc = Jsoup.parse(pageContent);
        HtmlUtilities.removeComments(doc);

        Elements elements = doc.getElementsByTag("table");

        // The My3WebService will also return usages as JSON. This is a common
        // format that the app and webservice will use.
        jsonArray = new JSONArray();

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
    }

    /**
     * Call back to the fragment with usages
     *
     * @param jsonArray Usages in JSON
     */
    @Override
    protected void onPostExecute(JSONArray jsonArray) {

        if (damn != null) {
            accountProcessorFragment.reportBackException(damn);
        } else {

            // We will save the results to persistent SharedPreferences
            if (jsonArray.length() > 0) {
                SharedPreferences sharedPref = context.getSharedPreferences(
                        "damo.three.ie.previous_usage", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("last_refreshed", DateUtils.dateNowAsString());
                editor.putString("usage_info", jsonArray.toString());
                editor.commit();

                try {
                    accountProcessorFragment.reportBackUsages(JSONUtils
                            .jsonToBaseItems(jsonArray));
                } catch (ParseException e) {
                    accountProcessorFragment.reportBackException(e);
                    e.printStackTrace();
                } catch (JSONException e) {
                    accountProcessorFragment.reportBackException(e);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * {@link AsyncTask} worker
     */
    @Override
    protected JSONArray doInBackground(Void... arg0) {
        try {

            start();
            return jsonArray;

        } catch (Exception e) {
            e.printStackTrace();
            damn = e;

        }
        // According to http://httpcomponents.10934.n7.nabble.com/how-do-I-close-connections-on-HttpClient-4-x-td13679.html
        // Apache Http library itself releases connections as needed. Also our HttpClient is a singleton, so we want it
        // to be reused. Therefore I'm not cleaning up in finally{} block.

        return null;

    }

    /**
     * add a POST property/value as {@link BasicNameValuePair} to {@link List<NameValuePair>}
     *
     * @param property POST property
     * @param value    POST value
     */
    private void addPropertyToPostData(String property, String value) {
        postData.add(new BasicNameValuePair(property, value));
    }

}