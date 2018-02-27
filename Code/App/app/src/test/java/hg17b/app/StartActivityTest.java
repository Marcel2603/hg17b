package hg17b.app;

import android.view.View;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MEDION on 27.02.2018.
 */
public class StartActivityTest {

    @Test
    public void onCreate() throws Exception {
        StartActivity startActivity = new StartActivity();
        int k = 0;

        assertEquals(k, StartActivity.kontrolle);
        assertEquals(false, StartActivity.isinDB);
    }

    @Test
    public void setText() throws Exception {

        StartActivity start = new StartActivity();
        start.setText(10);
        int points = 10;

        assertEquals(points, start.getPoints());
    }

    @Test
    public void onClick() throws Exception {

        StartActivity start = new StartActivity();
        start.onClick();
        //LogOut.isclicked = true

        assertEquals(true, LogOut.isclicked);
    }

}