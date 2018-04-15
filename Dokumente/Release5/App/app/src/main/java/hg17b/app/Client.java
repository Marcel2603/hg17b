/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * The class Client handles the connection to the Server,
 * and regulates the communication between App and Server(Database).
 */
public class Client extends AsyncTask<Void, Void, Void>{

    private String ip;
    private int port;
    private Socket socket = null;
    private SSLSocket sslSocket=null;
    private boolean noServer = false;
    private int entscheidung = 0;
    public int anzahl;
    private Context context;
    /** Variable for if Events should be refreshed (received from Server)*/
    public boolean refreshEvents = false;

    /**The Writer that writes to the Server*/
    private PrintWriter writer;
    /**The Reader that reads what the Server sent*/
    private BufferedReader reader;
    KeyHandler kh;

    /**
     * public constructor from Client
     * @param ip - the ip address we want to reach
     * @param port - the port to connect with the Server
     */
    public Client(String ip, int port, int entscheidungen, KeyHandler kh, Context context) {
        this.kh = kh;
        this.ip = ip;
        this.port = port;
        this.entscheidung = entscheidungen;
        this.context = context;
    }


    /**
     * returns int ip
     * @return
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * returns int port
     * @return
     */
    public int getPort() {
        return this.port;
    }

    /**
     * returns int entscheidung
     * @return
     */
    public int getEntscheidung() {
        return this.entscheidung;
    }

    /**
     * This method creats an background thread, that connects with the Server.
     * Here, we send and receive Data from the Database behind the Server.
     * @param args0
     */
    @Override
    protected Void doInBackground(Void... args0) {


        try {
            /* first read Events from local files, set refreshEvents on true if it's not working*/
            if (!readEvents(entscheidung)) {
                refreshEvents = true;
            }


            socket = new Socket(ip,port);
            System.out.println("Client online");
            OutputStream out = socket.getOutputStream();
            writer = new PrintWriter(out);
            InputStream in = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));

