/**
 * This Package contains the required Java Classes to build the Application
 */
package hg17b.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * A simple {@link Fragment} subclass,
 * that lists the next Events a Pupil can visit
 */
public class NextEvents extends Fragment {

    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10;
    Button btnBack, btnNext, refreshbutton;
    public static JSONArray list = new JSONArray();
    private int Zaehler = 0;

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
        View view = inflater.inflate(R.layout.fragment_next_events, container, false);
        btnBack = view.findViewById(R.id.buttonZurück);
        btnNext = view.findViewById(R.id.buttonWeiter);
        refreshbutton = view.findViewById(R.id.refreshbutton);

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
        refresh();
        return view;
    }
    public void setList(){
        if(list.length() == 0){
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
            try {
                tv1.setText(list.getJSONObject(0).getString("label"));

                tv2.setText(list.getJSONObject(1).getString("label"));
                tv3.setText(list.getJSONObject(2).getString("label"));
                tv4.setText(list.getJSONObject(3).getString("label"));
                tv5.setText(list.getJSONObject(4).getString("label"));
                tv6.setText(list.getJSONObject(5).getString("label"));
                tv7.setText(list.getJSONObject(6).getString("label"));
                tv8.setText(list.getJSONObject(7).getString("label"));
                tv9.setText(list.getJSONObject(8).getString("label"));
                tv10.setText(list.getJSONObject(9).getString("label"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                    try {
                        tv1.setText(list.getJSONObject(Zaehler).getString("label"));

                        tv2.setText(list.getJSONObject(Zaehler+1).getString("label"));
                        tv3.setText(list.getJSONObject(Zaehler+2).getString("label"));
                        tv4.setText(list.getJSONObject(Zaehler+3).getString("label"));
                        tv5.setText(list.getJSONObject(Zaehler+4).getString("label"));
                        tv6.setText(list.getJSONObject(Zaehler+5).getString("label"));
                        tv7.setText(list.getJSONObject(Zaehler+6).getString("label"));
                        tv8.setText(list.getJSONObject(Zaehler+7).getString("label"));
                        tv9.setText(list.getJSONObject(Zaehler+8).getString("label"));
                        tv10.setText(list.getJSONObject(Zaehler+9).getString("label"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    public void Next() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (list.length() < 10) {
                    Toast.makeText(getActivity(),
                            "Du bist auf der einzigsten Seite!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Zaehler += 10;
                        if (Zaehler < list.length()) {

                            tv1.setText(list.getJSONObject(Zaehler).getString("label"));

                        } else {
                            Zaehler -= 10;
                            Toast.makeText(getActivity(),
                                    "Du bist auf der letzten Seite!", Toast.LENGTH_LONG).show();
                        }
                        if (Zaehler + 1 < list.length()) {
                            tv2.setText((list.getJSONObject(Zaehler + 1).getString("label")));
                        } else {
                            tv2.setText(" ");
                        }
                        if (Zaehler + 2 < list.length()) {
                            tv3.setText((list.getJSONObject(Zaehler + 2).getString("label")));
                        } else {
                            tv3.setText(" ");
                        }
                        if (Zaehler + 3 < list.length()) {
                            tv4.setText((list.getJSONObject(Zaehler + 3).getString("label")));
                        } else {
                            tv4.setText(" ");
                        }
                        if (Zaehler + 4 < list.length()) {
                            tv5.setText((list.getJSONObject(Zaehler + 4).getString("label")));
                        } else {
                            tv5.setText(" ");
                        }
                        if (Zaehler + 5 < list.length()) {
                            tv6.setText((list.getJSONObject(Zaehler + 5).getString("label")));
                        } else {
                            tv6.setText(" ");
                        }
                        if (Zaehler + 6 < list.length()) {
                            tv7.setText((list.getJSONObject(Zaehler + 6).getString("label")));
                        } else {
                            tv7.setText(" ");
                        }
                        if (Zaehler + 7 < list.length()) {
                            tv8.setText((list.getJSONObject(Zaehler + 7).getString("label")));
                        } else {
                            tv8.setText(" ");
                        }
                        if (Zaehler + 8 < list.length()) {
                            tv9.setText((list.getJSONObject(Zaehler + 8).getString("label")));
                        } else {
                            tv9.setText(" ");
                        }
                        if (Zaehler + 9 < list.length()) {
                            tv10.setText((list.getJSONObject(Zaehler + 9).getString("label")));
                        } else {
                            tv10.setText(" ");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public int getZaehler(){
        return Zaehler;
    }
    public JSONObject getObject(int index){
        try {
            return list.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a listener for the refresh-button, which will receive new Eventdata from Server.
     */
    public void refresh(){
        refreshbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("refreshing");
            StartActivity.client.refreshEvents=1;
            }
        });
    }

    /**
     * Übergang zu den Details...
     */
    public void isClicked(){
        Intent intent = new Intent(getActivity(), EventDetails.class);
        startActivity(intent);
    }
}
