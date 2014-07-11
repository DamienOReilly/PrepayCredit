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

import damo.three.ie.prepay.Constants;
import org.apache.http.HttpVersion;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

class ThreeHttpParameters {

    /**
     * Default HttpParams to use with this application
     *
     * @return {@link HttpParams}
     */
    public static HttpParams getParameters() {

        HttpParams httpParameters = new BasicHttpParams();

        final int timeoutSocket = 3 * 60 * 1000;
        final int timeoutConnection = 3 * 60 * 1000;

        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
        HttpProtocolParams.setUserAgent(httpParameters, Constants.USER_AGENT);
        return httpParameters;
    }
}
