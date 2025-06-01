import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

public class Server
{
    
    public void  run (){
        int port = 5000;
        try {
            ServerSocket server = new ServerSocket(5000);
            server.setSoTimeout(10000);

            while (true) {
                System.err.println("Server is listening on port:" +port);

                //accept connection on the server
                Socket acceptConnection = server.accept();

                // print the connected client
                System.err.println("Connection accepted from Client:" +acceptConnection.getRemoteSocketAddress());

                PrintWriter toClient = new PrintWriter(acceptConnection.getOutputStream());
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(acceptConnection.getInputStream()));

                toClient.println("HELLO FROM SERVER");
                toClient.close();
                fromClient.close();
                acceptConnection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[])
    {
        Server server = new Server();

        try{
            server.run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}