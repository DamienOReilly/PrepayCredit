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

package damo.three.ie.servlet.net;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ThreeHttpClient {

    /**
     * Setup a HttpClient with with our custom keystore.
     *
     * @return HttpClient
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     */
    public HttpClient getHttpClient() throws KeyManagementException,
            UnrecoverableKeyException, NoSuchAlgorithmException,
            KeyStoreException, CertificateException, IOException {

        // TOMCAT and many others don't have Entrust's cert down as trusted.
        // I have manually added their cert to prevent MITM attacks.
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream jks = getClass().getClassLoader().getResourceAsStream(
                "My3.jks");

        char pwd[] = "this_is_my3_keystore".toCharArray();

        ks.load(jks, pwd);
        jks.close();

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        TrustManager[] tm = tmf.getTrustManagers();

        KeyManagerFactory kmfactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(ks, pwd);
        KeyManager[] km = kmfactory.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(km, tm, null);

        TrustStrategy trustStrategy = new TrustStrategy() {

            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {

                return false;
            }

        };

        SSLSocketFactory sslsf = new SSLSocketFactory("TLS", null, null, ks,
                null, trustStrategy, new AllowAllHostnameVerifier());

        Scheme httpsScheme = new Scheme("https", 443, sslsf);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);

        PoolingClientConnectionManager conn = new PoolingClientConnectionManager(
                schemeRegistry);

        return new DefaultHttpClient(conn, ThreeHttpParameters.getParameters());
    }

}
