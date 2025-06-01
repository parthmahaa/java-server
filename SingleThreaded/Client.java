import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public void run(){
        int port = 5000;

        try {
            InetAddress address = InetAddress.getByName("localhost");
            Socket socket = new Socket(address, port);
            PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            toSocket.println("Hello from the client");
            String socketResponse = fromSocket.readLine();
            System.err.println("Response from soceket:" + socketResponse);
            toSocket.close();
            fromSocket.close();
            socket.close();
        } catch (ConnectException ce) {
            System.err.println("Could not connect to server at localhost:" + port + ". Is the server running?");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }    

    public static void main(String[] args) {
        
        Client client =  new Client();
        client.run();
    }
}
