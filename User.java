
public class User {
	String name;
	String ipAddress; 
	
	public User(String name, String ipAddress) {
		this.name = name;
		this.ipAddress = ipAddress;
	}
	
	public String getIP(User user) {
		return user.ipAddress;
	}
}

