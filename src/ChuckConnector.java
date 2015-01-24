import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder;


public class ChuckConnector {
	
	static String starter[] = {"chuck", "--loop"};
	String path = "/usr/share/doc/chuck/examples/"; //TODO UPDATE THIS
		
	
	//Starts the initial loop for ChucK
	public static void start() throws IOException {
		ProcessBuilder startChuck = new ProcessBuilder("chuck", "--loop");
		try {
			startChuck.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public void add(String input) {
		ProcessBuilder addShred = new ProcessBuilder("chuck", "+", path + input);
	}
	
	public void remove(String input) {
		ProcessBuilder removeShred = new ProcessBuilder("chuck", "-", path + input);
	}
	
	public void halt() {
		ProcessBuilder haltShred = new ProcessBuilder("chuck", "halt");
	}
	
	public void killChuck() {
		ProcessBuilder kill = new ProcessBuilder("chuck", "kill");
	}
	
	public void removeAllShreds() {
		ProcessBuilder removeAll = new ProcessBuilder("chuck", "remove.all");
	}
	
	public static void main(String args[]) {
		
	}
}
