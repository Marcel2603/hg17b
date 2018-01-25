package hg17b.app;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class StartActivity extends AppCompatActivity {

    public EditText etID;
    TextView tvInfo;
    public static String data;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    Button logOut;
    TestClient client;
    public static boolean isinDB;
    public static boolean isclicked;
    public static int points;
    public static int kontrolle;


    protected void onCreate(Bundle savedInstanceState) {
            logOut = (Button) findViewById(R.id.btnLogOut);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.start_activity);
            isinDB = false;
            etID = findViewById(R.id.etID);
            tvInfo = findViewById(R.id.tvInfo);
            kontrolle = 0;
            client = new TestClient("pcai042.informatik.uni-leipzig.de", 1831);
            client.execute();



    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //übergang zum nächsten Menü + senden der Daten an den Server
    public void buttonOnClick(View v) {
        isinDB = false;
        isclicked = true;

        kontrolle = 0;
        //Überprüfen ob Eingabe im Format passt
       data = etID.getText().toString();
       if(data.length()>2){
           Toast.makeText(this,
                   "Deine Eingabe war zu lang\n Maximal 6 Stellen erlaubt", Toast.LENGTH_LONG).show();
       }else{

           while(!isinDB && kontrolle == 0){
               //Schleife
               System.out.println("JFIFJIAFJWAJIF" + kontrolle);
           }
           if(isinDB) {
                setContentView(R.layout.main_activity);
                initNavigationMenu();
            }else{
                Toast.makeText(this,
                        "Deine ID ist nicht in der Datenbank", Toast.LENGTH_LONG).show();
                kontrolle = 0;

            }
       }




            /*
            if (etID.getText().toString().isEmpty()) {
                data = "000001";
            } else {
                data = etID.getText().toString();
                System.out.println("data");
            }
            tvInfo.setTextSize(18);
            tvInfo.setText(data + " war deine letzte Eingabe,\ndiese ID ist zu lang...");

            if (Integer.parseInt(data) > 999999) {
                Toast.makeText(this,
                        "Deine Eingabe war zu lang\n Maximal 6 Stellen erlaubt", Toast.LENGTH_LONG).show();
            }
        }

        if(Integer.parseInt(data) <= 999999){
            setContentView(R.layout.main_activity);
            initNavigationMenu();
        }*/

    }

    //Funktion für den Toast zur Veranstalterseite
    public void txtOnClick(View v){
        Toast.makeText(this, "Seite nicht verfügbar", Toast.LENGTH_SHORT).show();
    }

    //sendet ID an Server zur überprüfung

    public static void setText(int s){
        points = s;
    }

    //empfängt Daten vom Server/DB
    public void getData(){

    }

    /**
     *Funktion zum erstellen des Menüs(als DrawerLayout)
     *und zum navigieren im Menü(weiterleitung auf die einzelnen Fragmente...)
     */
    public void initNavigationMenu(){

        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Weiterleitung wird hier geregelt...
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

