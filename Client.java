import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
 
public class Client {
 
 
    public static void main(String arg[]){
        //Creating a SocketClient object
        Socket socketClient;
        BufferedReader stdIn ;
        String userInput;
                 
        try {
            socketClient = new Socket("127.0.01",9993);
             
                stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                while ((userInput = stdIn.readLine()) != null) {
                    System.out.println(userInput);
                }
             
        } catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } catch (IOException e) {
            System.err.println("Cannot establish connection. Server may not be up."+e.getMessage());
        }
    }
}