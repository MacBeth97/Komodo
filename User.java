import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	String name;
	String ipAddress; 
	
	public User(String name, String ipAddress) {
		this.name = name;
		this.ipAddress = ipAddress;
	}
	
	public String getIP() {
		return this.ipAddress;
	}
	
//	public privateChat getMenu() {
//		
//	}
}

