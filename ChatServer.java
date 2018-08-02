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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ChatServer {
	
	//Stores UserNames of all clients connected to server
	static ArrayList<String> userNames = new ArrayList<String>();
	//static User[] userArray = new User[50];
	static ArrayList<User> userArray = new ArrayList<User>();
	static InetAddress IP;
	
	//Assigns PrintWriters to each user that connects to the server, preventing server from freezing each time a user sends text
	//PrintWriter objects of all the clients
	static ArrayList<BufferedWriter> buffWriter = new ArrayList<BufferedWriter>();

	public static void main(String[] args) {
		
		try {
			
			System.out.println("Waiting for client ...");
			ServerSocket ss = new ServerSocket(8000);
			
			while(true) 
			{
				Socket sock = ss.accept();
				System.out.println("Connection Established");
				
				for (User line : userArray) {
					System.out.println("Currently connected users are: ");
					System.out.println(line.name);
					System.out.println("===============================");
				}
				
				ConversationHandler handler = new ConversationHandler(sock);
				handler.start();
			}

			
		}
		catch (Exception e){
			JOptionPane.showMessageDialog(null, "PLEASE NOTE: Server Terminated, messages will no longer send!");
//			e.printStackTrace();
		}
		
	}
	
}

class ConversationHandler extends Thread {
	Socket sockySock;
	BufferedReader in;
	//Writes data to sockets output stream
	BufferedWriter out;
	String user;
	String ip;
	//Writes data to message log file
	PrintWriter msgLogs;
	//Writes data character by character to file
	static FileWriter fw;
	static BufferedWriter bw;
	
	public ConversationHandler(Socket sockySock) throws IOException {
		this.sockySock = sockySock;

		fw = new FileWriter("./ChatServer-logs.txt", true);
		bw = new BufferedWriter(fw);
		msgLogs = new PrintWriter(bw, true);
	}
	
	//Contains Thread Logic
	public void run() {
		
		try 
		{
			in = new BufferedReader(new InputStreamReader(sockySock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sockySock.getOutputStream()));
			
			int count = 0;
			
			while (true) {
				if (count > 0) {
					out.write("Sorry, this name has been taken!\n");
					out.flush();
				} else {
					out.write("Name is Required!\n");
					out.flush();
				}
				
				user = in.readLine();
				ip = in.readLine();
				
				if (user == null) {
					return;
				}
				
				//If name doesn't exist, add userName to the ArrayList
				if (!ChatServer.userNames.contains(user)) {
					User toAdd = new User(user, ip);				
					ChatServer.userArray.add(toAdd);
					ChatServer.userNames.add(user);
					break;
				}
				count++;
			}
			
			//Send name from server to client
			out.write("YOURNAME" + user);
			ChatServer.buffWriter.add(out);
			
			//Reads message from a client and sends to all other clients
			//System.out.println("Before send message loop");
			while (true) {
				String message = in.readLine();
				if (message == null) {
					return;
				}
				
				msgLogs.println(user + ": " + message);
				
				for (BufferedWriter writer : ChatServer.buffWriter) {
					//This sends the message
					writer.write(user + ": " + message  +"\n");
				}
			}			
			
		}
		catch (Exception e) 
		{
			
			JOptionPane.showMessageDialog(null, "Connection Terminated by User: " + user + "\n");
			
			//Remove disconnected user from ArrayList

			for (User toRemove: ChatServer.userArray) {
				if (toRemove.name == user && ChatServer.userArray.size() > 1) {
					(ChatServer.userArray).remove(toRemove);
					//ChatServer.userNames.remove(user);
					break;
				}
			}
			//ChatServer.userArray.remove(ChatServer.userArray.get());

			//(ChatServer.userNames).remove(toRemove);		

//			System.out.println("Users still connected: ");
//			for (User usersLeft : ChatServer.userArray) {
//				System.out.println(usersLeft.name);
//				System.out.println("=======================");
//			}
			System.out.println("Connection Terminated by User: " + user);
		}
	}
}
