/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The class Client handles the connection to the Server,
 * and regulates the communication between App and Server(Database).
 */
public class Client extends AsyncTask<Void, Void, Void>{

    private String ip;
    private int port;
    private Socket socket = null;
    private boolean noServer = false;
    private int entscheidung = 0;
    public int anzahl;

    /**
     * public constructor from Client
     * @param ip - the ip address we want to reach
     * @param port - the port to connect with the Server
     */
    public Client(String ip, int port, int entscheidung) {
        this.ip = ip;
        this.port= port;
        this.entscheidung = entscheidung;
    }


    //Standard Getter
    public String getIp(){
        return this.ip;
    }
    public int getPort(){
        return this.port;
    }
    public int getEntscheidung(){
        return this.entscheidung;
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
            //schueler
            if(entscheidung == 1) {
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
                            StartActivity.setText(Integer.parseInt(vonServer));
                            StartActivity.isinDB = true;
                            StartActivity.kontrolle = 1;
                            writer.write("Rangliste" + "\n");
                            writer.flush();
                            for (int i = 1; i <= 10; i++) {
                                Ranking.score.put(i, reader.readLine());
                                System.out.println(Ranking.score.get(i));
                            }
                            Ranking.rang = reader.readLine();




                            writer.write("Event\n");
                            writer.flush();
                            String temp = "";
                            while(!temp.equals("ENDE")){
                                temp = reader.readLine();
                                if(!temp.equals("ENDE")) {
                                    NextEvents.list.add(temp);
                                }
                            }
                            writer.write("Eventpast\n");
                            writer.flush();
                            temp = "";
                            JSONObject obj;
                            JSONArray ar = new JSONArray();
                            while(!temp.equals("ENDE")){
                                temp = reader.readLine();
                                if(!temp.equals("ENDE")) {
                                   // LastEvents.list.add(temp);
                                    obj = new JSONObject(temp);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        ar = isDouble(ar, obj);
                                    }else {
                                        ar.put(obj);
                                    }
                                }else {
                                    LastEvents.list = ar;
                                }
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

                    if (LogOut.isclicked) {
                        schleife = false;
                        writer.write("disconnect" + "\n");
                        writer.flush();
                        writer.close();
                        socket.close();
                        LogOut.isclicked = false;
                    }
                }
            }
            //Veranstalter
            if (entscheidung == 2){
                writer.write("veranstalter" + "\n");
                writer.flush();
                boolean schleife1 = true;
                int stop = 0;
                int t = 0;

                while (schleife1) {
                    if(t == 0) {
                        String email = OrganizerLogIn.nutzer;
                        writer.write("Ueberpruefe Email" + "\n");
                        writer.flush();
                        System.out.println("Befehl");
                        writer.write(email + "\n");
                        writer.flush();
                        System.out.println(email);
                        String vonServer = reader.readLine();
                        System.out.println(vonServer);
                        if (vonServer.equals("true")) {
                            writer.write("Event\n");
                            writer.flush();
                            String temp = "";
                            while(!temp.equals("ENDE")){
                                temp = reader.readLine();
                                if(!temp.equals("ENDE")) {
                                    OrganizerNextEvents.list.add(temp);
                                }
                            }
                            writer.write("Eventpast\n");
                            writer.flush();
                            temp = "";
                            while(!temp.equals("ENDE")){
                                temp = reader.readLine();
                                if(!temp.equals("ENDE")) {
                                    OrganizerLastEvents.list.add(temp);
                                }
                            }
                            OrganizerLogIn.isinDB = true;
                            OrganizerLogIn.schleife = 0;

                            t = 1;

                        }else{
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
                        System.out.println("b");

                    }
                    System.out.println("a");
                    if (OrganizerMain.veranstalter){
                        writer.write("GetAnzahl");
                        writer.flush();
                        anzahl = Integer.parseInt(reader.readLine());
                    }
                    if (LogOut.isclicked) {
                        OrganizerLogIn.isinDB = false;
                        OrganizerLogIn.schleife = 0;
                        System.out.println("Test");
                        schleife1 = false;
                        writer.write("disconnect" + "\n");
                        writer.flush();
                        System.out.println("Test");
                        writer.close();
                        socket.close();
                        LogOut.isclicked = false;
                    }
                }
                System.out.println("Test");
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
     *
     * @param status
     */
    public void setServerStatus(boolean status){
        noServer = status;
    }

    /**
     *
     * @return
     */
    public boolean getServerStatus(){
        return noServer;
    }

    private JSONArray isDouble(JSONArray ar, JSONObject obj) throws JSONException {
        boolean stop = true;
        for(int i = 0; i < ar.length() && stop; i++) {
            JSONObject temp = ar.getJSONObject(i);
            if (temp.getString("label").equals(obj.getString("label"))){
                if (temp.getString("address").equals(obj.getString("address"))){
                    if (temp.getString("description").equals(obj.getString("description"))){
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
        if(stop || ar.length() == 0) {
            ar.put(obj);
        }
        return ar;
    }

}

