/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import sun.security.x509.X509Key;
import sun.security.pkcs.PKCS8Key;
import sun.security.x509.X500Name;

import java.util.Base64;

/**
 * this class generates , saves and loads the rsa keys 
 */
public class sshkey {
	private static PublicKey publickey = null;
	private static PrivateKey privateKey = null;
    private static KeyPairGenerator kpg;
    private static ByteArrayOutputStream bs = null;
    private static KeyPair keypair;
	private static sshkey thisinstance;
    private String signaturealgorithm= "MD5WithRSA";
	private static Base64.Encoder encoder = Base64.getEncoder();
	private static Base64.Decoder decoder = Base64.getDecoder();
	
	/**
	 * return the current used Signaturalgorithm 
	 */
	public String getSignaturealgorithm() {
        return signaturealgorithm;
    }
	
	/**
	 * replace the old Siginaturealgorithm 
	 * with a new one
	 */
	 public void setSignaturealgorithm(String signaturealgorithm) {
        this.signaturealgorithm = signaturealgorithm;
    }
	
	/**
	 * constructor creates a keypair 
	 */
	private sshkey() {
        try {
           kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.print("No such algorithm RSA in constructor csrgenerator\n");
        }
        kpg.initialize(2048);
        keypair = kpg.generateKeyPair();
        publickey = keypair.getPublic();
        privateKey = keypair.getPrivate();
    } 
	
	/** Generates a new key pair 
    *
    * @param int bits 
    *   this is the number of bits in modulus must be 512, 1024, 2048  or so on 
    */
     public KeyPair generateRSAkys(int bits) {
		try {
           kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.print("No such algorithm RSA in constructor csrgenerator\n");
        }
          kpg.initialize(bits);
            keypair = kpg.generateKeyPair();
            publickey = keypair.getPublic();
            privateKey = keypair.getPrivate();
            KeyPair dup= keypair;
     return dup;
    }
	
	/**
	 * returns the publickey 
	 * @return publickey
	 */
	public  PublicKey getPublicKey() {
        return publickey;
    }
	
	public static sshkey getInstance() {
            if (thisinstance == null)
                thisinstance = new sshkey();
            return thisinstance;
        }
	
