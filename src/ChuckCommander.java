import java.io.IOException;


public class ChuckCommander extends Thread{
	
	String command;
	public ChuckCommander(String command){
		this.command = command;
	}
	
    public void run() {
        ChuckConnector c = new ChuckConnector();
        c.add(command);
    }

    public static void main(String args[]) {
        (new ChuckCommander("bash /media/data/kinect/Kinectacorn/startChuck.sh")).start();
        (new ChuckCommander("chuck + /home/bhalpin/Downloads/chuck-1.3.5.0/examples/otf_06.ck")).start();
    }

	
}