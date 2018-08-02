import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {
    /**
     * Creates a SocketServer object and starts the server.
     *
     * @param args
     */
    public static void main(String[] args) {
        // Setting a default port number.
        ServerSocket serverSocket;
        Socket client ;
        BufferedWriter writer;
        BufferedReader in;
        try {
                serverSocket = new ServerSocket(9993);
                client = serverSocket.accept();
             
                writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                in = new BufferedReader(new InputStreamReader(System.in));
                String t = "";
                while ((t = in.readLine()) != null) {
                    writer.write(t);
                    writer.flush();
                }
                System.out.println(t);
                writer.write(t);
                writer.flush();
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}