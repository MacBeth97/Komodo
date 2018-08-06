import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.swing.*;

//update chat clien tarray in while
public class ChatClient {

	static BufferedReader in;
	static BufferedWriter out;
	static ObjectInputStream userObjectInput;
	static ObjectOutputStream userObjectOutput;

	static ArrayList<User> userArray = new ArrayList<User>();
	static User newUser;
	static String user;
	static privateChat[] whispers = new privateChat[50];
	public static void main(String[] args) {
		try {
			Display menu = new Display();
			menu.setVisible(true);
			startTheChat();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "PLEASE NOTE: Server Terminated, messages will no longer send!");
		}
	}
	

	public static void startTheChat() throws Exception {
		// Requires user to input there ip address to allow for connection
		// identification
		// Returns value entered by client
		String ipAddress = JOptionPane.showInputDialog(null, "Enter Server IP Address:", "IP Address Required!",
				JOptionPane.PLAIN_MESSAGE);

		Socket sock = new Socket(ipAddress, 8000);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		userObjectOutput = new ObjectOutputStream(sock.getOutputStream());
		userObjectInput = new ObjectInputStream(sock.getInputStream());
		int index = 0;

		// Takes the user input and sends it to the server
		while (true) {
			String str = in.readLine();
			if (str.equals("Name is Required!")) {
				user = JOptionPane.showInputDialog(null, 
						"Enter a unique Name:", 
						"Name Required!",
						JOptionPane.PLAIN_MESSAGE);
				
				out.write(user);
				out.newLine();
				out.write(ipAddress);
				out.newLine();
				out.flush();

			} else if (str.equals("Sorry, this name has been taken!")) {

				user = JOptionPane.showInputDialog(null, 
						"Enter another Name:", 
						"Name Already Exists!",
						JOptionPane.WARNING_MESSAGE);

				out.write(user);
				out.newLine();
				out.write(ipAddress);
				out.newLine();
				out.flush();

			} else if (str.startsWith("YOURNAME")) {

				// Ignores string YOURNAME and isolates userName only
				Display.userListField.setText("");
				userArray = (ArrayList<User>) userObjectInput.readObject();
				
				Display.displayUsername.setText("You are logged in as: " + str.substring(8) + "\n");

			} else if (str.startsWith("update")) {
				String recvd = str.substring(6);
				String[] users = new String[50];
				users = recvd.split(",");
				Display.userListField.setText("");
				for (String name : users) {
					if (name.startsWith("[") && name.endsWith("]")) {
						Display.userListField.append(name.substring(1, name.length() - 1) + "\n");
					} else if (name.endsWith("]")) {
						Display.userListField.append(name.substring(0, name.length() - 1) + "\n");
					} else if (name.startsWith("[")){
						Display.userListField.append(name.substring(1) + "\n");
					} else {
						Display.userListField.append(name + "\n");
					}
				}

			} else if (str.startsWith("@")) {
				//open chat menu if doesn't already exist 
				//send msg so that it displays on both users
				// -> find user's chat menu in array 
				String privMsg = str.substring(1);
				String[] splitMsg = privMsg.split(":");
				String privUser = splitMsg[0];

				str = splitMsg[1];
				
				for (User toSend : ChatServer.userArray) {
					if (privUser.equals(toSend.name)) {
						String privIP = toSend.getIP();
						Socket temp = new Socket(privIP, 8000);
					}
				}
			}	else {
				Display.chatField.append(str + "\n");
			}

		}
	}

}