import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Create the Commands.
 * @author marcel
 *
 */
public class Commands implements Runnable {
    /**
     * ServerSocket.
     */
    private ServerSocket server;
    /**
     * Constructor to give the Serversocket.
     * @param serversocket Serversocket.
     */
    public Commands(final ServerSocket serversocket) {
        this.server = serversocket;
    }
    /**
     * Create the Commands and handle the requests.
     */
    @Override
    public void run() {
        Console c = System.console();
        String msg = "";
        ArrayList<String> befehle = new ArrayList<String>();
        befehle.add("help");
        befehle.add("list");
        befehle.add("stop");
        boolean schleife = true;
        while (schleife) {
            msg = c.readLine();
//            gibt Liste mit allen Befehlen aus
            if (msg.toLowerCase().equals("help")) {
                commands();
            }
            if (msg.toLowerCase().equals("list")) {
                clientlist();
            }
            if (msg.toLowerCase().equals("stop")) {
                ArrayList<Socket> clients = Server.getSocket();
                for (int i = 0; i < clients.size(); i++) {
                    senden("Shutdown", clients.get(i));
                }
                schleife = false;
                beenden();
            }
            if (!befehle.contains(msg.toLowerCase())) {
                System.out.println("Befehl nicht vorhanden."
                        + " Gib help fuer eine Uebersicht ein.");
            }
        }
    }
    /**
     * List of all Commands.
     */
    private void commands() {
        String shutdown = "stop - Faehrt den Server herunter \n";
        String list = "list - Zeigt eine Liste der verbundenen Clients \n";
        String help = "help - Zeigt diese Liste an \n";
        System.out.println("Folgende Befehle sind verfuegbar:");
        System.out.println(help + list + shutdown);
    }
    /**
     * Print a list with all connected clients.
     */
    private void clientlist() {
        System.out.print("Verbundene Clients:" + "\n" + "\n");
        if (Server.getSocket().size() != 0) {
            for (int i = 0; i < Server.getSocket().size(); i++) {
                System.out.println("Addresse: " + Server.getSocket().get(i).getInetAddress()
                        + " Port: " + Server.getSocket().get(i).getPort());
                }
            } else {
                System.out.println("Es sind keine Clients verbunden.");
        }
    }
    /**
     * Send the client a message.
     * @param message Message for the client.
     * @param client Clientsocket
     */
    private void senden(final String message, final Socket client) {
        try {
            OutputStream out = client.getOutputStream();
            PrintWriter writer = new PrintWriter(out);
            writer.write(message + "\n");
            writer.flush();
            out.close();
            writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    /**
     * shutdown the server.
     */
    private void beenden() {
        System.out.println("Der Server wird heruntegefahren");
        try {
            server.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
