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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
    private SSLServerSocket sslServerSocket;
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
            PrintWriter writer = new PrintWriter(out);
//            Kommunikation von Client zum Server
            InputStream in = client.getInputStream();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in));
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
                    /*if (recieve.equals("Eventpast")) {
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsStudents(true);
                        for (int i = 0;
                                i < list.size(); i++) {
                            System.out.println(list.get(i).get("label"));
                            writer.write(list.get(i).get("label") + "\n");
                            writer.flush();
                        }
                        writer.write("ENDE" + "\n");
                        writer.flush();
                        System.out.println("ENDE");
                    }*/
                    if (recieve.equals("Eventpast")) {
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsStudents(true);
                        
                        
                        
                        JSONObject obj;
                        for (int i = 0; i < list.size(); i++) {
                            obj = new JSONObject();
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
                    System.out.println("Blah");
                    if (recieve.equals("Ueberpruefe Email")) {
//                      getEmail
                       recieve = reader.readLine();
                       email = recieve;
                       if (true) { //db1.isOrganizer(receive) sollte da rein
                           KeyHandler ks = new KeyHandler();
                           //HAS KEY
                           //ELSE
                           //mail.senden("marcelemail2603@gmail.com");
                           writer.write("true\n");
                           writer.flush();
                           System.out.println("isEmail");
                           System.out.println(recieve);
                           if (ks.isAlias(recieve)) {
                               System.out.println("isAlias");
                               writer.write("keyExists\n");
                               writer.flush();
                               sslConnect(ks, email);
                           } else{
                               System.out.println("noAlias");
                               writer.write("noKeyExists\n");
                               writer.flush();
                               String random = "random";
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
                           }


                       } else {
                                writer.write("false" + "\n");
                                writer.flush();
                       }
                    }
                    if (recieve.equals("Eventpast")) {
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsOrganizer(email, true);
                        for (int i = 0;
                                i < list.size(); i++) {
                            writer.write(list.get(i).get("start") + "\n");
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
          }
            }
            System.out.println("Client: "
                    + client.getInetAddress() + ":"
                    + client.getPort() + " ist offline.");
            Server.deleteSocket(client);
            writer.close();
            reader.close();
            client.close();
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
private void sslConnect(KeyHandler kh, String email){
        KeyManagerFactory kmfactory = null;
        SSLContext sslContext = null;
        try {
            kmfactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            System.out.println(email);
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
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            System.out.println("SSLClient connected.1");
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            System.out.println("SSLClient connected.2");
            PrintWriter writer = new PrintWriter(out);
            System.out.println("SSLClient connected.3");
            System.out.println("SSLClient connected.4");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in));
            System.out.println(Arrays.toString(socket.getEnabledCipherSuites()));
            System.out.println("SSLClient connected.5");
            System.out.println(reader.readLine());
            System.out.println("SSLClient connected.6");
            writer.write("HALLOSSSLSOCKETICHBINEINSERVER\n");
            writer.flush();
            System.out.println("SSLClient connected.7");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}












