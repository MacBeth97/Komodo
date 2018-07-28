import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class ChatServer {
	
	//Stores UserNames of all clients connected to server
	static ArrayList<String> userNames = new ArrayList<String>();
	
	//Assigns PrintWriters to each user that connects to the server, preventing server from freezing each time a user sends text
	//PrintWriter objects of all the clients
	static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();

	public static void main(String[] args) {
		
		try {
			
			System.out.println("Waiting for client ...");
			ServerSocket ss = new ServerSocket(8000);
			
			while(true) 
			{
				Socket sock = ss.accept();
				System.out.println("Connection Established");
				ConversationHandler handler = new ConversationHandler(sock);
				handler.start();
			}

			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
}

class ConversationHandler extends Thread {
	Socket sockySock;
	BufferedReader in;
	PrintWriter out;
	String user;
	
	public ConversationHandler(Socket sockySock) throws IOException {
		this.sockySock = sockySock;
	}
	
	//Contains Thread Logic
	public void run() {
		
		try 
		{
			in = new BufferedReader(new InputStreamReader(sockySock.getInputStream()));
			out = new PrintWriter(sockySock.getOutputStream(), true);
			
			int count = 0;
			//Will wait for a user to enter a unique name
			while (true) {
				if (count > 0) {
					out.println("Sorry, this name has been taken!");
				} else {
					out.println("Name is Required!");
				}
				
				user = in.readLine();
				
				if (user == null) {
					return;
				}
				
				//If name doesn't exist, add userName to the ArrayList
				if (!ChatServer.userNames.contains(user)) {
					ChatServer.userNames.add(user);
					break;
				}
				count++;
			}
			
			out.println("Congrats you have a Name");
			ChatServer.printWriters.add(out);
			
			//Reads message from a client and sends to all other clients
			while (true) {
				String message = in.readLine();
				
				if (message == null) {
					return;
				}
				
				for (PrintWriter writer : ChatServer.printWriters) {
					//This sends the message
					writer.println(user + ": " + message);
				}
			}
			
		}
		catch (Exception e) 
		{
			System.out.println(e);
		}
	}
}
