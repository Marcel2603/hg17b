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
 * @author marcel
 *
 */
public class Handler implements Runnable {
	
	/**
	 * Socket für den Client
	 */
	private Socket client;
	private PupilDB db1;
	
	/**
	 * Konstruktor zum Übergeben des Clients
	 * @param client Client welcher als Thread auf dem Server laufen soll
	 */
	
	public Handler(Socket client,PupilDB db1) {
		this.client = client;
		this.db1 = db1;
	}

	
	/**
	 * Verwaltet und andwortet dem Client
	 */
	public void run() {
		try {
			OutputStream out = client.getOutputStream();
			PrintWriter writer = new PrintWriter(out);
			
			InputStream in = client.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String s = " ";
			
			HashMap<Integer, Integer> rangliste;
			s = reader.readLine();
			int id = 0;
			while(!s.equals("disconnect")) {
				
				if(s.equals("Ueberpruefe ID")) {
					//s = id
					s = reader.readLine();
					System.out.println("Prufen ID");
					//id an DB schicken
					id = Integer.parseInt(s);
					System.out.println(id);
					boolean inID = db1.isID(id);
					System.out.println(inID);
					if(inID) {//prüfen ob id in db
						writer.write("true" + "\n");
						writer.flush();
					}else {
						writer.write("false\n");
						writer.flush();
					}
				}
				if(s.equals("Punkte")) {
					Integer points = db1.getScore(id);
					System.out.println("Punkte " +points);
					writer.write(points.toString() + "\n");
					writer.flush();
				}
				if(s.equals("Rangliste")) {
					rangliste = db1.getToplist();
					for(int i = 1 ; i<=rangliste.size();i++) {
						System.out.println(i +". platz" +rangliste.get(i));
						writer.write(rangliste.get(i) + "\n");
						writer.flush();
					}
					writer.write(db1.getRank(id) + "\n");
					writer.flush();	
				}
				
				System.out.println("ich lese ");
				
				s = reader.readLine();
				System.out.println("Empfangen vom Client( " +client.getPort() + "): " +s);
			}
			System.out.println("Client aus");
			Server.deleteSocket(client);
			writer.close();
			reader.close();
			client.close();
		}catch(SocketException e) {
			Server.deleteSocket(client);			
		}catch(IOException e) {
			e.printStackTrace();
		}catch(NullPointerException e) {
			Server.deleteSocket(client);
		}
	}
}
