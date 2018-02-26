package hg17b.app;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

/**
 * this class creates the login page for organizers and initialises the organizer main menu
 */
public class OrganizerLogIn extends AppCompatActivity {


    private DrawerLayout drawerLayout2;
    private ActionBarDrawerToggle toggle;
    FragmentTransaction fragmentTransaction2;
    NavigationView navigationView2;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_log_in);
    }

    public void buttonOnClick2(View v){

        //check if EMail & Password are correct

        setContentView(R.layout.organizer_main_activity);
        initNavigationMenu2();
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
     * This method sets up the menu (as a DrawerLayout)
     * and enabled the navigation within the menu, handing off to the individual fragments.
     */
    public void initNavigationMenu2(){

        //setting up the menu bar
        drawerLayout2 = findViewById(R.id.drawerLayout2);
        toggle = new ActionBarDrawerToggle(this, drawerLayout2, R.string.open, R.string.close);

        drawerLayout2.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Forwarding is regulated here...
        fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.add(R.id.menuContainer2, new OrganizerMain());
        fragmentTransaction2.commit();
        getSupportActionBar().setTitle("Hauptseite");
        navigationView2 = findViewById(R.id.navigation_view2);
        navigationView2.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
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
                        // hier evtl. neues Fragment für Veranstalter Einstellungen
                        fragmentTransaction2.replace(R.id.menuContainer2, new Settings());
                        fragmentTransaction2.commit();
                        getSupportActionBar().setTitle("Einstellungen");
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
