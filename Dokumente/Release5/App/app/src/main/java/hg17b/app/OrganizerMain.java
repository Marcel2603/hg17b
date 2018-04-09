/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * this class is the main page for the organizer.
 */
public class OrganizerMain extends Fragment {

    public static boolean veranstalter;
    public TextView tvEvents;

    /**
     * public constructor from this class
     */
    public OrganizerMain() {
        // Required empty public constructor
    }

    /**
     * onCreateView creates and display the Layout,
     * later the Events could be loaded here...
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        veranstalter = true;

        View view = inflater.inflate(R.layout.organizer_fragment_main, container, false);
        tvEvents = view.findViewById(R.id.tvEvents);

        //tvEvents.setText("nächstes Event");

        return view;
    }
}