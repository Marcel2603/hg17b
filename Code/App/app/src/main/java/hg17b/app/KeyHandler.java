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
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

/**
 * KeyHandler for managing RSA Keys stored in Bouncy Castle KeyStore.
 */

public class KeyHandler {
    /**
     * The apps KeyStore.
     */
    KeyStore ks;
    /**
     * The KeyStore Password.
     */
    char[] PASSWORD = "password".toCharArray();
    /**
     * Filename of the KeyStore
     */
    String FILENAME = "KeyStore";
    /**
     * KeyStore File.
     */
    File f;

    /**
     * Constructor of KeyHandler. Loads KeyStore from file
     * Creates new KeyStore if none exists.
     * @param cache
     */
    public KeyHandler (File cache){
        //Context context=this;
        //context = context.getApplicationContext();
        Security.addProvider(new BouncyCastleProvider());

        try {
            ks = KeyStore.getInstance("BKS", "BC");
        } catch (Exception e1) {
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

    /**
     * Creates a new key with email as alias.
     * @param email
     */
    public void addKey(String email){
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(4096, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        char[] password = PASSWORD;
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
        try {
            ks.getEntry(email, protParam);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates self signed Certificate from KeyPair.
     * @param keyPair
     * @return
     * @throws CertificateException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws IllegalStateException
     * @throws SignatureException
     */
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

    /**
     * Checks if key exists.
     * @param email alias of the key.
     * @return true if exists. False if not.
     */
    public boolean isAlias(String email){
        try {
            if(email.toLowerCase().equals(ks.getCertificateAlias(ks.getCertificate(email)))){
                return true;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the publicStore
     * @param email the email to which the ps belongs.
     * @return
     */
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

    /**
     * Returns the dir of the keystore.
     * @return directory of KeyStore.
     */
    public String getDir(){
        return f.getParent();
    }

    /**
     * Returns the KeyStore file.
     * @return
     */
    public File getFile(){
        return f;
    }

    /**
     * returns the handlers keystore
     * @return keystore
     */
    public KeyStore getKeyStore(){
        return ks;
    }






}
