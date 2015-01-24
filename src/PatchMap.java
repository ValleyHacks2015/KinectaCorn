import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class PatchMap {
	boolean DEBUG = true;
	int userID;
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
		}else if(userID == 2){
			gestureToShred.put(GestureName.RH_UP, "drum_beat.ck");
			gestureToShred.put(GestureName.RH_OUT, "drum_break.ck");
			
		}
		
		
	}



	public static void main (String[] args)
	{
		//start chuck server
		//(new ChuckCommander("bash /media/data/kinect/Kinectacorn/startChuck.sh")).start();
		PatchMap p = new PatchMap(1);
		p.searchGestures(GestureName.RH_DOWN);
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
	
	public boolean searchGestures(GestureName gName){
		//search gestureList
		if (gestureToShred.containsKey(gName))
		{
			String shredName = gestureToShred.get(gName);
			if (shredName != null)
			{
				sendShred(shredName);
			}
			
			return true;
		}
		return false;
		
		//if match found
		//query hashmap for shred to send
		//sendShred
		//else do nothing
	}
	
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}

	
	
}
