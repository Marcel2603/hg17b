import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Commands implements Runnable{

	private ServerSocket server;

	public Commands(ServerSocket server) {
		this.server = server;
	}
	@Override
	public void run() {
	       Console c = System.console();
	       String msg = "";
	       boolean schleife = true;
	       while(schleife) {
	    	   msg = c.readLine();
	    	   //gib liste mit Befehle aus
	    	   if(msg.toLowerCase().equals("help")) {
	    		   commands();
	    	   }else {
	    	   if(msg.toLowerCase().equals("list")) {
	    		   clientlist();
	    	   }else {
	    	   if(msg.toLowerCase().contentEquals("stop")) {
	    		   ArrayList<Socket> clients = Server.getSocket();
	    		   for(int i = 0;i < clients.size(); i++) {
	    			   senden("Shutdown", clients.get(i));
	    		   }
	    		   schleife = false;
	    		   beenden();
	    	   }else {
	    		   
	    	   
	    	   System.out.println("Befehl nicht vorhanden, bitte "+"help"+" eingeben");
	    	   }}}
	       }
	    	
               
	}
	public void senden(String message, Socket client) {
		try {
		OutputStream out = client.getOutputStream();
		PrintWriter writer = new PrintWriter(out);
		writer.write(message +"\n");
		writer.flush();
		out.close();
		writer.close();

		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	private void commands() {
		String shutdown = "stop - Faehrt den Server herunter \n";
		String list = "list - Zeigt eine Liste der verbundenen Clients \n";
		String help = "help - Zeigt diese Liste an";
		System.out.println("Folgende Befehle sind verfügbar:");
		System.out.println(shutdown + list + help);
	}
	
	private void clientlist() {
		System.out.println("Verbundene Clients:");
		if(Server.getSocket().size() != 0) {
			for(int i = 0 ; i < Server.getSocket().size(); i++) {
				System.out.println("Addresse: " + Server.getSocket().get(i).getInetAddress() + " Port: " + Server.getSocket().get(i).getPort());
			}
		}else {
			System.out.println("Es sind keine Clients verbunden");
		}
	}
	private void beenden() {
		System.out.println("Der Server wird heruntergefahren");
		try {
			server.close();	
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}


