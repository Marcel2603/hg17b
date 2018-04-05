package hg17b.app;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by MR on 27.02.2018.
 */
public class ClientTest {
    @Test
    public void doInBackground() throws Exception {
        Client client = new Client("101", 1, 0,new KeyHandler(new File("")),new StartActivity());

        String ip = "101";
        int port = 1;
        int ent = 0;

        assertEquals(ip, client.getIp());
        assertEquals(port, client.getPort());
        assertEquals(ent, client.getEntscheidung());
    }

}