import java.net.*;

import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class newReceiver {
	// Selection Process
	public static String notDone = "1";
	public static String protocol;
	public static receiverGUI gui;

	// TCP
	public static BufferedReader in;
	public static BufferedWriter out;
	public static Socket sock;

	// UDP
	public static DatagramSocket sockellinskiJunior = null;
	public static DatagramPacket inPacket = null; // Receiving
	public static DatagramPacket outPacket = null; // Sending
	public static int port = 8000;

	public static void main(String[] args) {

		try {
			String ipAddress = JOptionPane.showInputDialog(null, "Enter Server IP Address:", "IP Address Required!",
					JOptionPane.PLAIN_MESSAGE);
		    gui = new receiverGUI();
			gui.setVisible(true);
			sock = new Socket(ipAddress, port);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

			while (notDone.equals("1")) {
				System.out.println("OANIC");
				protocol = JOptionPane.showInputDialog(null, "Which Protocol: TCP or RBUDP", "Tranfer Protocol",
						JOptionPane.PLAIN_MESSAGE);

				out.write(protocol);
				out.newLine();
				out.flush();
				notDone = in.readLine();
			}
			String str;
			while (true) {
				str = in.readLine();
				System.out.println(str);
				if (str == null) {
					return;
				}
				String fileName = str;
				String extension = in.readLine();
				//System.out.println(str);
				if (protocol.equals("TCP")) {
					startTCP(fileName, extension);
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	public static void Button(ActionEvent e) throws IOException {
//		//String filePath = gui.getTCPName();
//		System.out.println("PT " + newSender.protocol);
//		if (newSender.protocol.equals("TCP")) {
//			startTCP(gui);
//			//TCPthread TCPhandler = new TCPthread(sock, filePath);
//			//TCPhandler.start();
//		} else {
//			
//		}
//
//	}
//	
	public static void startTCP(String name, String type) throws IOException {
		try {
			//String name = in.readLine();
			System.out.println("File Name: " + name);
			//String type = in.readLine();
			System.out.println("Type: " + type);
			
			String extension;
			switch (type) {
			case "txt":
				extension = "Text File";
				break;
			case "pdf":
				extension = "Portable Doc";
				break;
			case "docx":
				extension = "Word File";
				break;
			default:
				extension = "Unknown file type";
				break;
			}
			gui.TCPTextArea.append(extension + ": " + name);

			byte[] b = new byte[2000];
			
			Socket sock = new Socket("localhost", 8000);
			InputStream in = sock.getInputStream();
			
			FileOutputStream f = new FileOutputStream("./outTest.txt");
			in.read(b, 0, b.length);
			
			f.write(b, 0, b.length);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
