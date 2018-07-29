import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ChatServer {
	
	//Stores UserNames of all clients connected to server
	static ArrayList<String> userNames = new ArrayList<String>();
	static InetAddress IP;
	
	//Assigns PrintWriters to each user that connects to the server, preventing server from freezing each time a user sends text
	//PrintWriter objects of all the clients
	static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();

	public static void main(String[] args) {
		
		try {
			
			System.out.println("Waiting for client ...");
			ServerSocket ss = new ServerSocket(8000);
			IP = InetAddress.getLocalHost();
			System.out.println("IP of my system is := "+IP.getHostAddress());
			
			while(true) 
			{
				Socket sock = ss.accept();
				System.out.println("Connection Established");
				ConversationHandler handler = new ConversationHandler(sock);
				handler.start();
			}

			
		}
		catch (Exception e){
//			System.out.println("Connection Terminated");
			e.printStackTrace();
		}
		
	}
	
}

class ConversationHandler extends Thread {
	Socket sockySock;
	BufferedReader in;
	//Writes data to sockets output stream
	PrintWriter out;
	String user;
	//Writes data to message log file
	PrintWriter msgLogs;
	//Writes data character by character to file
	static FileWriter fw;
	static BufferedWriter bw;
	
	public ConversationHandler(Socket sockySock) throws IOException {
		this.sockySock = sockySock;

		fw = new FileWriter("C:\\Users\\Bruce\\Desktop\\Komodo\\ChatServer-logs.txt", true);
		bw = new BufferedWriter(fw);
		msgLogs = new PrintWriter(bw, true);
	}
	
	//Contains Thread Logic
	public void run() {
		
		try 
		{
			in = new BufferedReader(new InputStreamReader(sockySock.getInputStream()));
			out = new PrintWriter(sockySock.getOutputStream(), true);
			
			int count = 0;
			
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
			
			//Send name from server to client
			//Appends name to accepted string
			out.println("YOURNAME" + user);
			ChatServer.printWriters.add(out);
			
			//Reads message from a client and sends to all other clients
			while (true) {
				String message = in.readLine();
				
				if (message == null) {
					return;
				}
				
				msgLogs.println(user + ": " + message);
				
				for (PrintWriter writer : ChatServer.printWriters) {
					//This sends the message
					writer.println(user + ": " + message);
				}
			}
			
		}
		catch (Exception e) 
		{
			
			JOptionPane.showMessageDialog(null, "Connection Terminated by User: " + user);
			
//			out.println(alert);
			
			System.out.println("Connection Terminated by User: " + user);
//			System.out.println(e);
		}
	}
}