	/**
	 * returns the privatekey
     * @return privtatekey
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
	/**
     * saves private key to a file
     * @param filename
     */
    public  void savePrivateKey(String filename) {
        PKCS8EncodedKeySpec pemcontents=null;
        pemcontents= new PKCS8EncodedKeySpec( privateKey.getEncoded());
        PKCS8Key pemprivatekey= new  PKCS8Key( );
        try {
            pemprivatekey.decode(pemcontents.getEncoded());
        } catch (InvalidKeyException e) {

            e.printStackTrace();
        }
        File file=new File(filename);
        try {

            file.createNewFile();
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(pemprivatekey.getEncoded());
            fos.flush();
            fos.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

	}
	
	/** 
	 * rloads a private Key from a .der file 
	 * @return the private key
	 * @param filename
	 */
	 public static PrivateKey loadPrivateKey(String filename)
	throws Exception {

    byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

    PKCS8EncodedKeySpec spec =
      new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePrivate(spec);
  }
	/**
	 * saves a public key in a .der file 
	 * @param filename
	 */
	 
    public  void savePublicKey(String filename) {
        X509EncodedKeySpec pemcontents=null;
        pemcontents= new X509EncodedKeySpec( publickey.getEncoded());
        X509Key pempublickey= new  X509Key();
        try {
            pempublickey.decode(pemcontents.getEncoded());
        } catch (InvalidKeyException e) {

            e.printStackTrace();
        }
        File file=new File(filename);
        try {

            file.createNewFile();
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(pempublickey.getEncoded());
            fos.flush();
            fos.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

	}
	
	/**
	 * loads a public key from a .der file
	 * @return the public key
	 * @param filename
	 */
  public static PublicKey loadPublicKey(String filename)
    throws Exception {

    byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

    X509EncodedKeySpec spec =
      new X509EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePublic(spec);
  }

	/**
     * Reads a Private Key from a pem base64 encoded file.
     * @param filename name of the file to read.
     * @param algorithm Algorithm is usually "RSA"
     * @return returns the privatekey which is read from the file;
     * @throws Exception
     */
    public  PrivateKey getPemPrivateKey(String filename, String algorithm) throws Exception {
          File f = new File(filename);
          FileInputStream fis = new FileInputStream(f);
          DataInputStream dis = new DataInputStream(fis);
          byte[] keyBytes = new byte[(int) f.length()];
          dis.readFully(keyBytes);
          dis.close();

          String temp = new String(keyBytes);
          String privKeyPEM = temp.replace("-----BEGIN PRIVATE KEY-----", "");
          privKeyPEM = privKeyPEM.replace("-----END PRIVATE KEY-----", "");
          System.out.println("Private key\n"+privKeyPEM);


          byte[] decoded = decoder.decode(privKeyPEM);

          PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
          KeyFactory kf = KeyFactory.getInstance(algorithm);
          return kf.generatePrivate(spec);
          }



    /**
     * Saves the private key to a pem file.
     * @param filename  name of the file to write the key into 
     * @param key the Private key to save.
     * @return  String representation of the pkcs8 object.
     * @throws Exception
     */
     /* public  String  SavePemPrivateKey(String filename) throws Exception {
        PrivateKey key=this.privateKey;
          File f = new File(filename);
          FileOutputStream fos = new FileOutputStream(f);
          DataOutputStream dos = new DataOutputStream(fos);


          byte[] keyBytes = key.getEncoded();
          PKCS8Key pkcs8= new PKCS8Key();
          pkcs8.decode(keyBytes);
          byte[] b=pkcs8.encode();
			String p = new String (b.toString());
				System.out.println("encode save");
				System.out.print(p);
		//String encode2 = new String(Base64.getEncoder().encode(
          String  encoded = encoder.encodeToString(p.getBytes());

          encoded= "-----BEGIN PRIVATE KEY-----\r\n" + encoded + "-----END PRIVATE KEY-----";

         dos.writeBytes(encoded);
         dos.flush();
         dos.close();

        // System.out.println("Private key\n"+ privKeyPEM);
        return pkcs8.toString();

          } */


    /**
     * Saves a public key to a base64 encoded pem file
     * @param filename  name of the file 
     * @param key public key to be saved 
     * @return string representation of the pkcs8 object.
     * @throws Exception
     */
    /* public  String  SavePemPublicKey(String filename) throws Exception {
        PublicKey key=this.publickey;  
        File f = new File(filename);
          FileOutputStream fos = new FileOutputStream(f);
          DataOutputStream dos = new DataOutputStream(fos);


          byte[] keyBytes = key.getEncoded();
          String  encoded = encoder.encodeToString(keyBytes);

          encoded= "-----BEGIN PUBLIC KEY-----\r\n" + encoded + "-----END PUBLIC KEY-----";

         dos.writeBytes(encoded);
         dos.flush();
         dos.close();

          //System.out.println("Private key\n"+privKeyPEM);
      return  encoded.toString();

          } */





       /**
     * reads a public key from a file
     * @param filename name of the file to read
     * @param algorithm is usually RSA
     * @return the read public key
     * @throws Exception
     */
   /*  public  PublicKey getPemPublicKey(String filename, String algorithm) throws Exception {
          File f = new File(filename);
          FileInputStream fis = new FileInputStream(f);
          DataInputStream dis = new DataInputStream(fis);
          byte[] keyBytes = new byte[(int) f.length()];
          dis.readFully(keyBytes);
          dis.close();

          String temp = new String(keyBytes);
          String publicKeyPEM = temp.replace("-----BEGIN PUBLIC KEY-----\n", "");
          publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");



         byte[] decoded = decoder.decode(publicKeyPEM);

          X509EncodedKeySpec spec =
                new X509EncodedKeySpec(decoded);
          KeyFactory kf = KeyFactory.getInstance(algorithm);
          return kf.generatePublic(spec);
          } */
		  
	/*public static void SaveKeyPair(String fnpub,String fnpri) throws IOException { 

		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
		publickey.getEncoded());
		FileOutputStream fos = new FileOutputStream(fnpub);
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();

		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		fos = new FileOutputStream(fnpri);
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	} */

	  
}