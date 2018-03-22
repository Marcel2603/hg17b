import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class KeyAdder {
    static KeyStore ks, ksNew;
    static char[] PASSWORD = "password".toCharArray();
    static String FILENAME = "trustStore";

    public static void main(String[] args) {
        String email = args[0];
        String newFileName = args[1];
        FileInputStream fis = null, fis2 = null;
        Security.addProvider(new BouncyCastleProvider());
        try {
            ks = KeyStore.getInstance("jks");
            ksNew = KeyStore.getInstance("bks");
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
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

}
