package hg17b.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogOut extends Fragment {

    private static final String TAG = "LogOut";
    public static Button btnLogOut;
    public static boolean isclicked = false;

    public LogOut() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_log_out, container, false);
        btnLogOut = view.findViewById(R.id.btnLogOut);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isclicked = true;
                System.out.println(isclicked);
                Toast.makeText(getActivity(), "Du hast dich abgemeldet", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(getActivity(), StartActivity.class);

                startActivity(intent);

            }
        });
        return view;
    }



}
