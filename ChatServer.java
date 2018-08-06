import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

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

	public static void main(String[] args) {

		try {

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
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "PLEASE NOTE: Server Terminated, messages will no longer send!");
		}

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
			userObjectOutput.writeObject(ChatServer.userArray);
			userObjectOutput.flush();
			ChatServer.buffWriter.add(out);
			userBuffer ub = new userBuffer(user, out);
			ChatServer.userBuffers.add(ub);
						
			for (BufferedWriter writer : ChatServer.buffWriter) {
				writer.write("update" + ChatServer.userNames);
				writer.newLine();
				writer.flush();
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
					for (userBuffer bu: ChatServer.userBuffers) {
						if (bu.getName().equals(privUser)) {
							bu.getWriter().write(user + ": "+ message);
							bu.getWriter().newLine();
							bu.getWriter().flush();
						}
						if (bu.getName().equals(user)) {
							bu.getWriter().write(user+ ": "+ message);
							bu.getWriter().newLine();
							bu.getWriter().flush();
						}
					}
				} else {
					msgLogs.println(user + ": " + message);
					for (BufferedWriter writer : ChatServer.buffWriter) {
						writer.write(user + ": " + message);
						writer.newLine();
						writer.flush();
					}
				}
			}
		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "Connection Terminated by User: " + user + "\n");

			// Remove disconnected user from ArrayList
			for (User toRemove : ChatServer.userArray) {
				if (toRemove.name == user && ChatServer.userArray.size() > 0) {
					(ChatServer.userArray).remove(toRemove);
					(ChatClient.userArray).remove(toRemove);
					ChatServer.userNames.remove(user);
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
			System.out.println("Connection Terminated by User: " + user);
		}
	}

}
