import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder;


public class ChuckConnector {
	
	
	static String ip = "@192.168.44.13";
	//String path = "/usr/share/doc/chuck/examples/"; //TODO UPDATE THIS


	//String path = "/media/data/kinect/Kinectacorn/shreds/";
	
	String path = "/home/mike/Documents/workspace/Kinectacorn/shreds/";
	//String path ="/ext_storage/Kinectacorn/shreds/";	
	
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
		String s = "chuck " + ip + path + input;
		(new ChuckCommander(s)).start();
	}
	
	
	
	public void remove(String input) {
		//TODO add integer argument to remove specific shred
		(new ChuckCommander("chuck "+ ip + " --remove")).start();
	}
	
	public void halt() {
		//TODO does not do what is expected!
		(new ChuckCommander("chuck " + ip + " --halt")).start();
	}
	
	public void killChuck() {
		(new ChuckCommander("chuck " + ip + " --kill")).start();
	}
	
	public void removeAllShreds() {
		(new ChuckCommander("chuck " + ip + " --removeall")).start();
	}
	
	public static void main(String args[]) {
		
	}
}
