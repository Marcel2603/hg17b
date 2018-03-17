import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import db.PupilDB;
/**
 * Create DB and start the server.
 * @author marcel
 *
 */
public class Server {
    /**
     * Port for the server.
     */
    static final int PORT = 1831;
    /**
     * Max number of connected clients at the same time.
     */
    static final int THREADNUMBER = 100;
    /**
     * ArrayList with all connected clients.
     */
    private static ArrayList<Socket> socket = new ArrayList<Socket>();
    /**
     * Datenbank for the pupil.
     */
    private PupilDB db1;
    /**
     * Socket for the server.
     */
    private ServerSocket server;
    /**
     * Mail-Konto des Servers.
     */
    private Mail mail;
    /**
     * Konstruktor to create Server and DB.
     * @param port Port for the server.
     */
    public Server(final int port) {
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.db1 = new PupilDB();
        mail = new Mail("hg17b.zukunftsdiplom", "F3u\"9Snl");
        System.out.println("Server is online (Port: " + port + ").");
    }
    /**
     * Main-Methode.
     * Start the server.
     * @param args Not necessary.
     */
   public static void main(final String[] args) {
        Server serv = new Server(PORT);
        serv.start();
    }
    /**
     * manage the server.
     */
    private void start() {
        ExecutorService executor = Executors.newFixedThreadPool(THREADNUMBER);
        try {
//            starts the commands for the server.
            /*
            try {
             // Fuer Testzwecke
             //label, address, url, description, start , end
            FileWriter f = new FileWriter("D:/uni/git/db.txt");
            BufferedWriter fr = new BufferedWriter(f);
            JSONObject obj = new JSONObject();
            ArrayList<HashMap<String, String>> ar = db1.getEventsStudents(true);
            for (int i = 0; i < ar.size(); i++) {
                System.out.println(ar.get(i).get("label"));
                obj.put("label", ar.get(i).get("label"));
                obj.put("address", ar.get(i).get("address"));
                obj.put("url", ar.get(i).get("url"));
                obj.put("description", ar.get(i).get("description"));
                obj.put("start", ar.get(i).get("start"));
               // obj.put("end", ar.get(i).get("end"));
                fr.write(obj.toString());
                fr.flush();
                fr.write(System.getProperty("line.separator"));
                fr.flush();
            }
            }catch (JSONException e ) {
                e.printStackTrace();
            }*/
            Thread t = new Thread(new Commands(server));
            t.start();
            Calendar cal = Calendar.getInstance();
            Date time = cal.getTime();
            DateFormat formatter = new SimpleDateFormat();
            while (true) {
                Socket client = server.accept();
                socket.add(client);
                System.out.println(formatter.format(time)
                        + ": Client : " + client.getInetAddress()
                + ":" + client.getPort() + " hat sich verbunden.");
                executor.execute(new Handler(client, db1, mail));
            }
        } catch (SocketException e) {
            } catch (IOException e) {
            e.printStackTrace();
            }
    }
    /**
     * Returns the ArrayList with all clients.
     * @return Liste of all connected clients.
     */
    public static ArrayList<Socket> getSocket() {
        ArrayList<Socket> list = socket;
        return list;
    }
    /**
     * Delete client out of the array.
     * @param client Clientsocket.
     */
    public static void deleteSocket(final Socket client) {
        socket.remove(client);
    }
}
