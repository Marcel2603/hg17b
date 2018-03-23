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
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
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
     * JSONArray mit allen nächsten Events.
     */
    private static JSONArray NextEvents;
    /**
     * JSONArray mit allen vergangenen Events.
     */
    private static JSONArray LastEvents;
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
        try {
            setLastEvents();
            setNextEvents();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Main-Methode.
     * Start the server.
     * @param args Not necessary.
     */
   public static void main(final String[] args) {
        System.setProperty("javax.net.ssl.keyStore", "trustStore");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
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
            Thread t = new Thread(new Commands(server));
            t.start();
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("MEZ"));
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
     * Sortiert das JSONArray.
     * @param ar Array mit Eventdaten
     * @param obj Event
     * @return sortiertes Eventarray
     * @throws JSONException wenn datenbank fehlerhaft.
     */
    private static JSONArray sort(JSONArray ar, JSONObject obj) throws JSONException {
        boolean stop = true;
        for (int i = 0; i < ar.length() && stop; i++) {
            JSONObject temp = ar.getJSONObject(i);
            if (temp.getString("label").equals(obj.getString("label"))) {
                if (temp.getString("address").equals(obj.getString("address"))) {
                    if (temp.getString("description").equals(obj.getString("description"))) {
                        String time = temp.getString("start");
                        time += " || " + obj.getString("start");
                        temp.put("start", time);

                            ar.remove(i);

                        ar.put(temp);
                        stop = false;
                    }
                }
            }
        }
        if (stop || ar.length() == 0) {
            ar.put(obj);
        }
        return ar;
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
    /**
     * Holt alle naechsten Events aus der DB.
     * @throws JSONException wenn datenbank fehlerhaft.
     */
    private static void setNextEvents() throws JSONException{
        ArrayList<HashMap<String, String>> list
        = db1.getEventsStudents(false);
        JSONObject obj;
        NextEvents = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            obj = new JSONObject();
            obj.put("label", list.get(i).get("label"));
            obj.put("address", list.get(i).get("address"));
            obj.put("url", list.get(i).get("url"));
            obj.put("description", list.get(i).get("description"));
            obj.put("start", list.get(i).get("start"));
            NextEvents = sort(NextEvents, obj);
        }
    }
    /**
     * Holt alle vergangenen Events aus der DB.
     * @throws JSONException wenn datenbank fehlerhaft.
     */
    private static void setLastEvents() throws JSONException{
        ArrayList<HashMap<String, String>> list
        = db1.getEventsStudents(true);
        JSONObject obj;
        NextEvents = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            obj = new JSONObject();
            obj.put("label", list.get(i).get("label"));
            obj.put("address", list.get(i).get("address"));
            obj.put("url", list.get(i).get("url"));
            obj.put("description", list.get(i).get("description"));
            obj.put("start", list.get(i).get("start"));
            LastEvents = sort(LastEvents, obj);
        }
    }
    /**
     * Gibt die LastEvnets zurueck.
     * @return LastEvents
     */
    public static JSONArray getLastEvent() {
        return LastEvents;
    }
    /**
     * Gibt die NextEvents zurueck.
     * @return NextEvents
     */
    public static JSONArray getNextEvent() {
        return NextEvents;
    }
}
