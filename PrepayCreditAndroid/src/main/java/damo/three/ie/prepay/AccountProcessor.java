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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import damo.three.ie.fragment.AccountProcessorFragment;
import damo.three.ie.net.ProcessRequest;
import damo.three.ie.net.ThreeHttpClient;
import damo.three.ie.util.AccountException;
import damo.three.ie.util.HtmlUtilities;
import damo.three.ie.util.PrepayException;
import org.acra.ACRA;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for logging into the My3 account and fetching details. Parsing data and returning usages
 * in JSON format.
 */
public class AccountProcessor extends AsyncTask<Void, Void, JSONArray> {

    private final AccountProcessorFragment accountProcessorFragment;
    private String pageContent = null;
    private Exception exception = null;
    private JSONArray jsonArray = null;
    private List<NameValuePair> postData = null;
    private ProcessRequest processRequest;


    /**
     * @param accountProcessorFragment Fragment that initialized this {@link AsyncTask}
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public AccountProcessor(AccountProcessorFragment accountProcessorFragment) throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {

        this.accountProcessorFragment = accountProcessorFragment;

        Context context = accountProcessorFragment.getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        DefaultHttpClient httpClient = ThreeHttpClient.getInstance(context).getHttpClient();
        processRequest = new ProcessRequest(httpClient);
        postData = new ArrayList<NameValuePair>();

        jsonArray = new JSONArray();

        addPropertyToPostData("username", sharedPreferences.getString("mobile", ""));
        addPropertyToPostData("password", sharedPreferences.getString("password", ""));
    }

    /**
     * Begin fetching the usage
     */
    private void start() throws IOException, AccountException, JSONException, PrepayException {
        // Attempt to log in.
        pageContent = processRequest.process(Constants.MY3_ACCOUNT_PAGE);
        Log.d(Constants.TAG, "using: my3account.three.ie");

        // Were we brought to the login page? If so, login. We sometimes skip this if our cookie still holds a valid
        // session.
        if (pageContent.contains("<label for=\"username\" class=\"portlet-form-input-label\">")) {
            Pattern p1 = Pattern.compile(Constants.LOGIN_TOKEN_REGEX, Pattern.DOTALL);
            Matcher m1 = p1.matcher(pageContent);
            if (m1.matches()) {
                Log.d(Constants.TAG, "Logging in...");
                addPropertyToPostData("lt", m1.group(1));
                pageContent = processRequest.process(Constants.MY3_ACCOUNT_PAGE, postData);
                if (pageContent.contains("Sorry, you've entered an invalid")) {
                    throw new AccountException("Invalid 3 mobile number or password.");
                } else if (pageContent.contains("You have entered your login details incorrectly too many times")) {
                    throw new AccountException("Account is temporarily disabled due to too many incorrect logins. " +
                            "Please try again later.");
                }
            }
        }

        // We end up here if we have logged in, or if our cookie was still valid.
        if (pageContent.contains("here</a> to access the service you requested.</p>")) {
            Pattern p1 = Pattern.compile(Constants.MY3_SERVICE_REGEX, Pattern.DOTALL);
            Matcher m1 = p1.matcher(pageContent);
            if (m1.matches()) {
                pageContent = processRequest.process(m1.group(1));
            }
            if (pageContent.contains("<p><strong>Your account balance.</strong></p>")) {
                parseUsage();
            } else {
                errorFetchingUsage();
            }

        }
    }

    /**
     * There was some problem fetching the usage, Alert the user, and log report for unexpected application state.
     * Might be useful for debugging.
     */
    private void errorFetchingUsage() throws PrepayException {

        String msg = "Unexpected response from server. Are you a 3Pay Ireland user? Or is my3account.three.ie down?";

        // There was some problem logging in. Log a bug report in-case 3 changed their page and we cannot parse it.
        PrepayException ex = new PrepayException(msg);
        ACRA.getErrorReporter().putCustomData("CURRENT_PAGE_CONTENT", pageContent);
        ACRA.getErrorReporter().handleSilentException(ex);

        //still let the user know we couldn't fetch the usage.
        throw new PrepayException(ex);
    }

    /**
     * Clean up the HTML, and parse. Convert usages into JSON.
     */
    private void parseUsage() throws JSONException {
        jsonArray = HtmlUtilities.parseUsageAsJSONArray(pageContent);
    }

    /**
     * Call back to the fragment with usages
     *
     * @param jsonArray Usages in JSON
     */
    @Override
    protected void onPostExecute(JSONArray jsonArray) {

        if (exception != null) {
            accountProcessorFragment.reportBackException(exception);
        } else {
            accountProcessorFragment.reportBackUsages(jsonArray);
        }
    }

    /**
     * {@link AsyncTask} worker
     */
    @Override
    protected JSONArray doInBackground(Void... arg0) {
        try {
            start();
            //return HtmlUtilities.parseUsageAsJSONArray(FileUtils.readFile(context, R.raw.usage));
        } catch (Exception e) {
            exception = e;
        }
        // According to:
        // http://httpcomponents.10934.n7.nabble.com/how-do-I-close-connections-on-HttpClient-4-x-td13679.html
        // Apache Http library itself releases connections as needed. Also our HttpClient is a singleton, so we want it
        // to be reused. Therefore I'm not cleaning up in finally{} block.
        return jsonArray;
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