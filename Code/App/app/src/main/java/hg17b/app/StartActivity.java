/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

/**
 * This is the Main Activity, and handles the Student part of the App.
 * The class StartActivity starts the Application and sets up the Layout.
 * In this class the connection to the server is established and
 * the menu is created. The ID is received from the User and checked
 * by the Server, afterwards data is received from there.
 */
public class StartActivity extends AppCompatActivity {
    public static boolean anmelden = false;
    public static int eventID;
    public static boolean abmelden = false;
    public static int index;
    public static EditText etID;
    public static boolean past;
    TextView tvInfo;
    public static String data;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    Button logOut;
    public static Client client;
    public static boolean isinDB;
    public static boolean isclicked;
    private static int points;
    public static int kontrolle;
    KeyHandler ks;

    /**
     * The onCreate method is called when the Application gets started.
     * Here we initialize the Layout, start the Client and connect them with the Server.
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ks = new KeyHandler(getFilesDir());
        BufferedReader br = null;


        File f = new File(getCacheDir(),"logindata.tmp");
        if (f.exists()) {
            try {
                br = new BufferedReader(new FileReader(f));
                String ID = br.readLine();
                if (ID.startsWith("v")) {
                    //if Veranstalter
                    OrganizerLogIn.nutzer = ID.substring(1);
                    client = new Client("pcai042.informatik.uni-leipzig.de", 1831, 2,ks,this);
                    client.execute();

                    if (client.getServerStatus()) {
                        Toast.makeText(this,
                                "Der Server ist offline, versuche es später wieder!",
                                    Toast.LENGTH_LONG).show();
                        client.setServerStatus(false);
                    }

                    Intent intent = new Intent(StartActivity.this, OrganizerLogIn.class);
                    startActivity(intent);
                } else {
                    //If it is a student's ID
                    File pointsfile = new File(getCacheDir(),"points.tmp");
                    if (pointsfile.exists()) {
                        br.close(); //close other file
                        br = new BufferedReader(new FileReader(pointsfile));
                        setPoints(br.read());
                    }
                    isclicked = true;
                    data = ID;
                    client = new Client("pcai042.informatik.uni-leipzig.de", 1831, 1, ks,this);
                    client.execute();
                    if (client.getServerStatus()) {
                        Toast.makeText(this,
                                "Der Server ist offline, versuche es später wieder!",
                                    Toast.LENGTH_LONG).show();
                        client.setServerStatus(false);
                    }
                    setContentView(R.layout.main_activity);
                    initNavigationMenu();
                }
            } catch(IOException ioEx) {
                ioEx.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            setContentView(R.layout.start_activity);

            kontrolle = 0;
            isinDB = false;
            logOut = findViewById(R.id.btnLogOut);
            etID = findViewById(R.id.etID);
            //tvInfo = findViewById(R.id.tvInfo);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String value = extras.getString("scannedID");
                System.out.println(value);
                etID.setText(value);
            }
        }
    }

    /**
     *
     * @param s
     */
    public static void setPoints(int s) {
        points = s;
    }

    public static void buttonanmelden (View v) {
        if (anmelden) {
            abmelden = true;
            anmelden = false;
        }else{
            anmelden = true;
        }

    }
    /**
     *
     * @return
     */
    public static int getPoints() {
        return points;
    }

