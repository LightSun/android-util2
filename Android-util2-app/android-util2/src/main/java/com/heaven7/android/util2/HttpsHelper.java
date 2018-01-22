package com.heaven7.android.util2;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * https helper.
 * @author heaven7
 * @since 1.0.2
 */
public class HttpsHelper {

    /**
     * create ssl socket factory. by target crt file.
     * @param context the context.
     * @param assetsFilePath the crt file path in assets.
     * @return an instance of SSLSocketFactory.
     */
    public static SSLSocketFactory createSSLSocketFactory(Context context, String assetsFilePath) {
        SSLContext sslContext = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream certificates = new BufferedInputStream(context.getAssets().open(assetsFilePath));
            Certificate ca;
            try {
                ca = certificateFactory.generateCertificate(certificates);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                certificates.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            sslContext = SSLContext.getInstance("TLSv1", "AndroidOpenSSL");
//            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sslContext != null ? sslContext.getSocketFactory() : null;
    }
}