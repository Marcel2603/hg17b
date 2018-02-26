package hg17b.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * This class shows upcoming events for an organizer that is logged in.
 */
public class OrganizerNextEvents extends Fragment {


    public OrganizerNextEvents() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.organizer_fragment_next_events, container, false);
    }

}
