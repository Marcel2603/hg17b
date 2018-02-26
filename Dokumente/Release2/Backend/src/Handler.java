import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
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
     * Konstruktor to create the Thread.
     * @param socket Clientsocket.
     * @param pupilDB DB with pupildata.
     */
    public Handler(final Socket socket, final PupilDB pupilDB) {
        this.client = socket;
        this.db1 = pupilDB;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
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
                    recieve = reader.readLine();
                }
                //Veranstalter
                if (pers == 2) {
                    String email = "";
                    recieve = reader.readLine();
                    if (recieve.equals("Ueberpruefe Email")) {
//                      getEmail
                        System.out.println("Befehl");
                       recieve = reader.readLine();
                       email = recieve;
                       System.out.println("Email");
                       if (db1.isOrganizer(recieve)) {
                           writer.write("true\n");
                           writer.flush();
                           System.out.println(recieve + "true");
                       } else {
                                writer.write("false" + "\n");
                                writer.flush();
                                System.out.println(recieve + "false");
                       }
                }
                  if (recieve.equals("GetAnzahl")) {
                      int anzahl = db1.getEventsOrganizer(email, true).size();
                      writer.write(anzahl + "\n");
                      writer.flush();
                  }
          }
            }
            System.out.println("Client: " + client.getInetAddress() + ":" + client.getPort() + " ist offline.");
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
