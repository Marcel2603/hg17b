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

    public Main() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        tvID = view.findViewById(R.id.tvID);
        tvPoints = view.findViewById(R.id.tvPoints);
        tvEvents = view.findViewById(R.id.tvEvents);

        tvID.setText("Deine ID lautet: \n\n" + StartActivity.data + "\n");

        return view;
    }

}
