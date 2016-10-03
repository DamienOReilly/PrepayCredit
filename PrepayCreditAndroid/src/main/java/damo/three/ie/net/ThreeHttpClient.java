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

import android.content.Context;
import damo.three.ie.R;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class ThreeHttpClient {

    private static ThreeHttpClient threeHttpClient;
    private final Context context;
    private DefaultHttpClient httpClient;

    private ThreeHttpClient(Context context) throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException {
        this.context = context;
        this.httpClient = new DefaultHttpClient(ThreeHttpParameters.getParameters());
    }

    /**
     * Singleton for our HttpClient - on demand!
     *
     * @param context Application context
     * @return ThreeHttpClient a reference to our HttpClient singleton
     */
    public static ThreeHttpClient getInstance(Context context) throws CertificateException, NoSuchAlgorithmException,
            KeyStoreException, IOException {

        if (threeHttpClient == null) {
            threeHttpClient = new ThreeHttpClient(context);
        }

        return threeHttpClient;
    }

    public DefaultHttpClient getHttpClient() {
        return httpClient;
    }
}