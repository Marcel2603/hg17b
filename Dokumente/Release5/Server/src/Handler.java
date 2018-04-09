import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.PupilDB;

/**
 * Handle the communication between Server, Client and DB.
 * @author marcel
 *
 */
public class Handler implements Runnable {
    /**
     * Die URL zur activate.php.
     */
    static final String URL = "pcai042.informatik.uni-leipzig.de/~hg17b/activate.php";
    /**
     * Der Port für die SSL Verbindung.
     */
    static final int SSLPORT = 1832;
    /**
     * Socket for the Client.
     */
    private Socket client;
    /**
     * DB with pupildata.
     */
    private PupilDB db1;
    /**
     * Mail-Konto.
     */
    private Mail mail;
    /**
     * SSL-Socket.
     */
    private SSLSocket sslSocket = null;
    /**
     * Writer fuer SSH Zwecke.
     */
    PrintWriter writer;
    /**
     * Reader fuer SSH Zwecke.
     */
    BufferedReader reader;
    /**
     * Konstruktor to create the Thread.
     * @param socket Clientsocket.
     * @param pupilDB DB with pupildata.
     * @param mail Email-Konot des Servers.
     */
    public Handler(final Socket socket, final PupilDB pupilDB, final Mail mail) {
        this.client = socket;
        this.db1 = pupilDB;
        this.mail = mail;
    }
    /**
     * Manage and run the Client.
     */
    public void run() {
        try {
//            Kommunikation von Server zum Client
            OutputStream out = client.getOutputStream();
            writer = new PrintWriter(out);
//            Kommunikation von Client zum Server
            InputStream in = client.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            String recieve = "";
            HashMap<Integer, Integer> ranking;
            int id = 0;
            recieve  = reader.readLine();
            //case für schueler und veranstalter
            int pers = 0;
            if (recieve.equals("schueler")) {
                pers = 1;
            }
            if (recieve.equals("veranstalter")) {
                pers = 2;
            }
//            Solange der Client nicht offline ist.
            String email = "";
            while (!recieve.equals("disconnect")) {
                //Schueler-Verbindung
                if (pers == 1) {
                    if (recieve.equals("Ueberpruefe ID")) {
//                       getID
                        recieve = reader.readLine();
                        id = Integer.parseInt(recieve);
                        boolean inID = db1.isID(id);
                        if (inID) {
                            writer.write("true\n");
                            writer.flush();
                        } else {
                                 writer.write("false" + "\n");
                                 writer.flush();
                        }
                    }
                    if (recieve.equals("Punkte")) {
                            Integer points = db1.getScore(id);
                            writer.write(points.toString() + "\n");
                            writer.flush();
                    }
                    if (recieve.equals("Rangliste")) {
                        ranking = db1.getToplist();
                        for (int i = 1; i <= ranking.size(); i++) {
                            writer.write(ranking.get(i) + "\n");
                            writer.flush();
                        }
                        writer.write(db1.getRank(id) + "\n");
                        writer.flush();
                    }
                    if (recieve.equals("Eventpast")) {
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsStudents(id);
                        JSONObject obj;
                        for (int i = 0; i < list.size(); i++) {
                            obj = new JSONObject();
                            obj.put("id", list.get(i).get("ID"));
                            obj.put("label", list.get(i).get("label"));
                            obj.put("address", list.get(i).get("address"));
                            obj.put("url", list.get(i).get("url"));
                            obj.put("description", list.get(i).get("description"));
                            obj.put("start", list.get(i).get("start"));
                            writer.write(obj.toString() + "\n");
                            writer.flush();
                        }
                        writer.write("ENDE" + "\n");
                        writer.flush();
                    }
                    if (recieve.equals("Anmelden")) {
                    	//INT des Events erhoehen.
                    	//zuerst ID , dann event
                    }
                    if (recieve.equals("Abmelden")) {
                    	//int des Events verringern.
                    	//zuerst ID, dann event
                    }
                    if (recieve.equals("Event")) {
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsStudents(false);
                        for (int i = 0;
                                i < list.size(); i++) {
                            writer.write(list.get(i).get("label") + "\n");
                            writer.flush();
                        }
                        writer.write("ENDE" + "\n");
                        writer.flush();
                    }
                    recieve = reader.readLine();
                }
                //Veranstalter
                if (pers == 2) {
                    recieve = reader.readLine();
                    if (recieve.equals("Ueberpruefe Email")) {
//                      getEmail
                       recieve = reader.readLine();
                       email = recieve;
                       if (db1.isOrganizer(email)) {
                           KeyHandler ks = new KeyHandler();
                           //HAS KEY
                           //ELSE
                           writer.write("true\n");
                           writer.flush();
                          
                           
                           /*Temporarily deactivated part because key activation doesn't work.
                            * 
                            * 
                            * 
                            if (ks.isAlias(recieve)) { //If a key already exists in Server Keystore
                               writer.write("keyExists\n");
                               writer.flush();
                             //creates SSL Socket and connects reader and writer with it.
                               sslConnect(ks, email); 
                           } else { //If there is no key start transmission and save it with random filename.
                               writer.write("noKeyExists\n");
                               writer.flush();
                               String random = randomString();
                               System.out.println(random);
                               String link = URL + "?id="
                                       + email + "&key=" + random;
                               System.out.println(link);
                               mail.senden("marcelemail2603@gmail.com", link);
                               try {
                                   out = new FileOutputStream(random);
                               } catch (FileNotFoundException ex) {
                                   System.out.println("File not found. ");
                               }

                               byte[] bytes = new byte[8192];

                               int count;
                               while ((count = in.read(bytes)) > 0) {
                                   out.write(bytes, 0, count);
                               }
                               break;
                           }*/


                       } else {
                                writer.write("false" + "\n");
                                writer.flush();
                       }
                    }
                    /*
                     * At this point writer and reader
                     * are already linked with SSLSocket.
                     */
                    if (recieve.equals("Eventpast")) {
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsOrganizer(email, true);
                        JSONObject obj = new JSONObject();
                        for (int i = 0;
                                i < list.size(); i++) {
                        	obj = new JSONObject();
                        	obj.put("id", list.get(i).get("ID"));
                            obj.put("label", list.get(i).get("label"));
                            obj.put("address", list.get(i).get("address"));
                            obj.put("url", list.get(i).get("url"));
                            obj.put("description", list.get(i).get("description"));
                            obj.put("start", list.get(i).get("start"));
                            writer.write(obj.toString() + "\n");
                            writer.flush();
                        }
                        writer.write("ENDE" + "\n");
                        writer.flush();
                    }
                    if (recieve.equals("Event")) {
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsOrganizer(email, false);
                        for (int i = 0;
                                i < list.size(); i++) {
                            writer.write(list.get(i).get("label") + "\n");
                            writer.flush();
                        }
                        writer.write("ENDE" + "\n");
                        writer.flush();
                    }
                    if (recieve.equals("Anmelden")) {
                    	String eventid = reader.readLine();
                    	String schuelerid = reader.readLine();
                    	db1.addAttendance(Integer.parseInt(schuelerid), eventid);
                    	recieve = " ";
                    }
           }
            }
            Calendar cal = Calendar.getInstance();
            Date time = cal.getTime();
            DateFormat formatter = new SimpleDateFormat();
            System.out.println(formatter.format(time) + "Client: "
                    + client.getInetAddress() + " ist offline.");
            Server.deleteSocket(client);
            writer.close();
            reader.close();
            client.close();
            if (sslSocket != null) {
                sslSocket.close();
            }
        } catch (SocketException e) {
            Server.deleteSocket(client);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Server.deleteSocket(client);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * Creates and opens the SSLServerSocket and SSLSocket with key from email.
     * Sets the writer and reader streams to SSLSocket
     * @param kh The KeyHandler.
     * @param email The email of connecting Organizer.
     */
    private void sslConnect(final KeyHandler kh, final String email) {
        KeyManagerFactory kmfactory = null;
        SSLContext sslContext = null;
        try {
            kmfactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            kmfactory.init(kh.getKeyStore(email), "password".toCharArray());
            Security.addProvider(new BouncyCastleProvider());
            sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory mgrFact =
                    KeyManagerFactory.getInstance("SunX509");
            mgrFact.init(kh.getKeyStore(email), "password".toCharArray());
            TrustManagerFactory trustFact =
                    TrustManagerFactory.getInstance("SunX509");
            trustFact.init(kh.getKeyStore(email));
            sslContext.init(mgrFact.getKeyManagers(), trustFact.getTrustManagers(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SSLServerSocketFactory fact = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) fact.createServerSocket(SSLPORT);
            sslSocket = (SSLSocket) serverSocket.accept();
            OutputStream out = sslSocket.getOutputStream();
            InputStream in = sslSocket.getInputStream();
            writer = new PrintWriter(out);
            reader = new BufferedReader(new InputStreamReader(in));
            System.out.println("SSLClient connected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Creates a random String of length 10.
     * @return random String.
     */
    private String randomString(){
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lower = upper.toLowerCase();
        final String digits = "0123456789";
        final String alphanum = upper + lower + digits;
        String random = "";
        for (int i = 0; i < 10; i++){
            int randomNum = ThreadLocalRandom.current().nextInt(0, alphanum.length());
            random = random + alphanum.charAt(randomNum);
        }
        return random;
    }
}












