/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;


public class EventDetails extends Fragment {

    TextView tv1, tv2, tv3;
    Button register;

    /**
     * public constructor
     */
    public EventDetails() {
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
        registerClick();
        return view;
    }

    /**
     * This method writes the details of an event (time / description)
     * in the intended TextViews and displays them
     */
    public void named() {
        try {
            if (StartActivity.past) {
                int index = StartActivity.index;
                tv1.setText(LastEvents.list.getJSONObject(index).getString("start"));
                tv2.setText("");
                tv3.setText(LastEvents.list.getJSONObject(index).getString("description"));
            } else {
                int index = StartActivity.index;
                tv1.setText(NextEvents.list.getJSONObject(index).getString("start"));
                tv2.setText("");
                tv3.setText(NextEvents.list.getJSONObject(index).getString("description"));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * This method gets active when the Button is hit.
     * The Button Text & Color gets changed and the organizer
     * receives information about the number of participants.
     */
    private void registerClick() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (register.getText().equals("anmelden")) {
                    Toast.makeText(getActivity(),
                            "Du hast dich angemeldet", Toast.LENGTH_SHORT).show();
                    register.setText("abmelden");
                    register.setBackgroundColor(getResources().getColor(R.color.colorAccent2));

                } else if (register.getText().equals("abmelden")) {
                    Toast.makeText(getActivity(),
                            "Du hast dich abgemeldet", Toast.LENGTH_SHORT).show();
                    register.setText("anmelden");
                    register.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                } else {
                }
                //speichern der Anzahl der Anmeldungen für Veranstalter einbinden
            }
        });
    }
}
