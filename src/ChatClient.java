import java.io.IOException;
import java.net.UnknownHostException;
import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.swing.*;

public class ChatClient {
	
	static JFrame menu = new JFrame("Chat App");
	static JTextArea chatField = new JTextArea(22, 40);
	static JTextField textField = new JTextField(40);
	static JLabel blankLabel = new JLabel("      ");
	static JButton sendButton = new JButton("Send");
	static JLabel userName = new JLabel("               ");
	static BufferedReader in;
	static PrintWriter out;

	ChatClient() {
		
		menu.setLayout(new FlowLayout());
		menu.add(userName);
		menu.add(new JScrollPane(chatField));
		menu.add(blankLabel);
		menu.add(textField);
		menu.add(sendButton);
		
		menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menu.setSize(475, 500);
		menu.setVisible(true);
		
		textField.setEditable(false);
		chatField.setEditable(false);
		
		//Listens for when user clicks button
		sendButton.addActionListener(new Listener());
		//Listens for when user clicks Enter
		textField.addActionListener(new Listener());
		
	}
	
	//Takes input from the users
	void startChat() throws Exception {
		
		//Requires user to input there ip address to allow for connection identification
		//Returns value entered by client
		String ipAddress = JOptionPane.showInputDialog(
				menu,
				"Enter Server IP Address:",
				"IP Address Required!",
				JOptionPane.PLAIN_MESSAGE);
		
		Socket sock = new Socket(ipAddress, 8000);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new PrintWriter(sock.getOutputStream(), true);
		
		
		//Takes the user input and sends it to the server
		while (true) 
		{
			
			String str = in.readLine();
			if (str.equals("Name is Required!")) {
				String user = JOptionPane.showInputDialog(
						menu,
						"Enter a unique Name:",
						"Name Required!",
						JOptionPane.PLAIN_MESSAGE);
				
				out.println(user);
			} else if(str.equals("Sorry, this name has been taken!")) {
				
				String user = JOptionPane.showInputDialog(
						menu,
						"Enter another Name:",
						"Name Already Exists!",
						JOptionPane.WARNING_MESSAGE);
				
				out.println(user);
				
			} else if(str.startsWith("YOURNAME")) {
				
				textField.setEditable(true);
				
				//Ignores string YOURNAME and isolates userName only
				userName.setText("You are logged in as: " + str.substring(8));
				
			} else {
				
				chatField.append(str + "\n");
				
			}
			
		}
	
	}
	
	public static void main(String[] args) {
		
		try {
			ChatClient client = new ChatClient();
			client.startChat();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
}

//Action Listener interface
class Listener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Sends data to client output stream
		ChatClient.out.println(ChatClient.textField.getText());
		ChatClient.textField.setText("");
		
	}
	
}


