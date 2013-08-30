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

package damo.three.ie.net;

import damo.three.ie.util.HtmlUtilities;
import org.acra.ACRA;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ProcessRequest {

    private HttpClient httpClient = null;
    private HttpResponse httpResponse;
    private HttpEntity httpEntity;
    private HttpUriRequest httpUriRequest = null;

    /**
     * GET constructor
     *
     * @param httpClient {@link HttpClient}
     * @param url        URL to GET
     */
    public ProcessRequest(HttpClient httpClient, String url) {
        this.httpUriRequest = new HttpGet(url);
        this.httpClient = httpClient;

        httpResponse = null;
        httpEntity = null;
    }

    /**
     * POST constructor
     *
     * @param httpClient {@link HttpClient}
     * @param url        URL to POST to
     * @param nvp        Name, Value Pairs
     * @throws UnsupportedEncodingException
     */
    public ProcessRequest(HttpClient httpClient, String url,
                          List<NameValuePair> nvp) throws UnsupportedEncodingException {
        this.httpClient = httpClient;
        this.httpUriRequest = new HttpPost(url);
        ((HttpEntityEnclosingRequestBase) this.httpUriRequest)
                .setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));

        httpResponse = null;
        httpEntity = null;
    }

    /**
     * @return Returns resulting HTML of the request as string
     * @throws IOException
     */
    public String process() throws IOException {

        httpResponse = httpClient.execute(httpUriRequest);
        httpEntity = httpResponse.getEntity();

        String pageContent = HtmlUtilities.getPageContent(httpEntity);
        if (httpEntity != null)
            httpEntity.consumeContent();


        // add current page content crash report logger incase application falls over. This is useful for
        // debugging logging in or parsing problems.
        // These pages don't contain the users credentials or other personal/sensitive information.
        ACRA.getErrorReporter().putCustomData("CURRENT_PAGE_CONTENT", pageContent);
        return pageContent;

    }

}
