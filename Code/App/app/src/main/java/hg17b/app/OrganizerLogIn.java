/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * this class creates the login page for organizers
 * and initialises the organizer main menu
 */
public class OrganizerLogIn extends AppCompatActivity {

    KeyHandler ks;
    Client client;
    public static int schleife = 1;
    public static String nutzer;
    public static boolean isinDB;
    private DrawerLayout drawerLayout2;
    private ActionBarDrawerToggle toggle;
    FragmentTransaction fragmentTransaction2;
    NavigationView navigationView2;
    EditText email;

    /**
     * Standard onCreate Method, loads the LogIn Layout
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ks = new KeyHandler(getFilesDir());
        if (nutzer != null) {
            setContentView(R.layout.organizer_main_activity);
            initNavigationMenu2();
        } else {
            setContentView(R.layout.organizer_log_in);
        }
    }

    /**
     * Checks if E-Mail is in the Database,
     * an later if the password is correct
     * @param v
     */
    public void buttonOnClick2(View v) {

        email = findViewById(R.id.editTextEMail);
        nutzer = email.getText().toString();
        client = new Client("pcai042.informatik.uni-leipzig.de", 1831, 2, ks,this);
        client.execute();

        if (client.getServerStatus()) {
            Toast.makeText(this,
             "Der Server ist offline, versuche es später wieder!", Toast.LENGTH_LONG).show();
            client.setServerStatus(false);
        }
      /*  while(schleife == 1) {
             //um auf Antwort des Servers zu warten?
        }
        if (isinDB) {

            //*Create File to safe ID*//*
            BufferedWriter bw = null;
            try {
                File f = new File(getCacheDir(),"logindata.tmp");
                bw = new BufferedWriter(new FileWriter(f));
                bw.write("v"+nutzer);
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            */
            setContentView(R.layout.organizer_main_activity);
            initNavigationMenu2();
      /*} else {
            Toast.makeText(this,"Email nicht in DB.", Toast.LENGTH_LONG).show();
            client.setServerStatus(false);
        }*/
    }

    /**
     * disables the Back-Button to avoid disconnection
     */
    @Override
    public void onBackPressed() {
        //Pseudocode:
        /**if (!shouldAllowBack()) {
         doSomething();
         } else {
         super.onBackPressed();
         }*/
    }


    /**
     * This method enables a menu, and that the menu items are clickable
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
     * This method sets up the menu (as a DrawerLayout)
     * and enabled the navigation within the menu, handing off to the individual fragments.
     */
    public void initNavigationMenu2() {

        //setting up the menu bar
        drawerLayout2 = findViewById(R.id.drawerLayout2);
        toggle = new ActionBarDrawerToggle(this, drawerLayout2, R.string.open, R.string.close);

        drawerLayout2.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //forwarding is regulated here...
        fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.add(R.id.menuContainer2, new OrganizerMain());
        fragmentTransaction2.commit();
        getSupportActionBar().setTitle("Hauptseite");
        navigationView2 = findViewById(R.id.navigation_view2);
        navigationView2.setNavigationItemSelectedListener(new NavigationView
                                                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Hauptseite:
                        fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.menuContainer2, new OrganizerMain());
                        fragmentTransaction2.commit();
                        getSupportActionBar().setTitle("Hauptseite");
                        drawerLayout2.closeDrawers();
                        break;

                    case R.id.Kommende_Veranstaltungen:
                        fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.menuContainer2, new OrganizerNextEvents());
                        fragmentTransaction2.commit();
                        getSupportActionBar().setTitle("kommende Veranstaltungen");
                        drawerLayout2.closeDrawers();
                        break;

                    case R.id.Historie:
                        fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.menuContainer2, new OrganizerLastEvents());
                        fragmentTransaction2.commit();
                        getSupportActionBar().setTitle("vergangene Veranstaltungen");
                        drawerLayout2.closeDrawers();
                        break;

                    case R.id.Einstellungen:
                        fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.menuContainer2, new Settings());
                        fragmentTransaction2.commit();
                        getSupportActionBar().setTitle("Einstellungen");
                        drawerLayout2.closeDrawers();
                        break;

                    case R.id.Scanner:
                        Intent intent = new Intent(OrganizerLogIn.this,OcrCaptureActivity.class);
                        startActivity(intent);
                        getSupportActionBar().setTitle("Scanner");
                        drawerLayout2.closeDrawers();
                        break;

                    case R.id.Log_Out:
                        fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.menuContainer2, new LogOut());
                        fragmentTransaction2.commit();
                        getSupportActionBar().setTitle("Abmelden");
                        drawerLayout2.closeDrawers();
                        break;

                }
                return true;
            }
        });
    }
}