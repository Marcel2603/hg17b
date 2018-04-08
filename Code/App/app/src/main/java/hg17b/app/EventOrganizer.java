package hg17b.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;

public class EventOrganizer extends Fragment {

    TextView tv1, tv2, tv3;

    public EventOrganizer() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_organizer, container, false);
        super.onCreate(savedInstanceState);

        tv1 = view.findViewById(R.id.tvTime1);
        tv2 = view.findViewById(R.id.User);
        tv3 = view.findViewById(R.id.tvDetail);
        named();

        return view;
    }

    public void named() {
        try {
            if (StartActivity.past) {
                int index = StartActivity.index;
                tv1.setText(OrganizerLastEvents.list.getJSONObject(index).getString("start"));
                tv2.setText("Anzahl der Anmeldungen");
                tv3.setText(OrganizerLastEvents.list.getJSONObject(index).getString("description"));
            } else {
                int index = StartActivity.index;
                tv1.setText(OrganizerNextEvents.list.getJSONObject(index).getString("start"));
                tv2.setText("Anzahl der Anmeldungen");
                tv3.setText(OrganizerNextEvents.list.getJSONObject(index).getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}


/**
 *  zum Laden der Seite:
 *  Intent intent = new Intent(getActivity(), EventDetails.class);
 *  startActivity(intent);
 *  + Daten in die Activity Übergeben...
 */

