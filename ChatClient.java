import java.io.IOException;
import java.net.UnknownHostException;
import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.swing.*;

public class ChatClient {

	static BufferedReader in;
	static BufferedWriter out;
	static User[] userArray = new User[50];
	static String user;

	
//Takes input from the users
	public static void main(String[] args) {
		
		try {
			//ChatClient client = new ChatClient();
			//client.startChat();
			Display menu = new Display();
			menu.setVisible(true);
			//client.startChat();
			startTheChat();
		}
		catch (Exception e){
			JOptionPane.showMessageDialog(null, "PLEASE NOTE: Server Terminated, messages will no longer send!");
//			e.printStackTrace();
		}
		
	}
	
	public static void startTheChat() throws Exception {
		//Requires user to input there ip address to allow for connection identification
				//Returns value entered by client
				String ipAddress = JOptionPane.showInputDialog(
						null,
						"Enter Server IP Address:",
						"IP Address Required!",
						JOptionPane.PLAIN_MESSAGE);
				
				Socket sock = new Socket(ipAddress, 8000);
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				
				
				//Takes the user input and sends it to the server
				while (true) 
				{
					
					String str = in.readLine();
					if (str.equals("Name is Required!")) {
						 user = JOptionPane.showInputDialog(
								null,
								"Enter a unique Name:",
								"Name Required!",
								JOptionPane.PLAIN_MESSAGE);
						
						out.write(user);
						out.newLine();
						out.write(ipAddress);
						out.newLine();
						out.flush();
						
					} else if(str.equals("Sorry, this name has been taken!")) {
						
						 user = JOptionPane.showInputDialog(
								null,
								"Enter another Name:",
								"Name Already Exists!",
								JOptionPane.WARNING_MESSAGE);
						
						out.write(user);
						out.newLine();
						out.write(ipAddress);
						out.newLine();
						out.flush();
						
					} else if(str.startsWith("YOURNAME")) {
						
						//textField.setEditable(true);
						
						//Ignores string YOURNAME and isolates userName only
						Display.userListField.setText("");
						if (ChatServer.userArray.size() > 0) {
							System.out.println("Heroiin");
							for (User onlineUser: ChatServer.userArray) {
								String userToAdd = onlineUser.name;
								Display.userListField.append(userToAdd);
							}
						}
						Display.userListField.append(user);
						//Display.userListField.append(user);
						Display.displayUsername.setText("You are logged in as: " + str.substring(8) + "\n");
						//userName.setText("You are logged in as: " + str.substring(8));
						
					} else {
						
						Display.chatField.append(str + "\n");
						
					}
					
				}
		}
}