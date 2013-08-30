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
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
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

    private ThreeHttpClient(Context context) {
        this.context = context;
    }

    /**
     * Singleton for our HttpClient - on demand!
     *
     * @param context Application context
     * @return ThreeHttpClient a reference to our HttpClient singleton
     */
    public static ThreeHttpClient getInstance(Context context) {

        if (threeHttpClient == null) {
            threeHttpClient = new ThreeHttpClient(context);
        }

        return threeHttpClient;
    }

    /**
     * return a reference to our HttpClient with custom SSLSocketFactory
     *
     * @return HttpClient
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public HttpClient getHttpClient() throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {

        final KeyStore trusted = KeyStore.getInstance("BKS");

        // I included a BKS keystore with secure.damienoreilly.org's cert and my3account.three.ie's Root Cert
        // some older android devices don't have these certs in their trust list.
        final InputStream in = context.getResources()
                .openRawResource(R.raw.my3);
        try {

            trusted.load(in, "damopass".toCharArray());
        } finally {
            in.close();
        }

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https",
                new EasySSLSocketFactory(trusted), 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                ThreeHttpParameters.getParameters(), registry);

        return new DefaultHttpClient(ccm, ThreeHttpParameters.getParameters());
    }

}