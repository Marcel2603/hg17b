package hg17b.app;

import android.content.Context;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

/**
 *
 */

public class KeyHandler {
    KeyStore ks;
    char[] PASSWORD = "password".toCharArray();
    String FILENAME = "KeyStore";
    File f;

    public KeyHandler (File cache){
        //Context context=this;
        //context = context.getApplicationContext();
        Security.addProvider(new BouncyCastleProvider());
        try {
            ks = KeyStore.getInstance("BKS");
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
        }


        f = new File(cache, "KeyStore");
        FileInputStream fis = null;
        try {
            fis=new FileInputStream(f);
            try {
                ks.load(fis, PASSWORD);
                System.out.println("KeyStore gefunden und geladen. " + cache.toString());
            } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Keystore nicht gefunden, Datei wird erstellt.");
            FileOutputStream fos = null;
            try {
                ks.load(null, PASSWORD);
                fos = new FileOutputStream(f);
                ks.store(fos, PASSWORD);
                System.out.println("KeyStore erstellt und gespeichert.");
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public void addKey(String email){
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(4096, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        char[] password = "password".toCharArray();
        KeyStore.ProtectionParameter protParam =
                new KeyStore.PasswordProtection(password);
        FileOutputStream fos = null;
        try {
            KeyStore.PrivateKeyEntry skEntry =
                    new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), selfSign(keyPair));
            ks.setEntry(email, skEntry, protParam);
            fos = new java.io.FileOutputStream(f);
            ks.store(fos, password);
        } catch (InvalidKeyException | SignatureException | NoSuchProviderException |
                NoSuchAlgorithmException | IOException | KeyStoreException | CertificateException  e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
    public boolean isAlias(String email){
        try {
            System.out.println(ks.getCertificateAlias(ks.getCertificate(email)));
            if(email.toLowerCase().equals(ks.getCertificateAlias(ks.getCertificate(email)))){
                return true;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }
    public KeyStore getPublicStore(String email){
        KeyStore ps=null;
        try {
           ps = KeyStore.getInstance(KeyStore.getDefaultType());
           ps.load(null, PASSWORD);
           ps.setCertificateEntry(email, ks.getCertificate(email));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return ks;
    }
    public String getDir(){
        System.out.println(f.getParent());
        return f.getParent();
    }
    public File getFile(){
        return f;

    }
    public KeyStore getKeyStore(){
        return ks;
    }






}
