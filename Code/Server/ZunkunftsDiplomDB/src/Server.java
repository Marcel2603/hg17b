import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import db.PupilDB;
/**
 * Erzeugt Server und initialisiert die DB.
 * @author marcel
 *
 */
public class Server {

/**
 * ArrayList mit allen verbunden Clients.
 * getSocket hinzufügen.
 */
	private static ArrayList<Socket> socket = new ArrayList<Socket>();

	private PupilDB db1;

/**
 * .
*/
	private ServerSocket server;

/**
 * Port über welcher der Server laufen soll.
 */
	static final int PORT = 1831;


	public Server(int Port) {
		try {
			this.server = new ServerSocket(Port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.db1 = new PupilDB();
	}

	/**
	 * Startet den Server.
	 * @param args
	 *            nicht weiter zubeachten
	 */
  public static void main(String[] args) {
		Server serv = new Server(PORT);
		serv.start();
	}

	/**
	 * .
	 */
  private void start() {
    ExecutorService executor = Executors.newFixedThreadPool(100);
	try {
			System.out.println("Server wurde gestartet");
			Thread t = new Thread(new Commands(server));
			System.out.println(db1.getScore(2));
		    t.start();
			while (true) {
				Socket client = server.accept();
				socket.add(client);
				executor.execute(new Handler(client,db1));
			}

		} catch (IOException e) {
		}
	}
  
  public static ArrayList<Socket> getSocket(){
	  return socket;
  }
  
  public static void deleteSocket(Socket client) {
	  socket.remove(client);
  }

}
