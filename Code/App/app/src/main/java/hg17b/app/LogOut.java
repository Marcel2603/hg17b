/**
 * This Package contains the required Java Classes to build the Application
 */
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
 * A simple {@link Fragment} subclass,
 * that handles the Log Out and disconnection from the Server
 */
public class LogOut extends Fragment {

    private static final String TAG = "LogOut";
    public static Button btnLogOut;
    public static boolean isclicked = false;

    /**
     * public constructor from this class
     */
    public LogOut() {
        // Required empty public constructor
    }

    /**
     * onCreateView creates and displays the Layout.
     * If the Button is clicked the App will go to the log in page,
     * and starts the StartActivity again. ID is reset then.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View that is displayed
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_log_out, container, false);

        btnLogOut = view.findViewById(R.id.btnLogOut);
        onClickLogOut();

        return view;
    }

    /**
     * This method gets active when the Button is hit.
     * It enables the Disconnection in 'Client' and loads
     * the StartActivity (with the Log In screen)
     */
    private void onClickLogOut(){
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isclicked = true;
                // System.out.println(isclicked);

                Toast.makeText(getActivity(), "Du hast dich abgemeldet", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), StartActivity.class);
                startActivity(intent);
            }
        });
    }
}
