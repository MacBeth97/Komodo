import java.io.BufferedWriter;

public class userBuffer {
	String name;
	BufferedWriter buffWriter;
	
	public userBuffer(String name, BufferedWriter buffWriter) {
		this.name = name;
		this.buffWriter = buffWriter;
	}
	
	public String getName() {
		return this.name;
	}
	
	public BufferedWriter getWriter() {
		return this.buffWriter;
	}
}
