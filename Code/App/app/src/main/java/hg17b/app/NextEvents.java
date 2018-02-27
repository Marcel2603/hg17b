/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass,
 * that lists the next Events a Pupil can visit
 */
public class NextEvents extends Fragment {

    /**
     * public constructor from this class
     */
    public NextEvents() {
        // Required empty public constructor
    }

    /**
     * onCreateView creates and display the Layout
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View that is displayed
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_next_events, container, false);
    }

}
