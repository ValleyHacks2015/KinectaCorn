import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class PatchMap {
	boolean DEBUG = true;
	int userID;
	private String lastGesture;
	ChuckConnector chuckConnector;
	//ArrayList<GestureName> gestureList = new ArrayList<GestureName>();
	//ArrayList<String> shredList; //TODO initialize <---
	HashMap<GestureName, String> gestureToShred = new HashMap<GestureName, String>();
	/*
	gestureList.add();
	
	for(GestureName g: gestureList){
		
	}
	*/
	
	public PatchMap(int userID)
	{
		this.userID = userID;
		chuckConnector = new ChuckConnector();
		loadDefaultMapping(userID);
		if(DEBUG) System.out.println("Loading default mapping");
	}
	
	
	
	private void loadDefaultMapping(int userID) {
		//TODO add defaults to rethinkDB
		if(userID == 1){
			gestureToShred.put(GestureName.RH_UP, "lead.ck");
			gestureToShred.put(GestureName.RH_OUT, "break_lead.ck");
			gestureToShred.put(GestureName.LH_UP, "pad.ck");
			gestureToShred.put(GestureName.LEAN_LEFT, "harmony.ck");
			gestureToShred.put(GestureName.TURN_LEFT, "Dyno-limit.ck");
		} else if(userID == 2){
			gestureToShred.put(GestureName.RH_UP, "drum_beat.ck");
			gestureToShred.put(GestureName.RH_OUT, "drum_break.ck");
			gestureToShred.put(GestureName.LH_UP, "bass_hit_trip.ck");
			gestureToShred.put(GestureName.LEAN_RIGHT, "harmony.ck");
		} else if(userID == 3) {
			gestureToShred.put(GestureName.RH_OUT, "LiSa-munger2.ck");
			gestureToShred.put(GestureName.LEAN_FWD, "powerup.ck");
			gestureToShred.put(GestureName.LH_UP, "pad2.ck");
			
		}
		
	}



	public static void main (String[] args)
	{
		//start chuck server
		//(new ChuckCommander("bash /media/data/kinect/Kinectacorn/startChuck.sh")).start();
		PatchMap p = new PatchMap(1);
		//p.searchGestures(GestureName.RH_DOWN);
	}
	
	
	/**
	 * Send shred to chuck connecter to be played
	 * @param shredName 
	 */
	public void sendShred(String shredName){
		chuckConnector.add(shredName);
		
		
	}
	
	
	/**
	 * Add new mapping: gesture to shred relationship
	 * @param gName
	 * @param shredName
	 * 
	 * 
	 */
	public void addMap(GestureName gName, String shredName){
		// add relation to hashmap
		gestureToShred.put(gName, shredName);
		//gestureList.add(gName);
		//shredList.add(shredName);
		
		//add gName to gestureList
		//add shredName to shredList
		
	}
	
	public String checkGestures(GestureName gName){
		//search gestureList
		lastGesture = gName.toString();
		if (gestureToShred.containsKey(gName))
		{
			String shredName = gestureToShred.get(gName);
			return shredName;
			
			
		}
		return null;
		
		
	}
	
	public void addShred(String shredName){
		if (shredName != null)
		{
			sendShred(shredName);	
		}
	}
	
	public String getLastGesture(){
		return lastGesture;
	}
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}

	
	
}
