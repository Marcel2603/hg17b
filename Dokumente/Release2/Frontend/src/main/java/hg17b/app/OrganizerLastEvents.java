package hg17b.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * this class lists the last events of an organizer
 */
public class OrganizerLastEvents extends Fragment {

    public static boolean getVeranstalter;
    public static int anzahl;

    public OrganizerLastEvents() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.organizer_fragment_last_events, container, false);
    }


}
