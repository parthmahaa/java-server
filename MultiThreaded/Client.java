package MultiThreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8080;

        System.out.println("Attempting to connect to server at " + hostname + ":" + port);

        try (
            Socket socket = new Socket(hostname, port);
            PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Connection established. Sending HTTP GET request...");
            
            toSocket.println("Host: " + hostname);
            toSocket.println("Connection: close");
            toSocket.println(); 

            System.out.println("\n--- Server Response ---");
            String line;
            while ((line = fromSocket.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("--- End of Response ---");

        } catch (UnknownHostException e) {
            System.err.println("Host unknown: " + hostname);
        } catch (IOException e) {
            System.err.println("Client Error: Could not connect or communicate with the server. Is it running?");
        }
    }
}