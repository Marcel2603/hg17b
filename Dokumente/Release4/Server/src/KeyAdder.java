import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStoreException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Adds Key to Server TrustStore.
 * @author Nikolai Kortenbruck
 *
 */
public class KeyAdder {
    /**
     * Old and new KeyStore.
     */
    private static KeyStore ks, ksNew;
    /**
     * KeyStore Password.
     */
    private static char[] PASSWORD = "password".toCharArray();
    /**
     * Filename to trustStore to which key will be added.
     */
    private static String FILENAME = "trustStore";
    /**
     * Adds a key from keyfile to Server TrustStore.
     * @param args args[0] is the alias of the key to be added.
     * args[1] the name of the temporary KeyStore created. 
     */
    public static void main(String[] args) {
        String email = args[0];
        String newFileName = args[1];
        FileInputStream fis = null, fis2 = null;
        Security.addProvider(new BouncyCastleProvider());
        try {
            ks = KeyStore.getInstance("jks");
            ksNew = KeyStore.getInstance("bks");
        } catch (KeyStoreException e1) {

        }
        FileOutputStream out = null;
        try {
            File f = new File(FILENAME);
            fis = new FileInputStream(f);
            ks.load(fis, PASSWORD);
            fis.close();
            File f2 = new File(newFileName);
            fis = new FileInputStream(f2);
            ksNew.load(fis, PASSWORD);
            File f3 = new File(FILENAME);
            KeyStore.ProtectionParameter protParam =
                    new KeyStore.PasswordProtection(PASSWORD);
            Entry skEntry = ksNew.getEntry(email, protParam);
            ks.setEntry(email, skEntry, protParam);
            out = new FileOutputStream(f3);
            ks.store(out, PASSWORD);
            System.out.print("Key added");
        } catch (Exception e) {
            System.out.println("No such key.");

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {

                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {

                }
            }
        }
    }
}
