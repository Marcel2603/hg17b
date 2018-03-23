import java.io.IOException;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
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
   
   
    
}
