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
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Ranking extends Fragment {

    public TextView text1,text2,text3,text4,text5,text6,text7,text8,text9,text10,text11;
    public static HashMap<Integer, String> score = new HashMap<Integer, String>();
    public static String rang;

    /**
     * public constructor from this class
     */
    public Ranking() {
        // Required empty public constructor
    }

    /**
     * onCreateView creates and display the Layout.
     * The Ranking is received from the Server and the TextViews getting created.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View that is displayed
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        // Inflate the layout for this fragment

        text1 = view.findViewById(R.id.textView1);
        text1.setText(score.get(1));
        text2 = view.findViewById(R.id.textView3);
        text2.setText(score.get(2));
        text3 = view.findViewById(R.id.textView5);
        text3.setText(score.get(3));
        text4 = view.findViewById(R.id.textView7);
        text4.setText(score.get(4));
        text5 = view.findViewById(R.id.textView9);
        text5.setText(score.get(5));
        text6 = view.findViewById(R.id.textView11);
        text6.setText(score.get(6));
        text7 = view.findViewById(R.id.textView13);
        text7.setText(score.get(7));
        text8 = view.findViewById(R.id.textView15);
        text8.setText(score.get(8));
        text9 = view.findViewById(R.id.textView17);
        text9.setText(score.get(9));
        text10 = view.findViewById(R.id.textView19);
        text10.setText(score.get(10));
        text11 = view.findViewById(R.id.textView20);
        text11.setText("Du bist aktuell auf Platz: " + rang);

        return view;
    }

}
