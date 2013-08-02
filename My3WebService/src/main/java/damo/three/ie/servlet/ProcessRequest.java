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

import damo.three.ie.servlet.util.HtmlUtilities;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

class ProcessRequest {

    private HttpClient httpClient = null;
    private HttpUriRequest httpUriRequest = null;

    /**
     * GET constructor
     *
     * @param httpClient HttpClient
     * @param url        URL
     */
    public ProcessRequest(HttpClient httpClient, String url) {
        this.httpUriRequest = new HttpGet(url);
        this.httpClient = httpClient;

    }

    /**
     * POST constructor
     *
     * @param httpClient HttpClient
     * @param nvp        NameValuePair
     * @throws UnsupportedEncodingException
     */
    public ProcessRequest(HttpClient httpClient,
                          List<NameValuePair> nvp) throws UnsupportedEncodingException {
        this.httpClient = httpClient;
        this.httpUriRequest = new HttpPost(AccountProcessor.MY3_URL);
        ((HttpEntityEnclosingRequestBase) this.httpUriRequest)
                .setEntity(new UrlEncodedFormEntity(nvp, "utf-8"));

    }

    /**
     * @return Returns resulting HTML of the request as string
     * @throws IOException
     */
    public String process() throws IOException {

        HttpResponse httpResponse = httpClient.execute(httpUriRequest);
        HttpEntity httpEntity = httpResponse.getEntity();

        String pageContent = HtmlUtilities.getPageContent(httpEntity);
        if (httpEntity != null)
            EntityUtils.consume(httpEntity);

        return pageContent;

    }

}
