/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The class Client handles the connection to the Server,
 * and regulates the communication between App and Server(Database).
 */
public class Client extends AsyncTask<Void, Void, Void>{

    private String ip;
    private int port;
    private Socket socket = null;

    /**
     * public constructor from Client
     * @param ip - the ip address we want to reach
     * @param port - the port to connect with the Server
     */
    public Client(String ip, int port) {
        this.ip = ip;
        this.port= port;
    }

    /**
     * This method creats an background thread, that connects with the Server.
     * Here, we send and receive Data from the Database behind the Server.
     * @param args0
     */
    @Override
    protected Void doInBackground(Void... args0) {

        try{
            socket = new Socket(ip,port);
            System.out.println("Client online");
            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(out);
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            boolean schleife = true;
            int stop = 0;

            while(schleife) {
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
                        StartActivity.setText(Integer.parseInt(vonServer));
                        StartActivity.isinDB = true;
                        StartActivity.kontrolle = 1;
                        writer.write("Rangliste" + "\n");
                        writer.flush();
                        for(int i=1; i<=10; i++){
                            Ranking.score.put(i, reader.readLine());
                            System.out.println(Ranking.score.get(i));
                        }
                        Ranking.rang = reader.readLine();


                    } else {
                        StartActivity.isinDB = false;
                        StartActivity.kontrolle = 1;

                    }
                    StartActivity.isclicked = false;
                }

                if (LogOut.isclicked && schleife) {
                    schleife = false;
                    writer.write("disconnect" + "\n");
                    writer.flush();
                    writer.close();
                    socket.close();
                    LogOut.isclicked = false;
                }
            }

            } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }return null;
    }
}

