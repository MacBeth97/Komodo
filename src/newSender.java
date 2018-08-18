import java.net.*;

import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class newSender {
	public static BufferedReader in;
	public static BufferedWriter out;
	public static senderGUI sender;
	public static Socket sock;
	public static String notDone = "1";
	public static String protocol;

	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(8000);
			sender = new senderGUI();
			sender.setVisible(true);
			
			while (true) {
				sock = ss.accept();
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				System.out.println("Connection Established...");


				while (notDone.equals("1")) {
					protocol = in.readLine();

					if (protocol.startsWith("TCP")) {
						System.out.println("TCP Selected");
						notDone = "0";
					} else if (protocol.startsWith("RBUDP")) {
						System.out.println("RBUDP Selected");
						notDone = "0";
					} else {
						System.out.println("Incorrect Input");
					}
					out.write(notDone);
					out.newLine();
					out.flush();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void Button(ActionEvent e, senderGUI gui) throws IOException {
		String filePath = gui.getTCPName();
		if (protocol.equals("TCP")) {
			TCPthread TCPhandler = new TCPthread(sock, filePath);
			TCPhandler.start();
		} else {
			
		}

	}

}

class TCPthread extends Thread {
	private Socket sock;
	public String path;
	public static String fileName;
	public static BufferedWriter out;

	public TCPthread(Socket sock, String path) throws IOException {
		this.sock = sock;
		this.path = path;
	}

	// Contains Thread Logic
	public void run() {

		try {
			
			fileName = getNameFromPath(path);
			System.out.println("File Name: " + fileName);

			String extension = getFileType(fileName);
			System.out.println("File Type: " + extension);
			
			System.out.println("HRE");
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			System.out.println("EHERHEH");
			
			while(true) {
				out.write(fileName);
				out.newLine();
				out.flush();
				out.write(extension);
				out.newLine();
				out.flush();
				
				FileInputStream f = new FileInputStream(fileName);
				byte[] b = new byte[2000];

				// Starts reading from 0 to end of file
				// Stores data into b
				f.read(b, 0, b.length);

				// Convert file to stream
				OutputStream out = sock.getOutputStream();
				// Sends variable b
				out.write(b, 0, b.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getNameFromPath(String path) {
		String[] splitPath = path.split("/");
		String fileName = splitPath[splitPath.length - 1];
		return fileName;
	}
	
	public static String getFileType(String name) {
		String[] fileType = fileName.split("\\.");
		String extension = fileType[fileType.length - 1];
		return extension;
	}
}
