import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.omg.CORBA_2_3.portable.InputStream;


public class ChuckCommander extends Thread{
	
	String path = "/home/mike/Documents/workspace/Kinectacorn/shreds/";
	String test;
	String command;
	public ChuckCommander(String command){
		this.command = command;
	}
	
	void copy(java.io.InputStream inputStream, PrintStream out) throws IOException{
		while(true){
			int c = inputStream.read();
			if(c == -1) break;
			out.write((char)c);
		}
		
	}
	
    public void run() {
    	try {
			if(Driver.DEBUG)System.out.println(command);
			Process p1 = Runtime.getRuntime().exec(command);
			copy(p1.getInputStream(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String test = command;
    }

    public static void main(String args[]) {
        //(new ChuckCommander("bash /media/data/kinect/Kinectacorn/startChuck.sh")).start();
        //(new ChuckCommander("chuck + /home/mike/Downloads/chuck-1.3.5.0/examples/otf_06.ck")).start();
    }

	
}