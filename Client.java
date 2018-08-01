import java.io.IOException;
import java.net.UnknownHostException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;




public class Client {
 
	public static void main(String[] args) {
		
		try 
		{
			System.out.println("Client Started");
			
			//Set to localhost when server and client are on the same machine
			Socket sock = new Socket("localhost" , 8000);
			
			//Reads data from the console
			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
			
			//Reads data from sockets input stream
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			//Sends data over socket
			PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
			
			int option, num1, num2 = 0;
			option = 0;
			num1 = 0;
			
			do {
				System.out.println("Choose an option");
				System.out.println("1, Addition");
				System.out.println("2, Subtraction");
				System.out.println("3, Multiplication");
				System.out.println("4, Division");
				System.out.println("5,Exit? Press the exit button then");
				System.out.println("Enter an option");
				
				//Convert input from string to an integer
				option = Integer.parseInt(userInput.readLine());
				
				if (option != 5) {
				
					System.out.println("Enter First Number");
					num1 = Integer.parseInt(userInput.readLine());
					
					System.out.println("Enter Second Number");
					num2 = Integer.parseInt(userInput.readLine());

					//The colon is a delimiter that allows the server to differentiate between values
					//Sends to server as a complete string
					out.println(option + ":" + num1 + ":" + num2);

				} else {
					out.println(option + ":0:0");
					break;
				}
				
				//Capture the result
				String answer = in.readLine();
				System.out.println("Server says: " + answer);
				System.out.println("");
				
				
			} while (true);
			System.out.println("Client terminated");
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
