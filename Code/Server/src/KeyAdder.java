import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.sql.Date;

import javax.security.auth.x500.X500Principal;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;


import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class KeyAdder {
    static KeyStore ks, ksNew;
    static char[] PASSWORD = "password".toCharArray();
    static String FILENAME = "trustStore";

    public static void main(String[] args) {
        String email = args[0];
        String newFileName = args[1];
        FileInputStream fis = null, fis2 = null;
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        try {
            ks = KeyStore.getInstance("jks");
            ksNew = KeyStore.getInstance("bks", "BC");
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        FileOutputStream out = null;
        try {
            File f = new File(FILENAME);
            fis = new FileInputStream(f);
            System.out.println(f.getAbsolutePath());
            ks.load(fis, PASSWORD);
            fis.close();
            File f2 = new File(newFileName);
            fis = new FileInputStream(f2);
            ksNew.load(fis, PASSWORD);
            ks.setCertificateEntry(email, ksNew.getCertificate(email));
            File f3 = new File(FILENAME);
            KeyStore.ProtectionParameter protParam =
                    new KeyStore.PasswordProtection(PASSWORD);
            //PrivateKey priv = (PrivateKey) ksNew.getKey("q", PASSWORD);
            ksNew.getEntry(email, protParam);
            //Key priv = ksNew.getKey(email, protParam);
            Certificate cert[] = new Certificate[1];
            cert[0] = ksNew.getCertificate(email); 
            
            Entry skEntry = ksNew.getEntry(email, protParam);
            //KeyStore.PrivateKeyEntry skEntry =
              //      new KeyStore.PrivateKeyEntry(priv, cert);
            ks.setEntry(email, skEntry, protParam);
            out = new FileOutputStream(f3);
            System.out.print(f.getAbsolutePath());
            ks.store(out, PASSWORD);
            System.out.print("Key added");
        } catch (Exception e) {
            System.out.println("No such key.");
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static KeyPair getKeyPair(final KeyStore keystore, 
            final String alias, final String password) {
          Key key = null;
        try {
            key = (PrivateKey) keystore.getKey(alias, password.toCharArray());
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

          Certificate cert = null;
        try {
            cert = ks.getCertificate(alias);
        } catch (KeyStoreException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            cert = keystore.getCertificate(alias);
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
          final PublicKey publicKey = cert.getPublicKey();

          return new KeyPair(publicKey, (PrivateKey) key);
        }
    
    
    public static Certificate[] selfSign(KeyPair keyPair) throws CertificateException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IllegalStateException, SignatureException {
        // generate a key pair
        // build a certificate generator
        @SuppressWarnings("deprecation")
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal dnName = new X500Principal("cn=example");

        // add some options
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setSubjectDN(new X509Name("dc=name"));
        certGen.setIssuerDN(dnName); // use the same
        // yesterday
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        // in 2 years
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000));
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping));

        // finally, sign the certificate with the private key of the same KeyPair
        Certificate cert[] = new Certificate[1];
        cert[0] = certGen.generate(keyPair.getPrivate(), "BC");
        return cert;
    }

}
