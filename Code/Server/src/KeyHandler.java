import java.io.IOException;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.KeyStore.Entry;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 * Jo
 * @author Korti
 *
 */
public class KeyHandler {
   static final char[] PASSWORD="password".toCharArray() ;
   KeyStore ks;
   static final String STORE = "trustStore";
   public KeyHandler(){
       ks = null;
       char[] password;
       Security.addProvider(new BouncyCastleProvider());
       try {
           ks = KeyStore.getInstance("jks");
       } catch (KeyStoreException e1) {
           e1.printStackTrace();
       }
       password = "password".toCharArray();

        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(STORE);
            ks.load(fis, PASSWORD);
        } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
   public KeyStore getKeyStore(String email){
       KeyStore ksNew = null;
       char[] password;
       Security.addProvider(new BouncyCastleProvider());
       try {
           ksNew = KeyStore.getInstance("jks");
       } catch (KeyStoreException e1) {
           e1.printStackTrace();
       }
       try {
           ksNew.load(null, PASSWORD);
       } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
           e.printStackTrace();
       }
       KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(PASSWORD);

       try {
           Entry skEntry = ks.getEntry(email, protParam);
           ksNew.setEntry(email, skEntry, protParam);
       } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
           e.printStackTrace();
       }
       return ksNew;
   }


   public boolean isAlias(final String email){
       try {
           if (ks.containsAlias(email)){
               return true;
           }
    } catch (KeyStoreException e) {
        e.printStackTrace();
    }
       return false;
   }
}
