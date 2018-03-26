import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class ServerSSL extends Thread {
    private int port;
    SSLSocket socket;
    
    public ServerSSL(int port){
        this.port=port;
    }

    public void run(){
        System.setProperty("javax.net.ssl.keyStore", "trustStore");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        try {
            SSLServerSocket serverSocket = (SSLServerSocket) SSLServerSocketFactory
                    .getDefault().createServerSocket(5678);
            System.out.println("Server ready..." + serverSocket);

            System.out
                    .println("Supported Cipher Suites: "
                            + Arrays
                                    .toString(((SSLServerSocketFactory) SSLServerSocketFactory
                                            .getDefault())
                                            .getSupportedCipherSuites()));

           socket = (SSLSocket) serverSocket.accept();
            SSLSession sslSession = socket.getSession();
            String cipherSuite = sslSession.getCipherSuite();
            System.out.println(cipherSuite);

            Scanner scanner = new Scanner(socket.getInputStream());
            System.out.println("Reading...");
            while (scanner.hasNextLine()) {
                System.out.println("Server received: " + scanner.nextLine());
            }
            scanner.close();
            socket.close();
           

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            
        }
    }
}