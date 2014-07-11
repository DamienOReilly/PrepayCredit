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

import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Tweaked some code took from:
 * http://stackoverflow.com/questions/4115101/apache-httpclient-on-android-producing-certpathvalidatorexception
 * -issuername
 * <p/>
 * Using this as my3account.three.ie's certs are out of order !
 * Also I have my3account.three.ie's certs added to a keystore as Entrust's certs were not available on some Android
 * devices.
 * Cert validation is enforced to help prevent MiTM attacks.
 */
class EasySSLSocketFactory implements SocketFactory, LayeredSocketFactory {

    private static KeyStore keyStore = null;
    private SSLContext sslcontext = null;

    public EasySSLSocketFactory(KeyStore keyStore) {
        EasySSLSocketFactory.keyStore = keyStore;
    }

    /**
     * @return {@link SSLContext}
     * @throws IOException
     */
    private static SSLContext createEasySSLContext() throws IOException {
        try {
            SSLContext context = SSLContext.getInstance("TLS");

            KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmfactory.init(keyStore, "damopass".toCharArray());
            KeyManager[] km = kmfactory.getKeyManagers();

            context.init(km, new TrustManager[]{new EasyX509TrustManager(keyStore)}, null);
            return context;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Return an instance of our custom SSLContext
     *
     * @return {@link SSLContext}
     * @throws IOException
     */
    private SSLContext getSSLContext() throws IOException {
        if (this.sslcontext == null) {
            this.sslcontext = createEasySSLContext();
        }
        return this.sslcontext;
    }

    /**
     * @see org.apache.http.conn.scheme.SocketFactory#connectSocket(java.net.Socket,
     * java.lang.String, int, java.net.InetAddress, int,
     * org.apache.http.params.HttpParams)
     */
    @Override
    public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort,
                                HttpParams params) throws IOException {
        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);
        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

        if ((localAddress != null) || (localPort > 0)) {
            // we need to bind explicitly
            if (localPort < 0) {
                localPort = 0; // indicates "any"
            }
            InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
            sslsock.bind(isa);
        }

        sslsock.connect(remoteAddress, connTimeout);
        sslsock.setSoTimeout(soTimeout);
        return sslsock;
    }

    /**
     * @see org.apache.http.conn.scheme.SocketFactory#createSocket()
     */
    @Override
    public Socket createSocket() throws IOException {
        return getSSLContext().getSocketFactory().createSocket();
    }

    /**
     * @see org.apache.http.conn.scheme.SocketFactory#isSecure(java.net.Socket)
     */
    @Override
    public boolean isSecure(Socket socket) throws IllegalArgumentException {
        return true;
    }


    /**
     * @see org.apache.http.conn.scheme.LayeredSocketFactory#createSocket(java.net.Socket,
     * java.lang.String, int, boolean)
     */
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    // -------------------------------------------------------------------
    // javadoc in org.apache.http.conn.scheme.SocketFactory says :
    // Both Object.equals() and Object.hashCode() must be overridden
    // for the correct operation of some connection managers
    // -------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        return ((obj != null) && obj.getClass().equals(EasySSLSocketFactory.class));
    }

    @Override
    public int hashCode() {
        return EasySSLSocketFactory.class.hashCode();
    }
}