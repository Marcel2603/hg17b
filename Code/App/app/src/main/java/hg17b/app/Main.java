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
 * A simple {@link Fragment} subclass.
 */
public class Main extends Fragment {

    public TextView tvID, tvPoints, tvEvents;
    StartActivity s = new StartActivity();
    /**
     * public constructor from this class
     */
    public Main() {
        // Required empty public constructor
    }

    /**
     * onCreateView creates and display the Layout.
     * The ID and the associated points are received, and will be displayed
     * in the TextViews.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View that is displayed
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        tvID = view.findViewById(R.id.tvID);
        tvPoints = view.findViewById(R.id.tvPoints);
        tvEvents = view.findViewById(R.id.tvEvents);

        tvID.setText("Deine ID lautet: \n\n" + StartActivity.data + "\n");
        tvPoints.setText("Deine punkte: \n\n" + s.getPoints() + "\n");

        return view;
    }

}
