import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ChatServer {

	static ArrayList<String> userNames = new ArrayList<String>();
	static ArrayList<User> userArray = new ArrayList<User>();
	static InetAddress IP;

	// Assigns PrintWriters to each user that connects to the server, preventing
	// server from freezing each time a user sends text
	// PrintWriter objects of all the clients
	static ArrayList<BufferedWriter> buffWriter = new ArrayList<BufferedWriter>();
	static ArrayList<userBuffer> userBuffers = new ArrayList<userBuffer>();
	public static String userToRemove;
	public static boolean someoneHasExited;

	public static void main(String[] args) {

		try {
			//someoneHasExited = false;
			System.out.println("Waiting for client ...");
			ServerSocket ss = new ServerSocket(8000);
			
			while (true) {
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

		} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "PLEASE NOTE: Server Terminated, messages will no longer send!");
			
		}

	}
	
	public static void updateSomeoneHasExited(Boolean bool) {
		ChatServer.someoneHasExited = bool;
//		System.out.println("Updated: " + someoneHasExited);
	}
	
	public static Boolean getSomeoneHasExited() {
		return ChatServer.someoneHasExited;
	}
	public static void removeUser(String user) {
		String actualUser = user.substring(22);
		
		ChatServer.someoneHasExited = true;
		
		// Remove disconnected user from ArrayList
		for (User toRemove : ChatServer.userArray) {
			System.out.println("IN FOR: " + toRemove.name);
			if (toRemove.name.equals(actualUser)) {
				(ChatServer.userArray).remove(toRemove);
				(ChatClient.userArray).remove(toRemove);
				ChatServer.userNames.remove(actualUser);
				break;
			}
		}
		
		for (userBuffer userBuff: ChatServer.userBuffers) {
			if (userBuff.getName().equals(actualUser)) {
				ChatServer.userBuffers.remove(userBuff);
				break;
			}
		}
		
		if (ChatServer.userArray.size() >= 1) {
			System.out.println("Users still connected: ");
			for (User usersLeft : ChatServer.userArray) {
				System.out.println(usersLeft.name);
				System.out.println("=======================");
			}
		}
		JOptionPane.showMessageDialog(null, "Connection Terminated by User: " + actualUser + "\n");
		ChatServer.someoneHasExited = true;
		System.out.println("Connection Terminated by User: " + actualUser);
	}

}

class ConversationHandler extends Thread {
	Socket sockySock;
	BufferedReader in;
	BufferedWriter out;
	ObjectInputStream userObjectInput;
	ObjectOutputStream userObjectOutput;
	String user;
	String ip;
	User newUser;
	User toAdd;
	PrintWriter msgLogs;
	static FileWriter fw;
	static BufferedWriter bw;
	static Boolean privUserExists = false;


	public ConversationHandler(Socket sockySock) throws IOException {
		this.sockySock = sockySock;
		fw = new FileWriter("./ChatServer-logs.txt", true);
		bw = new BufferedWriter(fw);
		msgLogs = new PrintWriter(bw, true);
	}

	// Contains Thread Logic
	public void run() {

		try {
			in = new BufferedReader(new InputStreamReader(sockySock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sockySock.getOutputStream()));
			userObjectInput = new ObjectInputStream(sockySock.getInputStream());
			userObjectOutput = new ObjectOutputStream(sockySock.getOutputStream());

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

				// If name doesn't exist, add userName to the ArrayList
				if (!ChatServer.userNames.contains(user)) {
					toAdd = new User(user, ip);
					ChatServer.userArray.add(toAdd);
					ChatServer.userNames.add(user);
					break;
				}
				count++;
			}
			// Send name from server to client
			out.write("YOURNAME" + user);
			out.newLine();
			out.flush();
			ChatServer.buffWriter.add(out);
			userBuffer ub = new userBuffer(user, out);
			ChatServer.userBuffers.add(ub);
						
			for (userBuffer ubu : ChatServer.userBuffers) {
				ubu.getWriter().write("update" + ChatServer.userNames);
				ubu.getWriter().newLine();
				ubu.getWriter().flush();
			}

			// Reads message from a client and sends to all other clients
			while (true) {
				String message = in.readLine();

				if (message == null) {
					return;
				}

				if (message.startsWith("@")) {
					String privMsg = message.substring(1);
					String[] splitMsg = privMsg.split(":");
					String privUser = splitMsg[0];

					message = splitMsg[1];
					msgLogs.println(privUser + ": " + message);

					//send to both users...
					privUserExists = false;
					for (userBuffer bu: ChatServer.userBuffers) {
						if (bu.getName().equals(privUser)) {
							privUserExists = true;
							bu.getWriter().write("PM from " + user + ": "+ message);
							bu.getWriter().newLine();
							bu.getWriter().flush();
						}
						if (bu.getName().equals(user)) {
							if (privUserExists == true) {
								bu.getWriter().write("PM to " + privUser+ ": "+ message);
								bu.getWriter().newLine();
								bu.getWriter().flush();
							} else {
								JOptionPane.showMessageDialog(null, "ALERT: User does not exist");
							}
							
						}
					}
				} else if(message.startsWith("&")) {
					//System.out.println("IN SERVER: " + message.substring(1, message.length()));
					String actualUser = message.substring(1, message.length()).trim();
					for (User toRemove : ChatServer.userArray) {
						//System.out.println("IN FOR: " + toRemove.getName());
						//System.out.println("acut " + actualUser);
						String newName = toRemove.getName().trim();
						if (newName.equals(actualUser)){
							ChatServer.userArray.remove(toRemove);
							(ChatClient.userArray).remove(toRemove);
							ChatServer.userNames.remove(actualUser);
							//System.out.println("REMOVED");
							break;
						}
					}
					
					for (userBuffer userBuff: ChatServer.userBuffers) {
						if (userBuff.getName().trim().equals(actualUser)) {
							ChatServer.userBuffers.remove(userBuff);
							//System.out.println("Removed buffer");
							break;
						}
					}
					for (userBuffer ubu : ChatServer.userBuffers) {
						ubu.getWriter().write("update" + ChatServer.userNames);
						ubu.getWriter().newLine();
						ubu.getWriter().flush();
					}
				
				} else {
					msgLogs.println(user + ": " + message);
				
					
					for (userBuffer userBuffinski: ChatServer.userBuffers) {
						try {
							userBuffinski.getWriter().write(user + ": " + message);
							userBuffinski.getWriter().newLine();
							userBuffinski.getWriter().flush();
						} catch (Exception e) {

						}
					}
				}
			}
		} catch (Exception e) {
			
		}
	}
}
