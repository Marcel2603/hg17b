package hg17b.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * this class is the main page for the organizer.
 */
public class OrganizerMain extends Fragment {
    public static boolean veranstalter;
    /**
     * public constructor from this class
     */
    public OrganizerMain() {
        // Required empty public constructor
    }

    /**
     * onCreateView creates and display the Layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        veranstalter = true;








        View view = inflater.inflate(R.layout.organizer_fragment_main, container, false);


        return view;
    }
}
