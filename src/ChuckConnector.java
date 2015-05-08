import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder;


public class ChuckConnector {
	
	
	String ip = "localhost";


	//String path = "/media/data/kinect/Kinectacorn/shreds/";
	
	String path = "/home/mike/Documents/workspace/Kinectacorn/shreds/";
	//String path ="/ext_storage/Kinectacorn/shreds/";	
	
	//Starts the initial loop for ChucK
	public void start() throws IOException {
		String s = "chuck --loop";
		(new ChuckCommander(s)).start();
	}
		
	public void add(String input) {
		String s = "chuck " + ip + " + " + path + input;
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
	
	
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static void main(String args[]) {
		ChuckConnector ck = new ChuckConnector();
		
			ck.add("bass_hit.ck");
		
	}
	
	
}
