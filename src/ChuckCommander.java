import java.io.IOException;


public class ChuckCommander extends Thread{
	
	String command;
	public ChuckCommander(String command){
		this.command = command;
	}
	
    public void run() {
    	try {
			System.out.println(command);
			Process p1 = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void main(String args[]) {
        (new ChuckCommander("bash /media/data/kinect/Kinectacorn/startChuck.sh")).start();
        (new ChuckCommander("chuck + /home/mike/Downloads/chuck-1.3.5.0/examples/otf_06.ck")).start();
    }

	
}