            //schueler
            if (entscheidung == 1) {
                writer.write("schueler" + "\n");
                writer.flush();
                System.out.println("Schueler gesendet");
                boolean schleife = true;
                int stop = 0;

                while (schleife) {
                    //test Punkte
                    if (StartActivity.isclicked) {
                        writer.write("Ueberpruefe ID" + "\n");
                        writer.flush();
                        writer.write(StartActivity.data + "\n");
                        writer.flush();
                        String vonServer = reader.readLine();

                        if (vonServer.equals("true")) {

                            writer.write("Punkte" + "\n");
                            writer.flush();

                            //get punkte
                            System.out.println("punkte");
                            vonServer = reader.readLine();
                            System.out.println(vonServer);
                            int points = Integer.parseInt(vonServer);
                            //if the points updated refresh them
                            if (points != StartActivity.getPoints()) {
                                StartActivity.setPoints(points);
                                BufferedWriter bw = null;
                                try {
                                    File f = new File(context.getCacheDir(),"points.tmp");
                                    bw = new BufferedWriter(new FileWriter(f));
                                    bw.write(points);
                                    bw.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (bw != null) {
                                        bw.close();
                                    }
                                }
                            }
                            StartActivity.isinDB = true;
                            StartActivity.kontrolle = 1;
                            writer.write("Rangliste" + "\n");
                            writer.flush();
                            for (int i = 1; i <= 10; i++) {
                                Ranking.score.put(i, reader.readLine());
                                System.out.println(Ranking.score.get(i));
                            }
                            Ranking.rang = reader.readLine();

                            //get the Events
                            if (refreshEvents) {
                                receiveEvents(entscheidung);
                                refreshEvents=false;
                            }
                        } else {
                            StartActivity.isinDB = false;
                            StartActivity.kontrolle = 1;
                            writer.write("disconnect" + "\n");
                            writer.flush();
                            writer.close();
                            socket.close();
                        }
                        StartActivity.isclicked = false;
                    }
                    if (StartActivity.anmelden) {
                        writer.write("Anmelden" + "\n");
                        writer.flush();
                        writer.write(StartActivity.data + "\n");
                        writer.flush();
                        writer.write(StartActivity.eventID + "\n");
                        writer.flush();
                    }
                    if (StartActivity.abmelden) {
                        writer.write("Abmelden" +"\n");
                        writer.flush();
                        writer.write(StartActivity.data + "\n");
                        writer.flush();
                        writer.write(StartActivity.eventID + "\n");
                        writer.flush();
                    }
                    if (LogOut.isclicked) {
                        schleife = false;
                        writer.write("disconnect" + "\n");
                        writer.flush();

                        System.out.println("DISCONNECT");
                        writer.close();
                        socket.close();
                        LogOut.isclicked = false;
                    }
                }
            }
            //Veranstalter
            if (entscheidung == 2) {
                System.out.println("HILEF");
                writer.write("veranstalter" + "\n");
                writer.flush();
                boolean schleife1 = true;
                int stop = 0;
                int t = 0;
                while (schleife1) {
                    if (t == 0) {
                        String email = OrganizerLogIn.nutzer;
                        writer.write("Ueberpruefe Email" + "\n");
                        writer.flush();
                        writer.write(email + "\n");
                        writer.flush();
                        String vonServer = reader.readLine();
                        if (vonServer.equals("true")) {

                            /*
                            Temporarily deactivated key stuff because activation doesn't work.

                            vonServer = reader.readLine();
                            if(vonServer.equals("keyExists")){
                                System.out.println("key EXISTS!!!");
                                sslConnect();
                            } else if(vonServer.equals("noKeyExists")) {
                                KeyStore ks=null;
                                try {
                                    if(!kh.isAlias(email)) {
                                        kh.addKey(email);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ks = kh.getPublicStore(email);
                                OutputStream outServer = socket.getOutputStream();
                                try {
                                    File f = new File(kh.getDir(), "temp");
                                    FileOutputStream temp = new FileOutputStream(f);
                                    ks.store(temp, "password".toCharArray());
                                    temp.close();
                                    long length = f.length();
                                    byte[] bytes = new byte[8192];
                                    InputStream inKey = new FileInputStream(f);
                                    int count;
                                    while ((count = inKey.read(bytes)) > 0) {
                                        outServer.write(bytes, 0, count);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }*/


                            //get the Events
                            refreshEvents = true;
                            System.out.println(refreshEvents + "TEST");
                            if (refreshEvents) {
                                receiveEvents(entscheidung);
                                refreshEvents = false;
                            }
                            OrganizerLogIn.isinDB = true;
                            OrganizerLogIn.schleife = 0;
                            t = 1;

                        } else {
                            OrganizerLogIn.isinDB = false;
                            OrganizerLogIn.schleife = 0;
                            writer.write("disconnect" + "\n");
                            writer.flush();
                            writer.close();
                            socket.close();
                            LogOut.isclicked = false;
                            schleife1 = false;
                            t = 1;
                        }
                    }
                    if (OrganizerMain.veranstalter){
                        writer.write("GetAnzahl" + "\n");
                        writer.flush();
                        anzahl = Integer.parseInt(reader.readLine());
                    }
                    if (LogOut.isclicked) {
                        OrganizerLogIn.isinDB = false;
                        OrganizerLogIn.schleife = 0;
                        schleife1 = false;
                        writer.write("disconnect" + "\n");
                        writer.flush();
                        System.out.println("DISCOOONECTED");
                        writer.close();
                        socket.close();
                        LogOut.isclicked = false;
                    }
                }
            }
        } catch (ConnectException e) {
                setServerStatus(true);
                e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Receives the Event-Data from the Server.
     * This Method receives the Event-Data from the Server and updates the Lists.
     * It also stores the data to a file.
     * @param decision whether safe it as student or organizer data
     * @throws IOException in case something with Serverconnection happens
     * @throws JSONException because we safe it as a JSON-Object
     */
    private void receiveEvents (int decision) throws IOException, JSONException {
        //first the future Events
        System.out.println("EVENT");
        writer.write("Event\n");
        writer.flush();
        String temp = "";
        JSONObject obj;
        JSONArray ar = new JSONArray();
        while (!temp.equals("ENDE")) {
            temp = reader.readLine();
            if (!temp.equals("ENDE")) {
                // LastEvents.list.add(temp);
                obj = new JSONObject(temp);
                ar.put(obj);
            } else {
                if (decision==1) {
                    NextEvents.list = ar;
                    safeFile("NextEvents.tmp",LastEvents.list);
                } else if(decision==2) {
                    OrganizerNextEvents.list=ar;
                    safeFile("OrganizerNextEvents.tmp",OrganizerNextEvents.list);
                }
            }
        }
        System.out.println("EVENTPAST");
        //now the past events
        writer.write("Eventpast\n");
        writer.flush();
        temp = "";
        ar = new JSONArray();
        while (!temp.equals("ENDE")) {
            temp = reader.readLine();
            if (!temp.equals("ENDE")) {
                // LastEvents.list.add(temp);
                System.out.println(temp);
                obj = new JSONObject(temp);
                ar.put(obj);
            } else {
                if (decision==1) {
                    LastEvents.list = ar;
                    safeFile("LastEvents.tmp",LastEvents.list);
                } else if(decision==2) {
                    OrganizerLastEvents.list=ar;
                    safeFile("OrganizerLastEvents.tmp",OrganizerNextEvents.list);
                }
            }
        }
    }

    /**
     * Prints an Array into a file, where each line equals an item in the Array.
     * @param filename Name the File should have
     * @param ar The array with the data to be written
     * @throws JSONException we read from a JSON-Array
     */
    private void safeFile (String filename, JSONArray ar) throws JSONException {
        PrintWriter pw = null;
        try {
            File f = new File(context.getCacheDir(),filename);
            pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            for (int i=0;i<ar.length();i++) {
                pw.println(ar.get(i).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null){
                pw.close();
            }
        }
    }

    /**
     * Reads Event-Data out of local file or receives them from Server (if File nonexistent).
     * @param desicion whether sutend or organizer Data schall be read
     * @return true if data could be read from the files or false if not
     * @throws JSONException for case we read from Server (see receiveEvents-Method)
     */
    private boolean readEvents(int desicion) throws JSONException {
        String filename1 = null;
        String filename2 = null;
        JSONArray list1 = null;
        JSONArray list2 = null;
        if (desicion == 1) {
            filename1 = "NextEvents.tmp";
            filename2 = "LastEvents.tmp";
            list1 = NextEvents.list;
            list2 = LastEvents.list;
        } else if (desicion==2) {
            filename1="OrganizerNextEvents.tmp";
            filename2="OrganizerLastEvents.tmp";
            list1 = OrganizerNextEvents.list;
            list2 = OrganizerLastEvents.list;
        }

        File f1 = new File(context.getCacheDir(),filename1);
        File f2 = new File(context.getCacheDir(),filename2);
        BufferedReader br1 = null;
        BufferedReader br2 =null;

        //If one of the files with stored events doesn't exists, return false
        if (!f1.exists() || !f2.exists()) {
            return false;
        } else {
            //else read the files and don't bother the Server with it
            try {
                br1 = new BufferedReader(new FileReader(f1));
                String tmp = br1.readLine();
                while (tmp != null) {
                    list1.put(new JSONObject(tmp));
                    tmp=br1.readLine();
                }
                br2 = new BufferedReader(new FileReader(f2));
                tmp = br2.readLine();
                while (tmp != null) {
                    list2.put(new JSONObject(tmp));
                    tmp = br2.readLine();
                }
                return true;
            } catch(IOException ioEx) {
                ioEx.printStackTrace();
                return false;
            } finally {
                if (br1 != null) {
                    try {
                        br1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (br2 != null) {
                    try {
                        br2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Server staus (online offline).
     * @param status
     */
    public void setServerStatus(boolean status){
        noServer = status;
    }

    /**
     * Server staus (online offline).
     * @return
     */
    public boolean getServerStatus(){
        return noServer;
    }
    /**
     * Connects the client with the SSL protected Server.
     * Connects reader and writer to SSL Connection.
     */
    private void sslConnect() {
        try {
            String trustdir = kh.getDir() + "/keyStore";
            KeyManagerFactory kmfactory=null;
            try {
                kmfactory = KeyManagerFactory.getInstance(
                        KeyManagerFactory.getDefaultAlgorithm());
                kmfactory.init(kh.getKeyStore(), "password".toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
            KeyManager[] keymanagers =  kmfactory.getKeyManagers();
            TrustManagerFactory tmf = null;
            try {
                tmf = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                tmf.init(kh.getKeyStore());
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                sslContext.init(keymanagers, tmf.getTrustManagers(), new SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            SSLSocketFactory sf = sslContext.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sf.createSocket(ip, 1832);
            OutputStream out = null;
            out = sslSocket.getOutputStream();
            writer = new PrintWriter(out);
            InputStream in = sslSocket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            System.out.println("SSL-Client online");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}