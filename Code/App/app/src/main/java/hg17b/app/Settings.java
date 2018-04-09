/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass,
 * to make some Settings and change user preferences
 */
public class Settings extends Fragment {

    Button btnColorGreen, btnColorRed, btnColorBlue, btnTextSize, btnOther;

    /**
     * public constructor from this class
     */
    public Settings() {
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        btnColorGreen = view.findViewById(R.id.buttonColorGreen);
        btnColorRed = view.findViewById(R.id.buttonColorRed);
        btnColorBlue = view.findViewById(R.id.buttonColorBlue);
        btnTextSize = view.findViewById(R.id.buttonTextSize);
        btnOther = view.findViewById(R.id.buttonOther);

        changeColor();
        changeTextSize();
        otherChange();

        return view;
    }

    /**
     * method that changes the color
     */
    private void changeColor() {

        btnColorGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ändern der Farbe
                Toast.makeText(getActivity(), "Grün", Toast.LENGTH_SHORT).show();
            }
        });

        btnColorRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ändern der Farbe
                Toast.makeText(getActivity(), "Rot", Toast.LENGTH_SHORT).show();
            }
        });

        btnColorBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ändern der Farbe
                Toast.makeText(getActivity(), "Blau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * method that changes the text sizes
     */
    public void changeTextSize() {
        btnTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        "Zur Zeit nicht verfügbar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * placeholder for other changes
     */
    public void otherChange() {
        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        "Zur Zeit nicht verfügbar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}