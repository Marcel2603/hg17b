package hg17b.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventDetails extends AppCompatActivity {

    TextView tv1, tv2, tv3;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);

        tv1 = findViewById(R.id.tvTime1);
        tv2 = findViewById(R.id.tvTime2);
        tv3 = findViewById(R.id.tvDetail);
        register = findViewById(R.id.buttonRegister);
    }
}


    /**
     *  zum Laden der Seite:
     *  Intent intent = new Intent(getActivity(), EventDetails.class);
     *  startActivity(intent);
     *  + Daten in die Activity Übergeben...
     */



