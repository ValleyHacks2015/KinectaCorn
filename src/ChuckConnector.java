import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder;


public class ChuckConnector {
	
	
	static String starter[] = {"chuck", "--loop"};
	//String path = "/usr/share/doc/chuck/examples/"; //TODO UPDATE THIS
	String path = "/media/data/kinect/Kinectacorn/shreds/";
		
	
	//Starts the initial loop for ChucK
	public void start() throws IOException {
		ProcessBuilder startChuck = new ProcessBuilder("chuck", "--loop");
		try {
			startChuck.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public void add(String input) {
		/*
		ProcessBuilder addShred = new ProcessBuilder("chuck", "+", path + input);
		try {
			addShred.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		String s = "chuck + " + path + input;
		(new ChuckCommander(s)).start();
	}
	
	
	
	public void remove(String input) {
		//TODO add integer argument to remove specific shred
		(new ChuckCommander("chuck --remove")).start();
	}
	
	public void halt() {
		//TODO does not do what is expected!
		(new ChuckCommander("chuck --halt")).start();
	}
	
	public void killChuck() {
		(new ChuckCommander("chuck --kill")).start();
	}
	
	public void removeAllShreds() {
		(new ChuckCommander("chuck --removeall")).start();
	}
	
	public static void main(String args[]) {
		
	}
}
