import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import db.PupilDB;

/**
 * Handle the communication between Server, Client and DB.
 * @author marcel
 *
 */
public class Handler implements Runnable {
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
                System.out.println("schueler nekomen");
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
                        System.out.println("ID");
                        recieve = reader.readLine();
                        id = Integer.parseInt(recieve);
                        boolean inID = db1.isID(id);
                        if (inID) {
                            System.out.println(true);
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
                    }
                    if (recieve.equals("Event")) {
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsStudents(false);
                        for (int i = 0;
                                i < list.size(); i++) {
                            System.out.println(list.get(i).get("label"));
                            writer.write(list.get(i).get("label") + "\n");
                            writer.flush();
                        }
                        writer.write("ENDE" + "\n");
                        writer.flush();
                        System.out.println("ENDE");
                    }
                    System.out.println(recieve);
                    recieve = reader.readLine();
                }
                //Veranstalter
                if (pers == 2) {
                    recieve = reader.readLine();
                    if (recieve.equals("Ueberpruefe Email")) {
//                      getEmail

                       recieve = reader.readLine();
                       email = recieve;
                       System.out.println("Email");
                       if (db1.isOrganizer(recieve)) {
                           //HAS KEY
                           //ELSE
                           mail.senden("marcelemail2603@gmail.com");
                           
                           
                           
                           
                           
                           
                           writer.write("true\n");
                           writer.flush();
                           System.out.println(recieve + "true");
                       } else {
                                writer.write("false" + "\n");
                                writer.flush();
                                System.out.println(recieve + "false");
                       }
                    }
                    if (recieve.equals("Eventpast")) {
                        System.out.println("LAST EVENT ____________________________________________");
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsOrganizer(email, true);
                        for (int i = 0;
                                i < list.size(); i++) {
                            System.out.println(list.get(i).get("label"));
                            writer.write(list.get(i).get("start") + "\n");
                            writer.flush();
                        }
                        writer.write("ENDE" + "\n");
                        writer.flush();
                        System.out.println(list.size());
                    }
                    if (recieve.equals("Event")) {
                        System.out.println("NEXT EVENT ____________________________________________");
                        ArrayList<HashMap<String, String>> list
                        = db1.getEventsOrganizer(email, false);
                        for (int i = 0;
                                i < list.size(); i++) {
                            System.out.println(list.get(i).get("label"));
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
        }
    }
}
