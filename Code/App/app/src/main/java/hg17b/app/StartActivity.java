/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the Main Activity, and handles the Student part of the App.
 * The class StartActivity starts the Application and sets up the Layout.
 * In this class the connection to the server is established and
 * the menu is created. The ID is received from the User and checked
 * by the Server, afterwards data is received from there.
 */
public class StartActivity extends AppCompatActivity {

    public EditText etID;
    TextView tvInfo;
    public static String data;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    Button logOut;
    Client client;
    public static boolean isinDB;
    public static boolean isclicked;
    public static int points;
    public static int kontrolle;

    /**
     * The onCreate method is called when the Application gets started.
     * Here we initialize the Layout, start the Client and connect them with the Server.
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.start_activity);

            kontrolle = 0;
            isinDB = false;

            logOut = (Button) findViewById(R.id.btnLogOut);
            etID = findViewById(R.id.etID);
            tvInfo = findViewById(R.id.tvInfo);

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
        client = new Client("pcai042.informatik.uni-leipzig.de", 1831);
        client.execute();

        //Check if input fits into the TextField
       data = etID.getText().toString();
       if(data.length()>2){
           Toast.makeText(this,
                   "Deine Eingabe war zu lang\n Maximal 2 Stellen erlaubt", Toast.LENGTH_LONG).show();
       }else {
           while (!isinDB && kontrolle == 0 && !client.getServerStatus()) {
               //loop for testing
           }
           if (client.getServerStatus()) {
               Toast.makeText(this,
                       "Der Server ist offline, versuche es später wieder!", Toast.LENGTH_LONG).show();
               client.setServerStatus(false);
           } else {
               if (isinDB) {
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
     * Shows message if TextView tvOrganizer is clicked,
     * and starts the OrganizerLogIn Page
     * @param v
     */
    public void txtOnClick(View v){
        //Toast.makeText(this, "Veranstalter Seite", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(StartActivity.this, OrganizerLogIn.class);
            startActivity(intent);
    }

    /**
     * Sends ID to the Server
     * @param s
     */
    public static void setText(int s){
        points = s;
    }

    /**
     * This method sets up the menu (as a DrawerLayout)
     * and enabled the navigation within the menu, handing off to the individual fragments.
     */
    public void initNavigationMenu(){

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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
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

    public void onClick(View view){
        LogOut.isclicked = true;
    }
}

