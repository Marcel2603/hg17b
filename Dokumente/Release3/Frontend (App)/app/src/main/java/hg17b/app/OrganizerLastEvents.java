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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * this class lists the last events of an organizer
 */
public class OrganizerLastEvents extends Fragment {

    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10;
    Button btnBack, btnNext;
    public static boolean getVeranstalter;
    public static int anzahl;
    public static ArrayList<String> list = new ArrayList<String>();
    private int Zaehler = 0;

    public OrganizerLastEvents() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_fragment_last_events, container, false);

        btnBack = view.findViewById(R.id.buttonZurück);
        btnNext = view.findViewById(R.id.buttonWeiter);

        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        tv3 = view.findViewById(R.id.tv3);
        tv4 = view.findViewById(R.id.tv4);
        tv5 = view.findViewById(R.id.tv5);
        tv6 = view.findViewById(R.id.tv6);
        tv7 = view.findViewById(R.id.tv7);
        tv8 = view.findViewById(R.id.tv8);
        tv9 = view.findViewById(R.id.tv9);
        tv10 = view.findViewById(R.id.tv10);
        setList();

        Back();
        Next();
        return view;

    }
    public void setList(){
        if(list.size() == 0){
            tv1.setText(" ");
            tv2.setText(" ");
            tv3.setText(" ");
            tv4.setText(" ");
            tv6.setText(" ");
            tv7.setText(" ");
            tv8.setText(" ");
            tv9.setText(" ");
            tv10.setText(" ");
        }else {
            tv1.setText(list.get(0));
            tv2.setText(list.get(1));
            tv3.setText(list.get(2));
            tv4.setText(list.get(3));
            tv5.setText(list.get(4));
            tv6.setText(list.get(5));
            tv7.setText(list.get(6));
            tv8.setText(list.get(7));
            tv9.setText(list.get(8));
            tv10.setText(list.get(9));
        }
    }
    public void Back() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Zaehler == 0) {
                    Toast.makeText(getActivity(),
                            "Du bist auf der ersten Seite!", Toast.LENGTH_LONG).show();
                }else {
                    Zaehler -= 10;
                    tv1.setText(list.get(Zaehler));
                    tv2.setText(list.get(Zaehler+1));
                    tv3.setText(list.get(Zaehler+2));
                    tv4.setText(list.get(Zaehler+3));
                    tv5.setText(list.get(Zaehler+4));
                    tv6.setText(list.get(Zaehler+5));
                    tv7.setText(list.get(Zaehler+6));
                    tv8.setText(list.get(Zaehler+7));
                    tv9.setText(list.get(Zaehler+8));
                    tv10.setText(list.get(Zaehler+9));
                }
            }
        });

    }
    public void Next() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (list.size() < 10) {
                    Toast.makeText(getActivity(),
                            "Du bist auf der einzigsten Seite!", Toast.LENGTH_LONG).show();
                } else {
                    Zaehler += 10;
                    if (Zaehler < list.size()) {
                        tv1.setText(list.get(Zaehler));
                    } else {
                        Zaehler -= 10;
                        Toast.makeText(getActivity(),
                                "Du bist auf der letzten Seite!", Toast.LENGTH_LONG).show();
                    }
                    if (Zaehler + 1 < list.size()) {
                        tv2.setText((list.get(Zaehler + 1)));
                    } else {
                        tv2.setText(" ");
                    }
                    if (Zaehler + 2 < list.size()) {
                        tv3.setText((list.get(Zaehler + 2)));
                    } else {
                        tv3.setText(" ");
                    }
                    if (Zaehler + 3 < list.size()) {
                        tv4.setText((list.get(Zaehler + 3)));
                    } else {
                        tv4.setText(" ");
                    }
                    if (Zaehler + 4 < list.size()) {
                        tv5.setText((list.get(Zaehler + 4)));
                    } else {
                        tv5.setText(" ");
                    }
                    if (Zaehler + 5 < list.size()) {
                        tv6.setText((list.get(Zaehler + 5)));
                    } else {
                        tv6.setText(" ");
                    }
                    if (Zaehler + 6 < list.size()) {
                        tv7.setText((list.get(Zaehler + 6)));
                    } else {
                        tv7.setText(" ");
                    }
                    if (Zaehler + 7 < list.size()) {
                        tv8.setText((list.get(Zaehler + 7)));
                    } else {
                        tv8.setText(" ");
                    }
                    if (Zaehler + 8 < list.size()) {
                        tv9.setText((list.get(Zaehler + 8)));
                    } else {
                        tv9.setText(" ");
                    }
                    if (Zaehler + 9 < list.size()) {
                        tv10.setText((list.get(Zaehler + 9)));
                    } else {
                        tv10.setText(" ");
                    }
                }
            }
        });
    }



}
