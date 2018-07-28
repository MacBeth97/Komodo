import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class Server {

	public static void main(String[] args) {
		
		try 
		{
		System.out.println("Server started");
		System.out.println("Waiting for client ...");
		ServerSocket ss = new ServerSocket(8000);
		
		//Socket Object
		Socket sock = ss.accept();
		System.out.println("Established Connection");
		
		//Reads data from the input stream (Client)
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		
		//Sends data to output stream, doesn't store anything in the buffer
		PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
		
		while (true) {
			//Splits the input by detecting the delimiter specified
			String str[] = in.readLine().split(":");
			
			int option = Integer.parseInt(str[0]);
			int num1 = Integer.parseInt(str[1]);
			int num2 = Integer.parseInt(str[2]);
			
			//Sends result back to client 
			String result = "";
			
			int flag = 0;
			switch(option)
			{
			case 1: result = "Addition is: " + (num1 + num2);
					break;
			case 2: result = "Subtraction is: " + (num1 - num2);
					break;
			case 3: result = "Multiplication is: " + (num1 * num2);
					break;
			case 4: result = "Division is: " + (num1 / num2);
					break;
			case 5: flag = 1;
					break;
			default: break;
			}
			
			//Checks whether or not to terminate program
			if (flag == 1) {
				break;
			}
			out.println(result);
				
				
		}
		
		System.out.println("Server terminated");
		
			
		
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}