    /**
     * This method enabled a menu, and that the menu items are clickable
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method is called when the Button is clicked.
     * It sends the entered ID to the Server, and receives an answer.
     * If the ID isn't in the Database we send an message to the User,
     * otherwise the App receives the associated data and loads the Mainpage
     * where the User can interact.
     * @param v - a View
     */
    public void buttonOnClick(View v) {

        isinDB = false;
        isclicked = true;
        kontrolle = 0;
        client = new Client("pcai042.informatik.uni-leipzig.de", 1831, 1, ks, this);
        client.execute();

        //Check if input fits into the TextField
        data = etID.getText().toString();

       if (data.length()>2) {
           Toast.makeText(this,
                   "Deine Eingabe war zu lang\n Maximal 2 Stellen erlaubt",
                    Toast.LENGTH_LONG).show();
       } else {
           if (data.length()<1 || data == null) {
               Toast.makeText(this,
                       "Deine Eingabe war zu kurz\n", Toast.LENGTH_LONG).show();
           }
          while (!isinDB && kontrolle == 0 && !client.getServerStatus()) {
               //loop for testing
           }
           if (client.getServerStatus()) {
               Toast.makeText(this,
                       "Der Server ist offline, versuche es später wieder!",
                            Toast.LENGTH_LONG).show();
               client.setServerStatus(false);
           } else {
               if (isinDB) {

                   /*Create File to safe ID*/
                   BufferedWriter bw = null;
                   try {
                       File f = new File(getCacheDir(),"logindata.tmp");
                       bw = new BufferedWriter(new FileWriter(f));
                       bw.write(data);
                       bw.flush();
                   } catch (IOException e) {
                       e.printStackTrace();
                   } finally {
                       if (bw != null) {
                           try {
                               bw.close();
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                   }
                   setContentView(R.layout.main_activity);
                   initNavigationMenu();
               } else {
                   Toast.makeText(this,
                           "Deine ID ist nicht in der Datenbank", Toast.LENGTH_LONG).show();
                   kontrolle = 0;
               }
           }
       }
    }

    /**
     * the method starts the scanner and the camera
     * @param v
     */
    public void scannerOnClick(View v) {
        boolean forPupil = true;
        Intent i = new Intent(StartActivity.this, OcrCaptureActivity.class);
        i.putExtra("decider", forPupil);
        startActivity(i);
    }

    /**
     * disables the Back-Button to avoid disconnection
     */
    @Override
    public void onBackPressed() {
        /**if (!shouldAllowBack()) {
            doSomething();
        } else {
            super.onBackPressed();
        }*/
    }

    /**
     * starts the OrganizerLogIn Page
     * @param v
     */
    public void organizer(View v) {
        Intent intent = new Intent(StartActivity.this, OrganizerLogIn.class);
        startActivity(intent);
    }

    /**
     * Onclick method for LastEvents
     * @param v
     */
    public void Lastevents (View v) {
        try {
            EventDetails event;
            past = true;
            switch (v.getId()) {

                case R.id.tv1:
                    index = LastEvents.getZaehler();
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    //drawerLayout.closeDrawers();
                    break;

                case R.id.tv2:
                    //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_LONG).show();
                    index = LastEvents.getZaehler() + 1;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv3:
                    index = LastEvents.getZaehler() + 2;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv4:
                    index = LastEvents.getZaehler() + 3;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv5:
                    index = LastEvents.getZaehler() + 4;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv6:
                    index = LastEvents.getZaehler() + 5;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv7:
                    index = LastEvents.getZaehler() + 6;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv8:
                    index = LastEvents.getZaehler() + 7;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv9:
                    index = LastEvents.getZaehler() + 8;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv10:
                    index = LastEvents.getZaehler() + 9;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(LastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Onclick method for NextEvents
     * @param v
     */
    public void Nextevents (View v) {
        try {
            EventDetails event;
            past = false;
            switch (v.getId()) {

                case R.id.tv1:
                    index = NextEvents.getZaehler();
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    //drawerLayout.closeDrawers();
                    break;

                case R.id.tv2:
                    //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_LONG).show();
                    index = NextEvents.getZaehler() + 1;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv3:
                    index = NextEvents.getZaehler() + 2;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv4:
                    index = NextEvents.getZaehler() + 3;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv5:
                    index = NextEvents.getZaehler() + 4;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv6:
                    index = NextEvents.getZaehler() + 5;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv7:
                    index = NextEvents.getZaehler() + 6;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv8:
                    index = NextEvents.getZaehler() + 7;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv9:
                    index = NextEvents.getZaehler() + 8;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv10:
                    index = NextEvents.getZaehler() + 9;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(NextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Onclick method for OrganizerLastEvents
     * @param v
     */
    public void OrgLastevents (View v) {
        try {
            EventDetails event;
            past = true;
            switch (v.getId()) {

                case R.id.tv1:
                    /*
                    Fragment fragment_event_organizer = new EventDetails();
                    getSupportFragmentManager().beginTransaction().replace(R.id.last,fragment_event_organizer);
                    fragmentTransaction.commit();
                    EventDetails.tv1.setText(OrganizerLastEvents.list.getJSONObject(OrganizerLastEvents.getZaehler()).getString("Start"));
                    */
                    index = OrganizerLastEvents.getZaehler();
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    //drawerLayout.closeDrawers();
                    break;

                case R.id.tv2:
                    //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_LONG).show();
                    index = OrganizerLastEvents.getZaehler() + 1;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv3:
                    index = OrganizerLastEvents.getZaehler() + 2;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv4:
                    index = OrganizerLastEvents.getZaehler() + 3;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv5:
                    index = OrganizerLastEvents.getZaehler() + 4;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv6:
                    index = OrganizerLastEvents.getZaehler() + 5;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv7:
                    index = OrganizerLastEvents.getZaehler() + 6;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv8:
                    index = OrganizerLastEvents.getZaehler() + 7;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv9:
                    index = OrganizerLastEvents.getZaehler() + 8;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv10:
                    index = OrganizerLastEvents.getZaehler() + 9;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerLastEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Onclick method for OrganizerNextEvents
     * @param v
     */
    public void OrgNextevents (View v) {
        try {
            EventDetails event;
            past = false;
            switch (v.getId()) {

                case R.id.tv1:
                    /*
                    Fragment fragment_event_organizer = new EventDetails();
                    getSupportFragmentManager().beginTransaction().replace(R.id.last,fragment_event_organizer);
                    fragmentTransaction.commit();
                    EventDetails.tv1.setText(OrganizerNextEvents.list.getJSONObject(OrganizerNextEvents.getZaehler()).getString("Start"));
                    */
                    index = OrganizerNextEvents.getZaehler();
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    //drawerLayout.closeDrawers();
                    break;

                case R.id.tv2:
                    //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_LONG).show();
                    index = OrganizerNextEvents.getZaehler() + 1;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv3:
                    index = OrganizerNextEvents.getZaehler() + 2;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv4:
                    index = OrganizerNextEvents.getZaehler() + 3;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv5:
                    index = OrganizerNextEvents.getZaehler() + 4;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv6:
                    index = OrganizerNextEvents.getZaehler() + 5;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv7:
                    index = OrganizerNextEvents.getZaehler() + 6;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv8:
                    index = OrganizerNextEvents.getZaehler() + 7;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv9:
                    index = OrganizerNextEvents.getZaehler() + 8;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;

                case  R.id.tv10:
                    index = OrganizerNextEvents.getZaehler() + 9;
                    event = new EventDetails();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.menuContainer, event);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(OrganizerNextEvents.list.getJSONObject(index)
                            .getString("label"));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sets up the menu (as a DrawerLayout)
     * and enabled the navigation within the menu, handing off to the individual fragments.
     */
    public void initNavigationMenu() {

        //setting up the menu bar
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Forwarding is regulated here...
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.menuContainer, new Main());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Startseite");
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView
                                                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.Startseite:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.menuContainer, new Main());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Startseite");
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Kommende_Veranstaltungen:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.menuContainer, new NextEvents());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("nächste Veranstaltungen");
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Rangliste:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.menuContainer, new Ranking());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Rangliste");
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Historie:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.menuContainer, new LastEvents());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("bisherige Veranstaltungen");
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Einstellungen:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.menuContainer, new Settings());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Einstellungen");
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Log_Out:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.menuContainer, new LogOut());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Abmelden");
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });
    }

    /**
     *
     */
    public void onClick() {
        LogOut.isclicked = true;
    }
}