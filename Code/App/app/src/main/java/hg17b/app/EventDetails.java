package hg17b.app;

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

import org.json.JSONException;

public class EventDetails extends Fragment {

    TextView tv1, tv2, tv3;
    Button register;
    public EventDetails(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        super.onCreate(savedInstanceState);

        tv1 = view.findViewById(R.id.tvTime1);
        tv2 = view.findViewById(R.id.tvTime2);
        tv3 = view.findViewById(R.id.tvDetail);
        register = view.findViewById(R.id.buttonRegister);
        named();
        return view;
    }
    public void named(){
        try {
            tv1.setText(LastEvents.list.getJSONObject(LastEvents.getZaehler()).getString("start"));
            tv2.setText("");
            tv3.setText(LastEvents.list.getJSONObject(LastEvents.getZaehler()).getString("description"));
        } catch (JSONException e){
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



