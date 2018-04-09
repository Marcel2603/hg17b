/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONException;

public class EventOrganizer extends Fragment {

    TextView tv1, tv2, tv3;

    /**
     * public constructor
     */
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

    /**
     * This method writes the details of an event (time / description)
     * in the intended TextViews and displays them
     */
    public void named() {
        try {
            if (StartActivity.past) {
                int index = StartActivity.index;
                tv1.setText(OrganizerLastEvents.list.getJSONObject(index)
                        .getString("start"));
                tv2.setText("Anzahl der Anmeldungen");
                tv3.setText(OrganizerLastEvents.list.getJSONObject(index)
                        .getString("description"));
            } else {
                int index = StartActivity.index;
                tv1.setText(OrganizerNextEvents.list.getJSONObject(index)
                        .getString("start"));
                tv2.setText("Anzahl der Anmeldungen");
                tv3.setText(OrganizerNextEvents.list.getJSONObject(index)
                        .getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}